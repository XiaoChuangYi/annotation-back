package com.microservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.microservice.apiserver.ApiServerService;
import com.microservice.apiserver.vo.TermTypeVO;
import com.microservice.dataAccessLayer.entity.Account;
import com.microservice.dataAccessLayer.entity.Annotation;
import com.microservice.enums.AnnotationOptionEnum;
import com.microservice.result.AnnotationBratVO;
import com.microservice.result.PageVO;
import com.microservice.result.ResultVO;
import com.microservice.service.annotation.AnnotationBatchService;
import com.microservice.service.annotation.AnnotationService;
import com.microservice.utils.AnnotationConvert;
import com.microservice.vo.CombineAtomicTerm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cjl on 2018/3/29.
 */
@RestController
@RequestMapping(value = "/annotationWordPos")
public class AnnotationController extends BaseController{

    @Autowired
    private AnnotationService annotationService;

    @Autowired
    private ApiServerService apiServerService;

    @Autowired
    private AnnotationBatchService annotationBatchService;

    /**
     * 分页查询标注信息，支持term模糊查询，支持标注状态过滤，支持获取指定用户的标注数据
     */
    @RequestMapping(value = "/queryAnnotationDirectly.do")
    public ResultVO<PageVO<Annotation>> queryAnnotationDirectly(@RequestBody JSONObject params, @ModelAttribute("userAccount")Account account){
        int pageIndex=params.containsKey("pageIndex")?params.getInteger("pageIndex"):1;
        int pageSize=params.containsKey("pageSize")?params.getInteger("pageSize"):10;
        String term=params.getString("term");
        String state=params.getString("state");

        Page<Annotation> pageInfo =annotationService.listAnnotationByConditionPaging(state,account.getId()
                ,term,pageIndex,pageSize);
        List<AnnotationBratVO> annotationBratVOList= AnnotationConvert.convert2AnnotationBratVOList(pageInfo.getResult());
        PageVO<AnnotationBratVO> pageVO=new PageVO(pageInfo,false);
        pageVO.setDataList(annotationBratVOList);
        return ResultVO.success(pageVO);
    }

    /**
     * 不经过ApiServer服务器，更新annotation表并返回BratVO数据
     * @param anId
     * @param finalAnnotation
     */
    private AnnotationBratVO updateAnnotationReBratVO(String anId,String finalAnnotation,List<TermTypeVO> newTerms){
        //更新单条标注信息到数据库
        Annotation paramAnnotation=new Annotation();
        paramAnnotation.setId(anId);
        paramAnnotation.setFinalAnnotation(finalAnnotation);
        String newTermStr=TermTypeVO.convertToString(newTerms);
        paramAnnotation.setNewTerms(newTermStr);
        annotationService.updateAnnotation(paramAnnotation);
        Annotation newAnnotation=annotationService.getAnnotationById(anId);
        AnnotationBratVO annotationBratVO=AnnotationConvert.convert2AnnotationBratVO(newAnnotation);
        return annotationBratVO;
    }

    /**
     * 新增单位标注，不经过ApiServer，仅仅操作final_annotation字段
     */
    @RequestMapping(value = "/addFinalAnnotation.do")
    public ResultVO<AnnotationBratVO> addFinalAnnotation(@RequestBody JSONObject params,@ModelAttribute("userAccount") Account account){

        String anId=params.getString("anId");
        String text=params.getString("text");
        int startPosition=params.getIntValue("startPosition");
        int endPosition=params.getIntValue("endPosition");
        String annotationType=params.getString("annotationType");
        String option=params.getString("option");

        //判断当前用户是否可以操作该条标注数据
        Annotation annotation=annotationService.getAnnotationById(anId);
        if(!annotation.getModifier().equals(account.getId()))
            return ResultVO.error("当前用户无权操作该");

        //如果是新词,原有新词列表增加新词
        String oldTermsText = annotation.getNewTerms();
        if (AnnotationOptionEnum.NEW_TERM.name().equals(option)) {
            oldTermsText = AnnotationConvert.addNewTerm(oldTermsText, text,
                    annotationType);
        }
        List<TermTypeVO> newTerms = TermTypeVO.convertFromString(oldTermsText);

        //构建新的标注
        String finalAnnotation=AnnotationConvert.addUnitAnnotationByLambda(annotation.getFinalAnnotation(),annotationType,startPosition,endPosition,text);
        //更新单条标注信息到数据库
        AnnotationBratVO annotationBratVO=updateAnnotationReBratVO(anId,finalAnnotation,newTerms);
        return ResultVO.success(annotationBratVO);
    }

