package cn.malgo.annotation.core.service.annotation;

import java.util.*;

import cn.malgo.annotation.common.dal.model.Annotation;
import cn.malgo.annotation.common.dal.model.Corpus;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import cn.malgo.annotation.common.dal.mapper.AnnotationMapper;
import cn.malgo.annotation.common.dal.sequence.CodeGenerateTypeEnum;
import cn.malgo.annotation.common.dal.sequence.SequenceGenerator;
import cn.malgo.annotation.common.service.integration.apiserver.ApiServerService;
import cn.malgo.annotation.common.service.integration.apiserver.result.AnnotationResult;
import cn.malgo.annotation.common.service.integration.apiserver.vo.TermTypeVO;
import cn.malgo.annotation.common.util.AssertUtil;
import cn.malgo.annotation.core.model.check.AnnotationChecker;
import cn.malgo.annotation.core.model.enums.annotation.AnnotationStateEnum;
import cn.malgo.annotation.core.model.enums.term.TermStateEnum;
import cn.malgo.annotation.core.service.corpus.AtomicTermService;
import cn.malgo.annotation.core.service.corpus.CorpusService;
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
    private AnnotationMapper annotationMapper;

    @Autowired
    private ApiServerService       apiServerService;

    @Autowired
    private CorpusService corpusService;

    @Autowired
    private AtomicTermService      atomicTermService;


    /**
     * 根据状态分页查询标注
     * @param
     * @return
     */
    public Page<Annotation> queryOnePageThroughApiServer(String userId, int pageNum,
                                                         int pageSize) {
        Page<Annotation> pageInfo = PageHelper.startPage(pageNum, pageSize);
        List<String> stateList = new ArrayList<>();
        stateList.add(AnnotationStateEnum.PROCESSING.name());
        stateList.add(AnnotationStateEnum.INIT.name());

        annotationMapper.selectByStateListModifier(stateList, userId);
        decryptAES(pageInfo.getResult());
        apiServerService.batchPhraseUpdatePosWithNewTerm(pageInfo.getResult());
        for (Annotation annotation : pageInfo.getResult()) {
            updateFinalAnnotation(annotation.getId(), annotation.getFinalAnnotation());
        }
        return pageInfo;
    }
    /**
     *根据分配查询的逻辑获取指定数量数据的id集合
     * @param state
     * @param userId
     * @param total
     */
    public List<String> getAnnotationIDsByCondition(String state,String userId,int total){
        Page<String> pageInfo = PageHelper.startPage(1, total);
//        List<Annotation> annotationList=annotationMapper.selectByStateAndUserId(state, userId);
        annotationMapper.selectIDsByNum(state,userId);
        return pageInfo;
    }
    /**
     * 批量更新标准表的modifier字段
     */
    public  void updateBatchAnnotationUserId(List<String> annotationList,String userId){
        int updateBatch=annotationMapper.batchUpdateAnnotationUserId(annotationList,userId);
        AssertUtil.state(updateBatch > 0, "批量更新失败");
    }

    /**
     * 分页多条件查询数据库中的标注
     */
    public Page<Annotation> queryOnePageForDistribution(String annotationState, String userId,
                                                 int pageNum, int pageSize) {
        Page<Annotation> pageInfo = PageHelper.startPage(pageNum, pageSize);
        annotationMapper.selectByStateAndUserId(annotationState, userId);
        decryptAES(pageInfo.getResult());
        return pageInfo;
    }
    /**
     * 分页条件查询数据库中的标注
     */
    public Page<Annotation> queryOnePageDirectly(String annotationTerm,String annotationState, String userId,
                                                 int pageNum, int pageSize) {
        Page<Annotation> pageInfo = PageHelper.startPage(pageNum, pageSize);
        annotationMapper.selectByStateAndTermFuzzy(annotationState, userId,annotationTerm);
        decryptAES(pageInfo.getResult());
        return pageInfo;
    }

    /**
     * 根据状态分页查询标注
     * @param
     * @return
     */
    public Annotation queryByAnIdThroughApiServer(String anId) {

        Annotation annotation = annotationMapper.selectByPrimaryKey(anId);
        decryptAES(annotation);
        List<Annotation> annotationList = new ArrayList<>();
        annotationList.add(annotation);

        List<Annotation> annotationListNew = apiServerService
            .batchPhraseUpdatePosWithNewTerm(annotationList);

        return annotationListNew.get(0);
    }

    /**
     * 通过termId 来自动标注
     * 首次标注,主要用于定时任务的批处理
     * @param termId
     */
    public void autoAnnotationByTermId(String termId) {

        //通过调用apiServer服务,获取自动标注结果
        Corpus corpus = corpusService.queryByTermId(termId);
        String autoAnnotation = apiServerService.phraseTokenize(corpus.getTerm());

        //检查是否存标注信息
        Annotation annotation = annotationMapper.selectByTermId(termId);
        if (annotation == null) {
            saveTermAnnotation(corpus, autoAnnotation);
        } else {
            updateAutoAnnotation(annotation.getId(), autoAnnotation);
        }
    }

    /**
     * 批量自动标注
     * @param termList
     */
    public void autoAnnotationByTermList(List<Corpus> termList) {

        Map<String, String> termMap = new HashMap<>();
        for (Corpus corpus : termList) {
            termMap.put(corpus.getId(), corpus.getTerm());
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
     * 通过annotationId,来标注annottion表的最终标准字段
     */
    public Annotation autoFinalAnnotationByAnId(String anId,String finalAnnotaion,List<TermTypeVO> newTerms){
        String newTermsStr = TermTypeVO.convertToString(newTerms);
        updateFinalAnnotation(anId,finalAnnotaion,newTermsStr);
        Annotation result = annotationMapper.selectByPrimaryKey(anId);
        decryptAES(result);
        return result;
    }

    /**
     * 通过annotationId 来自动标注,此时需要根据用户标注的新词来调用apiServer的接口来处理
     * 用户手动标注后,调用apiServer,合成手动标注和自动标注
     * @param anId
     * @param manual
     * @param newTerms
     */
    public Annotation autoAnnotationByAnId(String anId, String manual,
                                           List<TermTypeVO> newTerms) {
        Annotation annotation = queryByAnId(anId);

        List<Annotation> annotationList = new ArrayList<>();

        String newTermsStr = TermTypeVO.convertToString(newTerms);
        annotation.setNewTerms(newTermsStr);
        annotation.setManualAnnotation(manual);
        annotationList.add(annotation);

        List<Annotation> finalAnnotationList = apiServerService
            .batchPhraseUpdatePosWithNewTerm(annotationList);

        updateManualAnnotation(anId, manual, newTermsStr,
            finalAnnotationList.get(0).getFinalAnnotation());

        Annotation result = annotationMapper.selectByPrimaryKey(anId);

        decryptAES(result);

        return result;

    }

    /**
     * 结束标注
     * @param anId
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void finishAnnotation(String anId) {
        Annotation annotationOld = queryByAnId(anId);

        //检查是否有歧义未处理
        boolean hasAmbiguity = AnnotationChecker
            .hasAmbiguity(annotationOld.getFinalAnnotation());
        AssertUtil.state(!hasAmbiguity, "存在歧义");

        //如果存在新词,保存新词到词库
        String newTermsStr = annotationOld.getNewTerms();
        List<TermTypeVO> termTypeVOList = TermTypeVO.convertFromString(newTermsStr);
        for (TermTypeVO termTypeVO : termTypeVOList) {
            atomicTermService.saveAtomicTerm(anId, termTypeVO);
        }

        String finalAnnotation = annotationOld.getFinalAnnotation().replace("-unconfirmed",
            "");
        String finalAnnotationAfterCrypt = SecurityUtil.cryptAESBase64(finalAnnotation);

        Annotation annotation = new Annotation();
        annotation.setId(anId);
        annotation.setState(AnnotationStateEnum.FINISH.name());
        annotation.setFinalAnnotation(finalAnnotationAfterCrypt);
        annotationMapper.updateByPrimaryKeySelective(annotation);
    }

    /**
     * 设置术语的状态为无法识别
     * @param anId
     */
    public void setUnRecognize(String anId) {
        Annotation annotation = new Annotation();
        annotation.setId(anId);
        annotation.setState(AnnotationStateEnum.UN_RECOGNIZE.name());
        int updateResult = annotationMapper.updateByPrimaryKeySelective(annotation);

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
    public Page<Annotation> queryOnePageUNEncrypted(String annotationState, String userId,
                                                    int pageNum, int pageSize) {
        Page<Annotation> pageInfo = PageHelper.startPage(pageNum, pageSize);
        annotationMapper.selectByStateModifier(annotationState, userId);
        return pageInfo;
    }

    /**
     * 保存标注,主要用于标注自动标注
     * @param corpus
     * @param autoAnnotation
     */
    private void saveTermAnnotation(Corpus corpus, String autoAnnotation) {
        saveTermAnnotation(corpus.getId(), corpus.getTerm(), autoAnnotation);
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
        Annotation annotation = new Annotation();
        annotation.setId(id);
        annotation.setTermId(termId);
        annotation.setTerm(term);
        annotation.setAutoAnnotation(securityAnnotation);
        annotation.setFinalAnnotation(securityAnnotation);
        annotation.setState(AnnotationStateEnum.INIT.name());

        int saveResult = annotationMapper.insert(annotation);
        AssertUtil.state(saveResult > 0, "保存自动标注失败");

        corpusService.updateTermState(termId, TermStateEnum.FINISH);

    }

    /**
     * 根据标注ID查询标注信息
     * @param id
     * @return
     */
    public Annotation queryByAnId(String id) {
        Annotation annotation = annotationMapper.selectByPrimaryKey(id);
        decryptAES(annotation);
        return annotation;
    }

    /**
     *后台分页查询标注信息
     * @param state
     * @param pageNum
     * @param pageSize
     */
    public List<Annotation> queryFinalAnnotationPagination(String state,int pageNum,int pageSize){
        List<Annotation> annotationList=annotationMapper.selectFinalAnnotationByPagination(state,pageNum,pageSize);
        decryptAES(annotationList);
        return  annotationList;
    }

    /**
     * 根据状态分页查询标注信息
     * @param stateList
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Page<Annotation> queryByStateList(List<String> stateList, int pageNum,
                                             int pageSize) {
        Page<Annotation> pageInfo = PageHelper.startPage(pageNum, pageSize);
        annotationMapper.selectByStateList(stateList);
        decryptAES(pageInfo.getResult());
        return pageInfo;
    }
    /**
     *批量更新标注表的最终和手动标注
     * @param
     */
    public void updateBatchAnnotation(List<Annotation> annotationList){
        int updateResult= annotationMapper.batchUpdateFinalAndManualAnnotation(annotationList);
        AssertUtil.state(updateResult > 0, "更新标注失败");

    }
    /**
     * 更新自动标注
     * @param anId
     * @param autoAnnotation
     */
    private void updateAutoAnnotation(String anId, String autoAnnotation) {
        String securityAnnotation = SecurityUtil.cryptAESBase64(autoAnnotation);

        Annotation annotation = new Annotation();
        annotation.setId(anId);
        annotation.setAutoAnnotation(securityAnnotation);
        annotation.setFinalAnnotation(securityAnnotation);
        annotation.setGmtModified(new Date());

        int updateResult = annotationMapper.updateByPrimaryKeySelective(annotation);
        AssertUtil.state(updateResult > 0, "更新自动标注失败");
    }
    /**
     * 更新最终标注并更新新词列表
     * @param anId
     * @param finalAnnotation
     */
    public void updateFinalAnnotation(String anId, String finalAnnotation,String newTerms) {
        String securityFinalAnnotation = SecurityUtil.cryptAESBase64(finalAnnotation);
        Annotation annotation = new Annotation();
        annotation.setId(anId);
        annotation.setFinalAnnotation(securityFinalAnnotation);
        annotation.setNewTerms(newTerms);
        annotation.setGmtModified(new Date());

        int updateResult = annotationMapper.updateByPrimaryKeySelective(annotation);
        AssertUtil.state(updateResult > 0, "更新最终标注失败");
    }
    /**
     * 更新最终标注
     * @param anId
     * @param finalAnnotation
     */
    public void updateFinalAnnotation(String anId, String finalAnnotation) {
        String securityFinalAnnotation = SecurityUtil.cryptAESBase64(finalAnnotation);

        Annotation annotation = new Annotation();
        annotation.setId(anId);
        annotation.setFinalAnnotation(securityFinalAnnotation);
        annotation.setGmtModified(new Date());

        int updateResult = annotationMapper.updateByPrimaryKeySelective(annotation);
        AssertUtil.state(updateResult > 0, "更新最终标注失败");
    }
    /**
     * 重载更新手动标注
     * @param anId
     * @param manualAnnotation
     * */
    public  void updateMunalAnnotation(String anId,String manualAnnotation){
        String securityManualAnnotation = SecurityUtil.cryptAESBase64(manualAnnotation);

        Annotation annotation = new Annotation();
        annotation.setId(anId);
        annotation.setManualAnnotation(securityManualAnnotation);
        annotation.setGmtModified(new Date());

        int updateResult = annotationMapper.updateByPrimaryKeySelective(annotation);
        AssertUtil.state(updateResult > 0, "更新最终标注失败");
    }

    /**
     * 加密标注信息,并且更新
     * @param annotation 传入的标注需是明文
     */
    public void cryptAnnotationAndUpdate(Annotation annotation) {
        annotation
            .setAutoAnnotation(SecurityUtil.cryptAESBase64(annotation.getAutoAnnotation()));
        annotation
            .setFinalAnnotation(SecurityUtil.cryptAESBase64(annotation.getFinalAnnotation()));
        annotation.setManualAnnotation(
            SecurityUtil.cryptAESBase64(annotation.getManualAnnotation()));
        annotationMapper.updateByPrimaryKeySelective(annotation);
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

        Annotation annotation = new Annotation();
        annotation.setId(anId);
        annotation.setFinalAnnotation(securityFinalAnnotation);
        annotation.setManualAnnotation(securityManualAnnotation);
        annotation.setNewTerms(newTerms);
        annotation.setGmtModified(new Date());
        annotation.setState(AnnotationStateEnum.PROCESSING.name());

        int updateResult = annotationMapper.updateByPrimaryKeySelective(annotation);
        AssertUtil.state(updateResult > 0, "更新手工标注失败");
    }

    /**
     * 更新标注状态
     * @param anId
     * @param annotationStateEnum
     */
    public void updateAnnotationState(String anId, AnnotationStateEnum annotationStateEnum) {
        Annotation annotation = new Annotation();
        annotation.setId(anId);
        annotation.setGmtModified(new Date());
        annotation.setState(annotationStateEnum.name());

        int updateResult = annotationMapper.updateByPrimaryKeySelective(annotation);
        AssertUtil.state(updateResult > 0, "更新标注状态失败");
    }

    private void decryptAES(List<Annotation> annotationList) {
        for (Annotation annotation : annotationList) {
            decryptAES(annotation);
        }
    }

    private void decryptAES(Annotation annotation) {
        annotation
            .setAutoAnnotation(SecurityUtil.decryptAESBase64(annotation.getAutoAnnotation()));
        annotation.setManualAnnotation(
            SecurityUtil.decryptAESBase64(annotation.getManualAnnotation()));
        annotation.setFinalAnnotation(
            SecurityUtil.decryptAESBase64(annotation.getFinalAnnotation()));
    }
    /**
     * 查询标注表的总条数
     * */
    public  int  annotationTermSize(String state){
        return  annotationMapper.selectTermAnnotationCount(state);
    }

}
