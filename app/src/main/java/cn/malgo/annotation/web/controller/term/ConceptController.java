package cn.malgo.annotation.web.controller.term;

import cn.malgo.annotation.common.dal.model.Concept;
import cn.malgo.annotation.core.service.concept.MixtureService;
import cn.malgo.annotation.web.controller.common.BaseController;
import cn.malgo.annotation.web.controller.term.request.AddConceptRequest;
import cn.malgo.annotation.web.result.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by cjl on 2017/11/29.
 */
@RestController
@RequestMapping(value = { "/concept" })
public class ConceptController extends BaseController {

    @Autowired
    private MixtureService mixtureService;


    /**
     * 新增concept信息
     * @param request
     * @return
     */
    @RequestMapping(value = { "/addConcept.do" })
    public ResultVO addNewConcept(AddConceptRequest request){
        AddConceptRequest.check(request);
        mixtureService.addNewConcept(request.getId(),request.getOriginName());
        return  ResultVO.success();
    }
    /**
     * 更新concept信息
     * @param id
     * @param conceptId
     * @param conceptName
     * @return
     */
    @RequestMapping(value = { "/updateConcept.do" })
    public ResultVO updateTermOrAddConcept(int id,String conceptId,String conceptName){
        mixtureService.updateTermOrAddConcept(id,conceptId,conceptName);
        return  ResultVO.success();
    }
    /**
     * 更新standName
     * @param newStandName
     * @param conceptId
     * @return
     */
    @RequestMapping(value = { "/updateStandName.do" })
    public ResultVO updateConceptName(String newStandName,String conceptId){
        mixtureService.updateStandNameofConcept(newStandName,conceptId);
        return  ResultVO.success();
    }
    /**
     * 查询concept信息
     */
    @RequestMapping(value = { "/queryAllConcept.do" })
    public ResultVO<List<Concept>> selectAllConcept(){
        List<Concept> conceptList=mixtureService.selectAllConcept();
        return ResultVO.success(conceptList);
    }
    @RequestMapping(value = {"/getSingleConcept.do"})
    public ResultVO<Concept> selectAllConcept(String conceptId){
        Concept concept=mixtureService.selectOneConcept(conceptId);
        return ResultVO.success(concept);
    }
}