    /**
     * 更新指定的单位标注，不经过ApiServer，仅仅操作final_annotation字段
     */
    @RequestMapping(value = "/updateFinalAnnotation.do")
    public  ResultVO<AnnotationBratVO> updateFinalAnnotation(@RequestBody JSONObject params,@ModelAttribute("userAccount") Account account){
        String anId=params.getString("anId");
        String tag=params.getString("tag");
        String newType=params.getString("newType");
        String oldType=params.getString("oldType");

        //判断当前用户是否可以操作该条标注数据
        Annotation annotation=annotationService.getAnnotationById(anId);
        if(!annotation.getModifier().equals(account.getId()))
            return ResultVO.error("当前用户无权操作该");

        String finalAnnotation=AnnotationConvert.updateUnitAnnotationTypeByLambda(annotation.getFinalAnnotation(),oldType,newType,tag);

        //获取原有的新词列表
        String newTermsText = annotation.getNewTerms();
        List<TermTypeVO> newTerms = TermTypeVO.convertFromString(newTermsText);
        //更新单条标注信息到数据库
        AnnotationBratVO annotationBratVO=updateAnnotationReBratVO(anId,finalAnnotation,newTerms);
        return ResultVO.success(annotationBratVO);
    }

    /**
     * 删除指定的单位标注，不经过ApiServer，仅仅操作final_annotation字段
     */
    @RequestMapping(value = "/deleteFinalAnnotation.do")
    public  ResultVO<AnnotationBratVO> deleteFinalAnnotation(@RequestBody JSONObject params,@ModelAttribute("userAccount") Account account){
        String anId=params.getString("anId");
        String tag=params.getString("tag");
        String option=params.getString("option");
        //判断当前用户是否可以操作该条标注数据
        Annotation annotation=annotationService.getAnnotationById(anId);
        if(!annotation.getModifier().equals(account.getId()))
            return ResultVO.error("当前用户无权操作该");

        //获取原有的新词列表
        String oldTermsText = annotation.getNewTerms();
        //如果是新词,原有新词列表增加新词
        if (AnnotationOptionEnum.NEW_TERM.name().equals(option)){
            //从原有手工标注中,查找tag对应的标注,构造成新词
            TermTypeVO termTypeVO = AnnotationConvert
                    .getTermTypeVOByTag(annotation.getManualAnnotation(), tag);
            if (termTypeVO != null) {
                oldTermsText = AnnotationConvert.deleteNewTerm(oldTermsText, termTypeVO.getTerm(),
                        termTypeVO.getType());
            }
        }
        List<TermTypeVO> newTerms = TermTypeVO.convertFromString(oldTermsText);

        String finalAnnotation=AnnotationConvert.deleteUnitAnnotationByLambda(annotation.getFinalAnnotation(),tag);
        //更新单条标注信息到数据库
        AnnotationBratVO annotationBratVO=updateAnnotationReBratVO(anId,finalAnnotation,newTerms);
        return ResultVO.success(annotationBratVO);
    }

    /**
     * 通过ApiServer更新单位标注
     * @param anId
     * @param manualAnnotation
     * @param newTerms
     */
    private AnnotationBratVO updateCurrentAnnotationByApiServer(String anId,String manualAnnotation,List<TermTypeVO> newTerms){
        Annotation annotation=annotationService.getAnnotationById(anId);
        annotation.setManualAnnotation(manualAnnotation);
        String newTermStr=TermTypeVO.convertToString(newTerms);
        annotation.setNewTerms(newTermStr);

        List<Annotation> annotationList=new ArrayList<>();
        annotationList.add(annotation);
        List<Annotation> finalAnnotationList=apiServerService.batchPhraseUpdatePosWithNewTerm(annotationList);

        Annotation paramAnnotation=new Annotation();
        paramAnnotation.setId(anId);
        paramAnnotation.setNewTerms(newTermStr);
        paramAnnotation.setManualAnnotation(manualAnnotation);
        paramAnnotation.setFinalAnnotation(finalAnnotationList.get(0).getFinalAnnotation());

        annotationService.updateAnnotation(paramAnnotation);
        Annotation newAnnotation=annotationService.getAnnotationById(anId);
        AnnotationBratVO annotationBratVO=AnnotationConvert.convert2AnnotationBratVO(newAnnotation);
        return annotationBratVO;
    }
    /**
     * 新增单位标注，经过ApiServer
     */
    @RequestMapping(value = "/addAnnotationByApiServer.do")
    public ResultVO<AnnotationBratVO> addAnnotationByApiServer(@RequestBody JSONObject params,@ModelAttribute("userAccount") Account account){
        String anId=params.getString("anId");
        String text=params.getString("text");
        int startPosition=params.getIntValue("startPosition");
        int endPosition=params.getIntValue("endPosition");
        String annotationType=params.getString("annotationType");
        String option=params.getString("option");

        //判断当前用户是否可以操作该条标注数据
        Annotation annotation=annotationService.getAnnotationById(anId);
        if(!annotation.getModifier().equals(account.getId()))
            return ResultVO.error("当前用户无权操作该记录！");

        //获取原有的新词列表
        String oldTermsText = annotation.getNewTerms();
        //如果是新词,原有新词列表增加新词
        if (AnnotationOptionEnum.NEW_TERM.name().equals(option)) {
            oldTermsText = AnnotationConvert.addNewTerm(oldTermsText, text,
                    annotationType);
        }
        List<TermTypeVO> newTerms = TermTypeVO.convertFromString(oldTermsText);
        //构建新的手工标注
        String manualAnnotation=AnnotationConvert.addUnitAnnotationByLambda(annotation.getManualAnnotation(),
                annotationType,startPosition,endPosition,text);
        //调用后台ApiServer，根据当前的手动标注更新到最终标注
        AnnotationBratVO annotationBratVO=updateCurrentAnnotationByApiServer(anId,manualAnnotation,newTerms);
        return ResultVO.success(annotationBratVO);
    }

