package cn.malgo.annotation.core.service.annotation;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
import cn.malgo.annotation.core.model.enums.annotation.AnnotationStateEnum;
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
     * @param annotationState
     * @return
     */
    public Page<AnTermAnnotation> queryOnePage(String annotationState, String userId, int pageNum,
                                               int pageSize) {
        Page<AnTermAnnotation> pageInfo = PageHelper.startPage(pageNum, pageSize);
        anTermAnnotationMapper.selectByStateModifier(annotationState, userId);
        decryptAES(pageInfo.getResult());
        apiServerService.batchPhraseUpdatePosWithNewTerm(pageInfo.getResult());
        for (AnTermAnnotation anTermAnnotation : pageInfo.getResult()) {
            updateFinalAnnotation(anTermAnnotation.getId(), anTermAnnotation.getFinalAnnotation());
        }
        return pageInfo;
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
            atomicTermService.saveAtomicTerm(termTypeVO);
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

    public AnTermAnnotation queryByAnId(String id) {
        AnTermAnnotation anTermAnnotation = anTermAnnotationMapper.selectByPrimaryKey(id);
        decryptAES(anTermAnnotation);
        return anTermAnnotation;
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

}
