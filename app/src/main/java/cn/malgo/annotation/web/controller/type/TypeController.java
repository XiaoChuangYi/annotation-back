package cn.malgo.annotation.web.controller.type;

import cn.malgo.annotation.common.dal.model.AnTermAnnotation;
import cn.malgo.annotation.common.dal.model.AnType;
import cn.malgo.annotation.common.util.AssertUtil;
import cn.malgo.annotation.core.service.annotation.AnnotationService;
import cn.malgo.annotation.core.service.type.AsyncTypeBatchService;
import cn.malgo.annotation.core.service.type.TypeAnnotationBatchService;
import cn.malgo.annotation.core.service.type.TypeService;
import cn.malgo.annotation.web.controller.common.BaseController;
import cn.malgo.annotation.web.controller.type.request.AddTypeRequest;
import cn.malgo.annotation.web.controller.type.request.UpdateTypeRequest;
import cn.malgo.annotation.web.result.ResultVO;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @Autowired
    private AsyncTypeBatchService asyncTypeBatchService;

    @RequestMapping(value = "/getTypes.do")
    public ResultVO<List<AnType>> getAllType(){
        List<AnType> anTypeList=typeService.selectAllTypes();
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
    public ResultVO updateType(String parentId,String typeId,String typeName){

        typeService.updateTypeName(parentId,typeId,typeName);
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
     * 根据type查询术语标注表中的的对应记录
     * @param typeCode
     */
    @RequestMapping(value="/selectAnnotationByType.do")
     public  ResultVO<List<AnTermAnnotation>> selectAnnotationByType(String typeCode,String term){
        List<AnTermAnnotation> anTermAnnotationList=typeService.queryAnnotationByType(typeCode,term);
        return  ResultVO.success(anTermAnnotationList);
    }

}
