package cn.malgo.annotation.core.service.annotation;

import java.util.*;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import cn.malgo.annotation.common.dal.mapper.AnTermAnnotationMapper;
import cn.malgo.annotation.common.dal.model.AnTerm;
import cn.malgo.annotation.common.dal.model.AnTermAnnotation;
import cn.malgo.annotation.common.dal.sequence.CodeGenerateTypeEnum;
import cn.malgo.annotation.common.dal.sequence.SequenceGenerator;
import cn.malgo.annotation.common.service.integration.apiserver.ApiServerService;
import cn.malgo.annotation.common.service.integration.apiserver.result.AnnotationResult;
import cn.malgo.annotation.common.service.integration.apiserver.vo.TermTypeVO;
import cn.malgo.annotation.common.util.AssertUtil;
import cn.malgo.annotation.common.util.log.LogUtil;
import cn.malgo.annotation.core.model.annotation.TermAnnotationModel;
import cn.malgo.annotation.core.model.check.AnnotationChecker;
import cn.malgo.annotation.core.model.convert.AnnotationConvert;
import cn.malgo.annotation.core.model.enums.annotation.AnnotationStateEnum;
import cn.malgo.annotation.core.model.enums.term.TermStateEnum;
import cn.malgo.annotation.core.service.term.AtomicTermService;
import cn.malgo.annotation.core.service.term.TermService;
import cn.malgo.common.security.SecurityUtil;

/**
 *
 * @author 张钟
 * @date 2017/10/18
 */
@Service
public class AnnotationService {

    private Logger                 logger = Logger.getLogger(AnnotationService.class);

    @Autowired
    private SequenceGenerator      sequenceGenerator;

    @Autowired
    private AnTermAnnotationMapper anTermAnnotationMapper;

    @Autowired
    private ApiServerService       apiServerService;

    @Autowired
    private TermService            termService;

    @Autowired
    private AtomicTermService      atomicTermService;

    /**
     * 根据状态分页查询标注
     * @param
     * @return
     */
    public Page<AnTermAnnotation> queryOnePageThroughApiServer(String userId, int pageNum,
                                                               int pageSize) {
        Page<AnTermAnnotation> pageInfo = PageHelper.startPage(pageNum, pageSize);
        List<String> stateList = new ArrayList<>();
        stateList.add(AnnotationStateEnum.PROCESSING.name());
        stateList.add(AnnotationStateEnum.INIT.name());

        anTermAnnotationMapper.selectByStateListModifier(stateList, userId);
        decryptAES(pageInfo.getResult());
        apiServerService.batchPhraseUpdatePosWithNewTerm(pageInfo.getResult());
        for (AnTermAnnotation anTermAnnotation : pageInfo.getResult()) {
            updateFinalAnnotation(anTermAnnotation.getId(), anTermAnnotation.getFinalAnnotation());
        }
        return pageInfo;
    }

    public Page<AnTermAnnotation> queryOnePageDirectly(String annotationState, String userId,
                                                       int pageNum, int pageSize) {
        Page<AnTermAnnotation> pageInfo = PageHelper.startPage(pageNum, pageSize);
        anTermAnnotationMapper.selectByStateModifier(annotationState, userId);
        decryptAES(pageInfo.getResult());
        return pageInfo;
    }

    /**
     * 根据状态分页查询标注
     * @param
     * @return
     */
    public AnTermAnnotation queryByAnIdThroughApiServer(String anId) {

        AnTermAnnotation anTermAnnotation = anTermAnnotationMapper.selectByPrimaryKey(anId);
        decryptAES(anTermAnnotation);
        List<AnTermAnnotation> anTermAnnotationList = new ArrayList<>();
        anTermAnnotationList.add(anTermAnnotation);

        List<AnTermAnnotation> anTermAnnotationListNew = apiServerService
            .batchPhraseUpdatePosWithNewTerm(anTermAnnotationList);

        return anTermAnnotationListNew.get(0);
    }

    /**
     * 通过termId 来自动标注
     * 首次标注,主要用于定时任务的批处理
     * @param termId
     */
    public void autoAnnotationByTermId(String termId) {

        //通过调用apiServer服务,获取自动标注结果
        AnTerm anTerm = termService.queryByTermId(termId);
        String autoAnnotation = apiServerService.phraseTokenize(anTerm.getTerm());

        //检查是否存标注信息
        AnTermAnnotation anTermAnnotation = anTermAnnotationMapper.selectByTermId(termId);
        if (anTermAnnotation == null) {
            saveTermAnnotation(anTerm, autoAnnotation);
        } else {
            updateAutoAnnotation(anTermAnnotation.getId(), autoAnnotation);
        }
    }

