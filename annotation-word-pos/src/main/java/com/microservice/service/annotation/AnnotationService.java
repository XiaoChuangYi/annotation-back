package com.microservice.service.annotation;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.microservice.apiserver.ApiServerService;
import com.microservice.apiserver.vo.TermTypeVO;
import com.microservice.dataAccessLayer.entity.Annotation;
import com.microservice.dataAccessLayer.mapper.AnnotationMapper;
import com.microservice.dataAccessLayer.mapper.CorpusMapper;
import com.microservice.enums.AnnotationStateEnum;
import com.microservice.enums.TermStateEnum;
import com.microservice.service.atomicterm.AnAtomicTermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by cjl on 2018/3/29.
 */
@Service
public class AnnotationService {

    @Autowired
    protected AnnotationMapper annotationMapper;

    @Autowired
    private ApiServerService apiServerService;

    @Autowired
    private AnAtomicTermService anAtomicTermService;

    @Autowired
    private CorpusMapper corpusMapper;



    /**
     * 根据状态，用户信息，查询标注数据
     * @param userModifier
     * @param state
     * @param pageIndex
     * @param pageSize
     */
    public Page<Annotation> listAnnotationForDistribution(String userModifier,String state,int pageIndex,int pageSize){
        Page<Annotation> pageInfo = PageHelper.startPage(pageIndex, pageSize);
        List<String> stateList = new ArrayList<>();
        stateList.add(state);
        String sort="state DESC,gmt_created ASC";
        annotationMapper.listAnnotationByStateListUseModifier(stateList,userModifier,sort);
        return pageInfo;
    }

    /**
     * 根据状态分页查询标注
     * @param
     * @return
     */
    public Page<Annotation> listAnnotationByPagingThroughApiServer(String userId, int pageNum,
                                                                   int pageSize) {
        Page<Annotation> pageInfo = PageHelper.startPage(pageNum, pageSize);
        List<String> stateList = new ArrayList<>();
        stateList.add(AnnotationStateEnum.PROCESSING.name());
        stateList.add(AnnotationStateEnum.INIT.name());
        String sort="state DESC,gmt_created ASC";
        annotationMapper.listAnnotationByStateListUseModifier(stateList, userId,sort);
        apiServerService.batchPhraseUpdatePosWithNewTerm(pageInfo.getResult());
        Annotation paramAnnotation=null;
        for (Annotation currentAnnotation : pageInfo.getResult()) {
            paramAnnotation=new Annotation();
            paramAnnotation.setId(currentAnnotation.getId());
            paramAnnotation.setFinalAnnotation(currentAnnotation.getFinalAnnotation());
            paramAnnotation.setGmtModified(new Date());
            updateAnnotation(paramAnnotation);
        }
        return pageInfo;
    }

    /**
     * 根据用户账号信息和标注状态和标注文本信息分页查询标注数据
     * @param annotationState
     * @param modifier
     * @param term
     * @param pageIndex
     * @param pageSize
     */
    public Page<Annotation> listAnnotationByConditionPaging(String annotationState, String modifier,String term, int pageIndex, int pageSize){
        Page<Annotation> pageInfo= PageHelper.startPage(pageIndex,pageSize);
        Annotation pAnnotation=new Annotation();
        pAnnotation.setModifier(modifier);
        pAnnotation.setState(annotationState);
        pAnnotation.setTerm(term);
        annotationMapper.listAnnotationByCondition(pAnnotation,"gmt_created desc");
        return pageInfo;
    }

    /**
     * 根据标注状态信息集合分页查询标注数据
     * @param stateList
     * @param pageIndex
     * @param pageSize
     */
    public Page<Annotation> listAnnotationByStatesAndUserModifierPaging(List<String> stateList,String userModifier,int pageIndex,int pageSize){
        Page<Annotation> pageInfo=PageHelper.startPage(pageIndex,pageSize);
        annotationMapper.listAnnotationByStatesAndModifier(stateList,userModifier);
        return pageInfo;
    }


