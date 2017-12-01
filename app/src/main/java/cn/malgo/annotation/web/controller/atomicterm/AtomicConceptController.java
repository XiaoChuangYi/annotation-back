package cn.malgo.annotation.web.controller.atomicterm;

import cn.malgo.annotation.common.dal.model.AnAtomicTerm;
import cn.malgo.annotation.common.dal.model.Concept;
import cn.malgo.annotation.common.util.AssertUtil;
import cn.malgo.annotation.core.service.concept.AtomicConceptService;
import cn.malgo.annotation.core.service.corpus.AtomicTermService;
import cn.malgo.annotation.web.controller.atomicterm.request.QueryAtomicRequest;
import cn.malgo.annotation.web.controller.term.request.AddConceptRequest;
import cn.malgo.annotation.web.result.PageVO;
import cn.malgo.annotation.web.result.ResultVO;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by cjl on 2017/11/30.
 */
@RestController
@RequestMapping(value = "/atomic/concept")
public class AtomicConceptController {

    @Autowired
    private AtomicConceptService atomicConceptService;

    @Autowired
    private AtomicTermService atomicTermService;
    /**
     * 分页查询原子术语
     * @param request
     * @return
     */
    @RequestMapping(value = "/list.do")
    public ResultVO<PageVO<AnAtomicTerm>> getOnePage(QueryAtomicRequest request) {

        Page<AnAtomicTerm> page = atomicTermService.queryOnePage(request.getTerm(),
                request.getType(), request.getPageNum(), request.getPageSize(),request.getChecked());
        PageVO<AnAtomicTerm> pageVO = new PageVO(page);

        return ResultVO.success(pageVO);
    }

    /**
     * 新增concept信息
     * @param request
     * @return
     */
    @RequestMapping(value = { "/addConcept.do" })
    public ResultVO addNewConcept(AddConceptRequest request){
        AddConceptRequest.check(request);
        atomicConceptService.addNewConceptAndUpdateAntomic(request.getId().toString(),request.getOriginName());
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
        atomicConceptService.updateAtomicTermOrAddConcept(id+"",conceptId,conceptName);
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
        atomicConceptService.updateStandNameofConcept(newStandName,conceptId);
        return  ResultVO.success();
    }
    /**
     * 查询concept信息
     */
    @RequestMapping(value = { "/queryAllConcept.do" })
    public ResultVO<List<Concept>> selectAllConcept(){
        List<Concept> conceptList=atomicConceptService.selectAllConcept();
        return ResultVO.success(conceptList);
    }
    @RequestMapping(value = {"/getSingleConcept.do"})
    public ResultVO<Concept> selectAllConcept(String conceptId){
        Concept concept=atomicConceptService.selectOneConcept(conceptId);
        return ResultVO.success(concept);
    }
    /**
     * 删除原子术语信息
     * @param id
     * @return
     */
    @RequestMapping(value = { "/deleteTerm.do" })
    public ResultVO  deleteTerm(String id){
        AssertUtil.notBlank(id,"主键ID为空");
        atomicConceptService.deleteAtomicTerm(id,"DISABLE");
        return  ResultVO.success();
    }
}
