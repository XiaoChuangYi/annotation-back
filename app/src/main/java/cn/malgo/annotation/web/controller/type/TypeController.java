package cn.malgo.annotation.web.controller.type;

import cn.malgo.annotation.common.dal.model.Annotation;
import cn.malgo.annotation.common.dal.model.AnType;
import cn.malgo.annotation.common.dal.model.AnnotationPagination;
import cn.malgo.annotation.common.util.AssertUtil;
import cn.malgo.annotation.core.model.convert.AnnotationConvert;
import cn.malgo.annotation.core.service.type.TypeAnnotationBatchService;
import cn.malgo.annotation.core.service.type.TypeService;
import cn.malgo.annotation.web.controller.annotation.result.AnnotationBratVO;
import cn.malgo.annotation.web.controller.common.BaseController;
import cn.malgo.annotation.web.controller.type.request.AddTypeRequest;
import cn.malgo.annotation.web.controller.type.request.QueryTypeRequest;
import cn.malgo.annotation.web.controller.type.request.UpdateTypeRequest;
import cn.malgo.annotation.web.request.PageRequest;
import cn.malgo.annotation.web.result.PageVO;
import cn.malgo.annotation.web.result.ResultVO;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by cjl on 2017/11/20.
 */
@RestController
@RequestMapping(value = "/type")
public class TypeController extends BaseController {

    @Autowired
    private TypeService typeService;

    @Autowired
    private TypeAnnotationBatchService typeAnnotationBatchService;

    @RequestMapping(value = "/getPaginationType.do")
    public ResultVO<PageVO<AnType>> getAllType(QueryTypeRequest request){
        Page<AnType> page =typeService.selectPaginationTypesAndShowParent(request.getPageNum(),request.getPageSize(),request.getTypeCode(),request.getTypeName());
        PageVO<AnType> pageVO = new PageVO(page);
        return ResultVO.success(pageVO);
    }


    @RequestMapping(value = "/getTypes.do")
    public ResultVO<List<AnType>> getAllType(){
        List<AnType> anTypeList =typeService.selectAllTypes();
        return ResultVO.success(anTypeList);
    }

    @RequestMapping(value = "/getTypesById.do")
    public ResultVO<List<AnType>> getAllType(String id){
        List<AnType> anTypeList=typeService.selectAllTypesById(id);
        return ResultVO.success(anTypeList);
    }

    @RequestMapping(value = "/addType.do")
    public ResultVO addType(AddTypeRequest request){
        AddTypeRequest.check(request);
        typeService.insertType(request.getParentId(),request.getTypeName(),request.getTypeCode());
        return  ResultVO.success();
    }

    @RequestMapping(value = "/updateType.do")
    public ResultVO updateType(String parentId,String typeId,String typeName,String originParentId){

        typeService.updateType(parentId,typeId,typeName,originParentId);
        return  ResultVO.success();
    }

    @RequestMapping(value = "/deleteType.do")
    public ResultVO deleteType(String typeId){
        AssertUtil.notBlank(typeId,"类型Id为空");
        typeService.deleteType(typeId);
        return  ResultVO.success();
    }
    /**
     * 批量更新原子术语表，术语表，术语标注标注表中的type类型
     */
    @RequestMapping(value="/updateBatchType.do")
    public ResultVO updateType(UpdateTypeRequest request){
        UpdateTypeRequest.check(request);
//        asyncTypeBatchService.asyncAutoBatchType(request.getTypeOld(),request.getTypeNew());
        typeAnnotationBatchService.batchReplaceAnnotationTerm(request.getTypeOld(),request.getTypeNew());
        typeService.updateBatchTypeOnAtomicTerm(request.getTypeOld(),request.getTypeNew());
        typeService.updateBatchTypeOnTerm(request.getTypeOld(),request.getTypeNew());
        typeService.updateTypeCodeById(request.getId(),request.getTypeNew());
        return  ResultVO.success();
    }
    /**
     * 更据原子术语信息，分页查询annotation表
     */
    @RequestMapping(value = {"/selectAnnotationByTermTypeArr.do"})
    public ResultVO<List<AnnotationBratVO>> selectAnnotationByTermTypeArr(CombineAtomicTermArr combineAtomicTermArr){
        List<Annotation> annotationPagination =typeService.queryAnnotationByCombineAtomicTerm(combineAtomicTermArr.getCombineAtomicTermList());
        List<AnnotationBratVO> annotationBratVOList = convertAnnotationBratVOList(annotationPagination);
        return  ResultVO.success(annotationBratVOList);
    }

    /**
     * @param type
     * @param term
     * @param pageIndex
     * @param pageSize
     * 更据原子术语信息，分页查询annotation表
     */
    @RequestMapping(value = "selectAnnotationServerPagination.do")
    public ResultVO<PageVO<AnnotationBratVO>> selectAnnotationServerPagination(String type, String term, int pageIndex, int pageSize){
        AnnotationPagination annotationPagination =typeService.queryAnnotationByType(type,term,pageIndex,pageSize);
        List<AnnotationBratVO> annotationBratVOList = convertAnnotationBratVOList(annotationPagination.getList());
        PageResult<AnnotationBratVO> pageResult=new PageResult<>();
        pageResult.setDataList(annotationBratVOList);
        pageResult.setTotal(annotationPagination.getLastIndex());
        return  ResultVO.success(pageResult);
    }

    /**
     * 模型转换,标注模型转换成brat模型
     * @param annotationList
     * @return
     */
    private List<AnnotationBratVO> convertAnnotationBratVOList(List<Annotation> annotationList) {
        List<AnnotationBratVO> annotationBratVOList = new ArrayList<>();
        for (Annotation annotation : annotationList) {
            AnnotationBratVO annotationBratVO = convertFromAnTermAnnotation(annotation);
            annotationBratVOList.add(annotationBratVO);
        }
        return annotationBratVOList;
    }

    /**
     * 模型转换,标注模型转换成brat模型
     * @param annotation
     * @return
     */
    private AnnotationBratVO convertFromAnTermAnnotation(Annotation annotation) {
        JSONObject bratJson = AnnotationConvert.convertToBratFormat(annotation);
        AnnotationBratVO annotationBratVO = new AnnotationBratVO();
        BeanUtils.copyProperties(annotation, annotationBratVO);
        annotationBratVO.setBratData(bratJson);
        return annotationBratVO;
    }
}