    /**
     * 删除单位标注，经过ApiServer
     */
    @RequestMapping(value = "/deleteAnnotationApiServer.do")
    public ResultVO<AnnotationBratVO> deleteAnnotationByApiServer(@RequestBody JSONObject params,@ModelAttribute("userAccount") Account account){
        String anId=params.getString("anId");
        String tag=params.getString("tag");
        String option=params.getString("option");
        //判断当前用户是否可以操作该条标注数据
        Annotation annotation=annotationService.getAnnotationById(anId);
        if(!annotation.getModifier().equals(account.getId()))
            return ResultVO.error("当前用户无权操作该记录！");

        //获取原有的新词列表
        String oldTermsText = annotation.getNewTerms();

        //如果是新词,原有新词列表增加新词
        if (AnnotationOptionEnum.NEW_TERM.name().equals(option)){
            //从原有手工标注中,查找tag对应的标注,构造成新词
            TermTypeVO termTypeVO = AnnotationConvert
                    .getTermTypeVOByTag(annotation.getManualAnnotation(), tag);
            if (termTypeVO != null) {
                oldTermsText = AnnotationConvert.deleteNewTerm(oldTermsText, termTypeVO.getTerm(),
                        termTypeVO.getType());
            }
        }
        List<TermTypeVO> newTerms = TermTypeVO.convertFromString(oldTermsText);
        //构建新的手工标注
        String manualAnnotation=AnnotationConvert.deleteUnitAnnotationByLambda(annotation.getManualAnnotation(),tag);
        //调用后台ApiServer，根据当前的手动标注更新到最终标注
        AnnotationBratVO annotationBratVO=updateCurrentAnnotationByApiServer(anId,manualAnnotation,newTerms);
        return ResultVO.success(annotationBratVO);
    }

    /**
     * 删除新词
     *
     */
    @RequestMapping(value = "/deleteNewTerm.do")
    public ResultVO<AnnotationBratVO> deleteNewTerm(@RequestBody JSONObject params,@ModelAttribute("userAccount") Account account){
        String anId=params.getString("anId");
        String term=params.getString("term");
        String type=params.getString("type");

        //判断当前用户是否可以操作该条标注数据
        Annotation annotation=annotationService.getAnnotationById(anId);
        if(!annotation.getModifier().equals(account.getId()))
            return ResultVO.error("当前用户无权操作该记录！");
        String newTermsText=AnnotationConvert.deleteNewTerm(annotation.getNewTerms(),term,type);
        //如果删除后的新词与删除前的一致,则无需更新删除后的新词
        if(annotation.getNewTerms().equals(newTermsText)){
            AnnotationBratVO annotationBratVO=AnnotationConvert.convert2AnnotationBratVO(annotation);
            return ResultVO.success(annotationBratVO);
        }
        //构建最新的新词列表
        List<TermTypeVO> newTerms = TermTypeVO.convertFromString(newTermsText);
        //调用后台ApiServer，根据当前的手动标注更新到最终标注
        AnnotationBratVO annotationBratVO=updateCurrentAnnotationByApiServer(anId,annotation.getManualAnnotation(),newTerms);
        return ResultVO.success(annotationBratVO);
    }


    /**
     * 更据原子术语组信息，分页查询annotation表
     */
    @RequestMapping(value = {"/selectAnnotationByTermTypeArr.do"})
    public ResultVO<List<AnnotationBratVO>> selectAnnotationByTermTypeArr(@RequestBody JSONObject params){
        List<CombineAtomicTerm> atomicTermList=params.getJSONArray("combineAtomicTermList").toJavaObject(List.class);
        List<Annotation> annotationList =annotationBatchService.listAnnotationByUnitAnnotationArr(atomicTermList);
        List<AnnotationBratVO> annotationBratVOList = AnnotationConvert.convert2AnnotationBratVOList(annotationList);
        return  ResultVO.success(annotationBratVOList);
    }

    /**
     * 完成标注
     */
    @RequestMapping(value = "/finish.do")
    public ResultVO finishAnnotation(@RequestBody JSONObject params,@ModelAttribute("userAccount") Account account) {
        String anId=params.getString("anId");
        Annotation annotation = annotationService.getAnnotationById(anId);
        if(account.equals(annotation.getModifier())){
            return ResultVO.error("您无权操作当前术语");
        }
        //关系到原子术语库，暂做todo
//        annotationService.finishAnnotation(anId);
        return ResultVO.success();
    }

}