    /**
     * 批量自动标注
     * @param termList
     */
    public void autoAnnotationByTermList(List<AnTerm> termList) {

        Map<String, String> termMap = new HashMap<>();
        for (AnTerm anTerm : termList) {
            termMap.put(anTerm.getId(), anTerm.getTerm());
        }

        List<AnnotationResult> annotationResultList = apiServerService
            .batchPhraseTokenize(termList);

        if (annotationResultList != null) {
            for (AnnotationResult annotationResult : annotationResultList) {
                saveTermAnnotation(annotationResult.getId(), termMap.get(annotationResult.getId()),
                    annotationResult.getAnnotation());
            }
        }

    }

    /**
     * 通过annotationId 来自动标注,此时需要根据用户标注的新词来调用apiServer的接口来处理
     * 用户手动标注后,调用apiServer,合成手动标注和自动标注
     * @param anId
     * @param manual
     * @param newTerms
     */
    public AnTermAnnotation autoAnnotationByAnId(String anId, String manual,
                                                 List<TermTypeVO> newTerms) {
        AnTermAnnotation anTermAnnotation = queryByAnId(anId);

        List<AnTermAnnotation> anTermAnnotationList = new ArrayList<>();

        String newTermsStr = TermTypeVO.convertToString(newTerms);
        anTermAnnotation.setNewTerms(newTermsStr);
        anTermAnnotation.setManualAnnotation(manual);
        anTermAnnotationList.add(anTermAnnotation);

        List<AnTermAnnotation> finalAnnotationList = apiServerService
            .batchPhraseUpdatePosWithNewTerm(anTermAnnotationList);

        updateManualAnnotation(anId, manual, newTermsStr,
            finalAnnotationList.get(0).getFinalAnnotation());

        AnTermAnnotation result = anTermAnnotationMapper.selectByPrimaryKey(anId);

        decryptAES(result);

        return result;

    }

    /**
     * 结束标注
     * @param anId
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void finishAnnotation(String anId) {
        AnTermAnnotation anTermAnnotationOld = queryByAnId(anId);

        //如果存在新词,保存新词到词库
        String newTermsStr = anTermAnnotationOld.getNewTerms();
        List<TermTypeVO> termTypeVOList = TermTypeVO.convertFromString(newTermsStr);
        for (TermTypeVO termTypeVO : termTypeVOList) {
            atomicTermService.saveAtomicTerm(anId, termTypeVO);
        }

        AnTermAnnotation anTermAnnotation = new AnTermAnnotation();
        anTermAnnotation.setId(anId);
        anTermAnnotation.setState(AnnotationStateEnum.FINISH.name());
        anTermAnnotationMapper.updateByPrimaryKeySelective(anTermAnnotation);
    }

    /**
     * 设置术语的状态为无法识别
     * @param anId
     */
    public void setUnRecognize(String anId) {
        AnTermAnnotation anTermAnnotation = new AnTermAnnotation();
        anTermAnnotation.setId(anId);
        anTermAnnotation.setState(AnnotationStateEnum.UN_RECOGNIZE.name());
        int updateResult = anTermAnnotationMapper.updateByPrimaryKeySelective(anTermAnnotation);

        AssertUtil.state(updateResult > 0, "设置术语状态为未识别异常");
    }

    /**
     * 分页查询标注,未解密
     * @param annotationState
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Page<AnTermAnnotation> queryOnePageUNEncrypted(String annotationState, String userId,
                                                          int pageNum, int pageSize) {
        Page<AnTermAnnotation> pageInfo = PageHelper.startPage(pageNum, pageSize);
        anTermAnnotationMapper.selectByStateModifier(annotationState, userId);
        return pageInfo;
    }

    /**
     * 更新标注
     */
    public void updateUNEncryptedAnnotation(String userId) {

        int batchCount = 1;
        Page<AnTermAnnotation> page = null;
        do {
            LogUtil.info(logger, "开始处理第" + batchCount + "批次");
            page = queryOnePageUNEncrypted(AnnotationStateEnum.UN_ENCRYPTED.name(), userId, 1, 10);
            AnTermAnnotation anTermAnnotation_temp = null;
            try {
                for (AnTermAnnotation anTermAnnotation : page.getResult()) {
                    anTermAnnotation_temp = anTermAnnotation;
                    anTermAnnotation.setAutoAnnotation(
                        SecurityUtil.cryptAESBase64(anTermAnnotation.getAutoAnnotation()));
                    anTermAnnotation.setFinalAnnotation(
                        SecurityUtil.cryptAESBase64(anTermAnnotation.getFinalAnnotation()));
                    anTermAnnotation.setManualAnnotation(
                        SecurityUtil.cryptAESBase64(anTermAnnotation.getManualAnnotation()));
                    anTermAnnotation.setState(AnnotationStateEnum.FINISH.name());
                    anTermAnnotationMapper.updateByPrimaryKeySelective(anTermAnnotation);
                }
            } catch (Exception e) {
                LogUtil.info(logger, "加密失败anId" + anTermAnnotation_temp.getId());
            }
            LogUtil.info(logger, "结束处理第" + batchCount + "批次,剩余:" + (page.getTotal() - 10));
            batchCount++;
        } while (page.getTotal() > 10);

    }

