package cn.malgo.annotation.web.controller.term;



import cn.malgo.annotation.common.dal.model.Term;
import cn.malgo.annotation.common.util.AssertUtil;
import cn.malgo.annotation.core.service.term.TermService;
import cn.malgo.annotation.web.controller.common.BaseController;
import cn.malgo.annotation.web.controller.term.request.AddTermRequest;
import cn.malgo.annotation.web.controller.term.request.ConditionTermRequest;
import cn.malgo.annotation.web.controller.term.request.UpdateTermRequest;
import cn.malgo.annotation.web.controller.term.result.TermGroupResult;
import cn.malgo.annotation.web.request.PageRequest;
import cn.malgo.annotation.web.result.PageVO;
import cn.malgo.annotation.web.result.ResultVO;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * Created by cjl on 2017/11/28.
 */
@RestController
@RequestMapping(value = { "/term" })
public class TermController extends BaseController {

    @Autowired
    private TermService termService;

    /**
     * 根据conceptId分页查询标准术语
     * @param request
     */
    @RequestMapping(value = {"/queryPaginationByConceptId.do"})
    public  ResultVO<PageVO<Term>> queryPaginationByConceptId(ConditionTermRequest request){
        Page<Term> page=termService.listTermByConceptId(request.getConceptId(),request.getPageNum(),request.getPageSize());
        PageVO<Term> pageVO=new PageVO(page);
        return ResultVO.success(pageVO);
    }

    /**
     *置空术语表的concept_id
     * @param id
     */
    @RequestMapping(value = {"/clearConceptIdOfTerm.do"})
    public ResultVO clearConceptIdOfTerm(int id){
        termService.clearConceptIdOfTerm(id);
        return ResultVO.success();
    }
    /**
     * 分页查询术语信息
     * @param request
     * @return
     */
    @RequestMapping(value = { "/queryAll.do" })
    public ResultVO<PageVO<Term>> queryAll(PageRequest request) {
        //分页查询
        Page<Term> page = termService.listTermByPaging(request.getPageNum(), request.getPageSize());
        PageVO<Term> pageVO = new PageVO(page);
        return ResultVO.success(pageVO);
    }

    /**
     * 分页按条件查询术语信息
     * @param request
     * @return
     */
    @RequestMapping(value = { "/queryByCondition.do" })
    public ResultVO<PageVO<Term>> queryAllByCondition(ConditionTermRequest request) {
        //分页查询
        Page<Term> page = termService.listTermByPagingCondition(request.getPageNum(), request.getPageSize(),
                request.getTermName(),request.getTermType(),request.getLabel(),request.getChecked(),request.getOriginName());
        PageVO<Term> pageVO = new PageVO(page);
        return ResultVO.success(pageVO);
    }

    /**
     * 更新术语信息
     * @param request
     * @return
     */
    @RequestMapping(value = { "/updateTerm.do" })
    public ResultVO  updateTermById(UpdateTermRequest request){
        UpdateTermRequest.check(request);
        termService.updateTerm(request.getId(),request.getConceptId(),request.getConceptName());
        return  ResultVO.success();
    }

    /**
     * 更新术语标签字段
     * @param id
     * @param label
     * @return
     */
    @RequestMapping(value = { "/updateLabelOfTerm.do" })
    public ResultVO  updateTermById(Integer id,String label){
            termService.updateTermLabel(id,label);
        return  ResultVO.success();
    }
    /**
     * 批量更新术语表conceptId字段
     */
    @RequestMapping(value = {"/updateBatchConceptIdOfTerm.do"})
    public ResultVO updateBatchConceptIdOfTerm(TermArr termArr,String conceptId){
        termService.updateBatchTermConceptId(termArr.getTermList(),conceptId);
        return ResultVO.success();
    }
    /**
     * 批量更新术语标签字段
     */
    @RequestMapping(value = {"/updateBatchLabelOfTerm.do"})
    public ResultVO updateBatchLabelOfTerm(TermArr termArr,String label){
        termService.updateBatchTermLabel(termArr.getTermList(),label);
        return  ResultVO.success();
    }
    /**
     *批量覆盖更新所选记录的标签
     */
    @RequestMapping(value = "/coverBatchTags.do")
    public ResultVO coverBatchTags(TermArr termArr,String label){
        termService.coverBatchTermLable(termArr.getTermList(),label);
        return ResultVO.success();
    }

    /**
     * 新增术语信息
     * @param request
     * @return
     */
    @RequestMapping(value = { "/addTerm.do" })
    public ResultVO  addTerm(AddTermRequest request){
        AddTermRequest.check(request);
        termService.saveTerm(request.getConceptId(),request.getPconceptId(),
                request.getConceptName(),request.getConceptCode(),request.getConceptType(),request.getOriginName(),"","ENABLE");
        return  ResultVO.success();
    }
    /**
     * 弃用术语
     * @param id
     * @return
     */
    @RequestMapping(value = { "/abandonTerm.do" })
    public ResultVO  abandonTerm(Integer id){
        if(id==null)
            AssertUtil.notNull(id,"主键ID为空");
        termService.abandonTerm(id,"DISABLE");
        return  ResultVO.success();
    }
    /**
     *删除术语
     * @param id
     */
    @RequestMapping(value = {"/deleteTerm.do"})
    public ResultVO deleteTerm(Integer id){
        AssertUtil.notNull(id,"主键ID为空");
        termService.removeTerm(id);
        return  ResultVO.success();
    }
    /**
     *获取所有的type
     */
    @RequestMapping(value = { "/getTypesOfTerm.do" })
    public ResultVO<List<String>> getTypesOfTerm(){
        List<String> typeList=termService.listDistinctTermType();
        return  ResultVO.success(typeList);
    }

    /**
     * 根据termId查询标准术语表相关联的记录
     */
    @RequestMapping(value = { "/queryAssociatedByTermId.do" })
    public ResultVO<PageVO<Term>> queryAssociatedByTermId(ConditionTermRequest request) {
        //分页查询
        Page<Term> page = termService.listTermAssociatedConceptByTermId(request.getPageNum(), request.getPageSize(),request.getTermId());
        PageVO<Term> pageVO = new PageVO(page);
        return ResultVO.success(pageVO);
    }
    /**
     *根据originName分组查询数据
     */
    @RequestMapping(value = {"/queryTermGroupByOriginName.do"})
    public ResultVO<TermGroupResult> queryTermGroupByOriginName(int groupIndex, int groupSize){
        TermGroupResult termGroupResult=new TermGroupResult();
        List<List<Term>> termList=termService.listTermGroupByOriginName(groupIndex,groupSize);
        termGroupResult.setMixList(termList);
        termGroupResult.setGroups(termService.countTermGroupsByOriginName());
        return ResultVO.success(termGroupResult);
    }
    /**
     *根据originName查询分组后的组数
     */
    @RequestMapping(value = {"/getGroupsByOriginName.do"})
    public ResultVO<Integer> getGroupsByOriginName(){
        int groups=termService.countTermGroupsByOriginName();
        return ResultVO.success(groups);
    }
}