    /**
     * 根据标注状态信息集合分页查询标注数据
     * @param stateList
     * @param pageIndex
     * @param pageSize
     */
    public Page<Annotation> listAnnotationByStatesPaging(List<String> stateList,int pageIndex,int pageSize){
        Page<Annotation> pageInfo=PageHelper.startPage(pageIndex,pageSize);
        annotationMapper.listAnnotationByStateList(stateList,"state desc,gmt_created asc");
        return pageInfo;
    }

    /**
     * 根据主键ID查询特定的标注记录
     * @param id
     */
    public Annotation getAnnotationById(String id){
        return annotationMapper.getAnnotationById(id);
    }

    /**
     * 根据主键ID选择性选取字段，更新annotation
     * @param annotation
     */
    public void updateAnnotation(Annotation annotation){
        annotationMapper.updateAnnotationSelective(annotation);
    }

    /**
     * 批量更新标注annotation表的modifier字段
     * @param idList
     * @param modifier
     */
    public void batchUpdateAnnotationModifier(List<String> idList,String modifier){
        annotationMapper.batchUpdateAnnotationModifier(idList,modifier);
    }

    /**
     *批量更新标注表的最终和手动标注
     * @param annotationList
     */
    public void batchUpdateAnnotationFinalAndManual(List<Annotation> annotationList){
        annotationMapper.batchUpdateAnnotation(annotationList);
    }

    /**
     * 审核标注
     * 审核最终的标注，以及新词，审核通过后，批量新增新词到原子词库
     * @param anId
     */
    public void finishAnnotation(String anId){
        Annotation annotationOld = getAnnotationById(anId);

        //如果存在新词,保存新词到词库
        String newTermsStr = annotationOld.getNewTerms();
        List<TermTypeVO> termTypeVOList = TermTypeVO.convertFromString(newTermsStr);
        for (TermTypeVO termTypeVO : termTypeVOList) {
            anAtomicTermService.saveAtomicTerm(anId, termTypeVO);
        }

        String finalAnnotation = annotationOld.getFinalAnnotation().replace("-unconfirmed",
                "");
        Annotation annotation = new Annotation();
        annotation.setId(anId);
        annotation.setState(AnnotationStateEnum.FINISH.name());
        annotation.setFinalAnnotation(finalAnnotation);
        annotationMapper.updateAnnotationSelective(annotation);
    }

    /**
     * 新增原始文本和过ApiServer的预标注的文本到annotation表
     * 同时更改对应corpus表的状态
     */
    @Transactional(propagation = Propagation.REQUIRED)
    protected void saveTermAnnotation(String termId, String term, String autoAnnotation) {
        Annotation annotation = new Annotation();
        annotation.setTermId(termId);
        annotation.setTerm(term);
        annotation.setGmtModified(new Date());
        annotation.setAutoAnnotation(autoAnnotation);
        annotation.setFinalAnnotation(autoAnnotation);
        annotation.setState(AnnotationStateEnum.INIT.name());

        annotationMapper.saveAnnotationSelective(annotation);

        //todo 这里的枚举值估计会有问题
        corpusMapper.updateCorpusStateById(TermStateEnum.FINISH.name(),termId);

    }

    /**
     * 批量分配标注给指定的用户，同时调用算法后台的ApiServer，对文本进行预标注
     */
    public List<Annotation> designateAnnotationAndInitAnnotation(List<String> idArr,String modifier){
//        annotationMapper.batchUpdateAnnotationModifier(idArr,modifier);
        List<Annotation> annotationList=annotationMapper.listAnnotationByIdArr(idArr);
        List<Annotation> annotationListThroughApiServer=apiServerService.batchTokenizePos(annotationList);
        for (Annotation currentAnnotation : annotationListThroughApiServer) {
            Annotation annotation=new Annotation();
            annotation.setId(currentAnnotation.getId());
            annotation.setState(AnnotationStateEnum.INIT.name());
            annotation.setModifier(modifier);
            annotation.setAutoAnnotation(currentAnnotation.getFinalAnnotation());
            annotation.setFinalAnnotation(currentAnnotation.getFinalAnnotation());
            annotation.setGmtModified(new Date());
            annotationMapper.updateAnnotationSelective(annotation);
//            updateAnnotation(paramAnnotation);
        }
        return annotationListThroughApiServer;
    }

}