    /**
     * 保存标注,主要用于标注自动标注
     * @param anTerm
     * @param autoAnnotation
     */
    private void saveTermAnnotation(AnTerm anTerm, String autoAnnotation) {
        saveTermAnnotation(anTerm.getId(), anTerm.getTerm(), autoAnnotation);
    }

    /**
     * 保存标注,主要用于标注自动标注
     * @param termId
     * @param term
     * @param autoAnnotation
     */
    @Transactional(propagation = Propagation.REQUIRED)
    private void saveTermAnnotation(String termId, String term, String autoAnnotation) {

        String securityAnnotation = SecurityUtil.cryptAESBase64(autoAnnotation);

        String id = sequenceGenerator.nextCodeByType(CodeGenerateTypeEnum.DEFAULT);
        AnTermAnnotation anTermAnnotation = new AnTermAnnotation();
        anTermAnnotation.setId(id);
        anTermAnnotation.setTermId(termId);
        anTermAnnotation.setTerm(term);
        anTermAnnotation.setAutoAnnotation(securityAnnotation);
        anTermAnnotation.setFinalAnnotation(securityAnnotation);
        anTermAnnotation.setState(AnnotationStateEnum.INIT.name());

        int saveResult = anTermAnnotationMapper.insert(anTermAnnotation);
        AssertUtil.state(saveResult > 0, "保存自动标注失败");

        termService.updateTermState(termId, TermStateEnum.FINISH);

    }

    /**
     * 根据标注ID查询标注信息
     * @param id
     * @return
     */
    public AnTermAnnotation queryByAnId(String id) {
        AnTermAnnotation anTermAnnotation = anTermAnnotationMapper.selectByPrimaryKey(id);
        decryptAES(anTermAnnotation);
        return anTermAnnotation;
    }

    /**
     * 批量,全量检查标注的二义性
     */
    public void batchCheckAmbiguity() {

        LogUtil.info(logger, "开全量检查标注的二义性");
        int pageNum = 1;
        int pageSize = 10;
        Page<AnTermAnnotation> pageInfo = null;
        List<String> stateList = new ArrayList<>();
        stateList.add(AnnotationStateEnum.FINISH.name());
        do {

            pageInfo = PageHelper.startPage(pageNum, pageSize);
            anTermAnnotationMapper.selectByStateList(stateList);
            LogUtil.info(logger,
                "开始处理第" + pageNum + "批次,剩余" + (pageInfo.getPages() - pageNum) + "批次");

            for (AnTermAnnotation anTermAnnotation : pageInfo.getResult()) {
                try {
                    String text = SecurityUtil
                        .decryptAESBase64(anTermAnnotation.getFinalAnnotation());
                    List<TermAnnotationModel> result = hasAmbiguity(text);
                    if (result != null && result.size() > 0) {
                        //此时存在歧义,保存歧义数据到memo,并且设置状态为UN_RECOGNIZE
                        String memo = JSONArray.toJSONString(result);
                        LogUtil.info(logger,
                            "发现二义性标注,ID:" + anTermAnnotation.getId() + ",标注内容:" + memo);
                        AnTermAnnotation anTermAnnotationForUpdate = new AnTermAnnotation();
                        anTermAnnotationForUpdate.setId(anTermAnnotation.getId());
                        anTermAnnotationForUpdate.setMemo(memo);
                        anTermAnnotationForUpdate.setState(AnnotationStateEnum.UN_RECOGNIZE.name());
                        anTermAnnotationMapper
                            .updateByPrimaryKeySelective(anTermAnnotationForUpdate);
                    }
                } catch (Exception e) {
                    LogUtil.info(logger, "检查标注二义性异常,标注ID:" + anTermAnnotation.getId());
                }
            }
            LogUtil.info(logger,
                "结束处理第" + pageNum + "批次,剩余" + (pageInfo.getPages() - pageNum) + "批次");
            pageNum++;

        } while (pageInfo.getPages() >= pageNum);

    }

