package cn.malgo.annotation.web.controller.type;

import cn.malgo.annotation.common.dal.model.AnType;
import cn.malgo.annotation.common.util.AssertUtil;
import cn.malgo.annotation.core.service.type.AsyncTypeBatchService;
import cn.malgo.annotation.core.service.type.TypeAnnotationBatchService;
import cn.malgo.annotation.core.service.type.TypeService;
import cn.malgo.annotation.web.controller.common.BaseController;
import cn.malgo.annotation.web.controller.type.request.AddTypeRequest;
import cn.malgo.annotation.web.controller.type.request.UpdateTypeRequest;
import cn.malgo.annotation.web.result.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.plaf.synth.SynthEditorPaneUI;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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
    @RequestMapping(value = "/addType.do")
    public ResultVO addType(AddTypeRequest request){
        AddTypeRequest.check(request);
        typeService.insertType(request.getParentId(),request.getTypeName(),request.getTypeCode()    );
        return  ResultVO.success();
    }
    @RequestMapping(value = "/deleteType.do")
    public ResultVO deleteType(String typeId){
        AssertUtil.notBlank(typeId,"类型Id为空");
        typeService.deleteType(typeId);
        return  ResultVO.success();
    }

    @RequestMapping(value="/updateBatchType.do")
    public ResultVO updateType(UpdateTypeRequest request){
        UpdateTypeRequest.check(request);
        asyncTypeBatchService.asyncAutoBatchType(request.getTypeOld(),request.getTypeNew());
//        typeAnnotationBatchService.batchReplaceAnnotationTerm(request.getTypeOld(),request.getTypeNew());
        typeService.updateBatchTypeOnAtomicTerm(request.getTypeOld(),request.getTypeNew());
        typeService.updateBatchTypeOnTerm(request.getTypeOld(),request.getTypeNew());
        typeService.updateTypeCodeById(request.getId(),request.getTypeNew());
        return  ResultVO.success();
    }
}
