package cn.malgo.annotation.web.controller.conceptController;

import cn.malgo.annotation.common.dal.model.Concept;
import cn.malgo.annotation.core.business.mixtrue.MixtureTerm;
import cn.malgo.annotation.core.service.atomicTerm.AtomicTermService;
import cn.malgo.annotation.core.service.concept.ConceptService;
import cn.malgo.annotation.core.service.term.TermService;
import cn.malgo.annotation.web.controller.common.BaseController;
import cn.malgo.annotation.web.controller.term.request.AddConceptRequest;
import cn.malgo.annotation.web.controller.term.request.QueryConceptRequest;
import cn.malgo.annotation.web.result.PageVO;
import cn.malgo.annotation.web.result.ResultVO;
import com.github.pagehelper.Page;
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
    private ConceptService conceptService;

    @Autowired
    private TermService termService;

    @Autowired
    private AtomicTermService atomicTermService;



    /**
     * 新增concept信息
     * @param request
     * @return
     */
    @RequestMapping(value = { "/addConcept.do" })
    public ResultVO addNewConcept(AddConceptRequest request){
        AddConceptRequest.check(request);
        conceptService.saveConcept(request.getId(),request.getOriginName());
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
        conceptService.updateTermOrSaveConcept(id,conceptId,conceptName);
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
        conceptService.updateConceptStandardName(newStandName,conceptId);
        return  ResultVO.success();
    }
    /**
     * 查询concept信息
     */
    @RequestMapping(value = { "/queryAllConcept.do" })
    public ResultVO<List<Concept>> selectAllConcept(){
        List<Concept> conceptList=conceptService.listConcept();
        return ResultVO.success(conceptList);
    }

    /**
     *分页查询concept
     */
    @RequestMapping(value = {"/queryConceptPagination.do"})
    public ResultVO<PageVO<Concept>> selectConceptPagination(QueryConceptRequest request){
        Page<Concept> page = conceptService.listConceptByPagingCondition(request.getPageIndex(), request.getPageSize(),request.getStandardName());
        PageVO<Concept> pageVO = new PageVO(page);
        return ResultVO.success(pageVO);
    }
    @RequestMapping(value = {"/getSingleConcept.do"})
    public ResultVO<Concept> selectAllConcept(String conceptId){
        Concept concept=conceptService.getConceptByConceptId(conceptId);
        return ResultVO.success(concept);
    }
    /**
     * 根据term搜索原子术语表和术语表中的相关的记录
     */
    @RequestMapping(value = {"/queryTermOrAtomicTermByTerm.do"})
    public ResultVO<List<MixtureTerm>> queryTermOrAtomicTermByTerm(String termText){
        //术语表直接模糊查询数据库
        List<MixtureTerm> mixtureTermList=termService.listMixtureTermByTermName(termText);
        List<MixtureTerm> finalTermList=atomicTermService.listMixtureTermByCondition(mixtureTermList,termText);
        return  ResultVO.success(finalTermList);
    }
}