    /**
     * 更新自动标注
     * @param anId
     * @param autoAnnotation
     */
    private void updateAutoAnnotation(String anId, String autoAnnotation) {
        String securityAnnotation = SecurityUtil.cryptAESBase64(autoAnnotation);

        AnTermAnnotation anTermAnnotation = new AnTermAnnotation();
        anTermAnnotation.setId(anId);
        anTermAnnotation.setAutoAnnotation(securityAnnotation);
        anTermAnnotation.setFinalAnnotation(securityAnnotation);
        anTermAnnotation.setGmtModified(new Date());

        int updateResult = anTermAnnotationMapper.updateByPrimaryKeySelective(anTermAnnotation);
        AssertUtil.state(updateResult > 0, "更新自动标注失败");
    }

    /**
     * 更新最终标注
     * @param anId
     * @param finalAnnotation
     */
    private void updateFinalAnnotation(String anId, String finalAnnotation) {
        String securityFinalAnnotation = SecurityUtil.cryptAESBase64(finalAnnotation);

        AnTermAnnotation anTermAnnotation = new AnTermAnnotation();
        anTermAnnotation.setId(anId);
        anTermAnnotation.setFinalAnnotation(securityFinalAnnotation);
        anTermAnnotation.setGmtModified(new Date());

        int updateResult = anTermAnnotationMapper.updateByPrimaryKeySelective(anTermAnnotation);
        AssertUtil.state(updateResult > 0, "更新最终标注失败");
    }

    /**
     * 更新手工标注
     * @param anId
     * @param manualAnnotation
     * @param newTerms
     * @param finalAnnotation
     */
    private void updateManualAnnotation(String anId, String manualAnnotation, String newTerms,
                                        String finalAnnotation) {
        String securityManualAnnotation = SecurityUtil.cryptAESBase64(manualAnnotation);
        String securityFinalAnnotation = SecurityUtil.cryptAESBase64(finalAnnotation);

        AnTermAnnotation anTermAnnotation = new AnTermAnnotation();
        anTermAnnotation.setId(anId);
        anTermAnnotation.setFinalAnnotation(securityFinalAnnotation);
        anTermAnnotation.setManualAnnotation(securityManualAnnotation);
        anTermAnnotation.setNewTerms(newTerms);
        anTermAnnotation.setGmtModified(new Date());
        anTermAnnotation.setState(AnnotationStateEnum.PROCESSING.name());

        int updateResult = anTermAnnotationMapper.updateByPrimaryKeySelective(anTermAnnotation);
        AssertUtil.state(updateResult > 0, "更新手工标注失败");
    }

    private void decryptAES(List<AnTermAnnotation> anTermAnnotationList) {
        for (AnTermAnnotation anTermAnnotation : anTermAnnotationList) {
            decryptAES(anTermAnnotation);
        }
    }

    private void decryptAES(AnTermAnnotation anTermAnnotation) {
        anTermAnnotation
            .setAutoAnnotation(SecurityUtil.decryptAESBase64(anTermAnnotation.getAutoAnnotation()));
        anTermAnnotation.setManualAnnotation(
            SecurityUtil.decryptAESBase64(anTermAnnotation.getManualAnnotation()));
        anTermAnnotation.setFinalAnnotation(
            SecurityUtil.decryptAESBase64(anTermAnnotation.getFinalAnnotation()));
    }

    /**
     * 判断经过解密的标注文本中是否有二义性的标注存在
     * @param text
     * @return
     */
    private List<TermAnnotationModel> hasAmbiguity(String text) {

        List<TermAnnotationModel> termAnnotationModelList = AnnotationConvert
            .convertAnnotationModelList(text);
        List<TermAnnotationModel> resultList = new ArrayList<>();
        for (int first = 0; first < termAnnotationModelList.size(); first++) {
            for (int second = first + 1; second < termAnnotationModelList.size(); second++) {
                boolean result = AnnotationChecker.hasAmbiguity(termAnnotationModelList.get(first),
                    termAnnotationModelList.get(second));
                if (result) {
                    resultList.add(termAnnotationModelList.get(first));
                }
            }
        }
        return resultList;
    }

}
