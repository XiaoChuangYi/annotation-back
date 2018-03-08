package cn.malgo.annotation.web.controller.atomicterm;

import cn.malgo.annotation.common.dal.model.AnAtomicTerm;
import cn.malgo.annotation.common.dal.model.Concept;
import cn.malgo.annotation.common.util.AssertUtil;
import cn.malgo.annotation.core.service.atomicTerm.AtomicTermService;
import cn.malgo.annotation.core.service.concept.AtomicConceptService;
import cn.malgo.annotation.core.service.concept.ConceptService;
import cn.malgo.annotation.web.controller.atomicterm.request.QueryAtomicRequest;
import cn.malgo.annotation.web.controller.atomicterm.request.addAtomicConceptRequest;
import cn.malgo.annotation.web.controller.type.PageResult;
import cn.malgo.annotation.web.result.PageVO;
import cn.malgo.annotation.web.result.ResultVO;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by cjl on 2017/11/30.
 */
@RestController
@RequestMapping(value = "/atomic/concept")
public class AtomicConceptController {

    @Autowired
    private AtomicConceptService atomicConceptService;

    @Autowired
    private ConceptService conceptService;

    @Autowired
    private AtomicTermService atomicTermService;


    /**
     * 分页查询原子术语
     * @param request
     * @return
     */
    @RequestMapping(value = "/list.do")
    public ResultVO<PageVO<AnAtomicTerm>> getOnePage(QueryAtomicRequest request) {
        Page<AnAtomicTerm> page = atomicTermService.listAnAtomicTermByPagingCondition(request.getTerm(),
                request.getType(), request.getId(),request.getPageNum(), request.getPageSize(),request.getChecked());
        PageVO<AnAtomicTerm> pageVO = new PageVO(page);
        return ResultVO.success(pageVO);
    }
    /**
     *分页根据term模糊查询原子术语
     * @param request
     * @return
     */
    @RequestMapping(value = "/fuzzyList.do")
    public ResultVO<PageVO<AnAtomicTerm>> fuzzyQueryPage(QueryAtomicRequest request){
        Map<String,Object> atomicTermMap=atomicTermService.mapAnAtomicTermByPagingCondition(request.getTerm(),request.getPageNum(),request.getPageSize(),request.getChecked());
        PageResult<AnAtomicTerm> pageResult=new PageResult<>();
        pageResult.setTotal(Integer.parseInt(atomicTermMap.get("total").toString()));
        pageResult.setDataList((List<AnAtomicTerm>)atomicTermMap.get("atomicTermList"));
        return  ResultVO.success(pageResult);
    }

    /**
     * 新增concept信息
     * @param request
     * @return
     */
    @RequestMapping(value = { "/addConcept.do" })
    public ResultVO addNewConcept(addAtomicConceptRequest request){
        addAtomicConceptRequest.check(request);
        atomicConceptService.addNewConceptAndUpdateAtomicTerm(request.getId(),request.getOriginName());
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
    public ResultVO updateTermOrAddConcept(String id,String conceptId,String conceptName){
        atomicConceptService.updateAtomicTermOrAddConcept(id,conceptId,conceptName);
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
    @RequestMapping(value = {"/getSingleConcept.do"})
    public ResultVO<Concept> selectAllConcept(String conceptId){
        Concept concept=conceptService.getConceptByConceptId(conceptId);
        return ResultVO.success(concept);
    }
    /**
     * 遗弃原子术语信息
     * @param id
     * @return
     */
    @RequestMapping(value = { "/abandonAtomicTerm.do" })
    public ResultVO  abandonAtomicTerm(String id){
        AssertUtil.notBlank(id,"主键ID为空");
        atomicTermService.abandonAtomicTerm(id,"DISABLE");
        return  ResultVO.success();
    }


}
