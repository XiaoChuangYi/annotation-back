package cn.malgo.annotation.web.controller.term;



import cn.malgo.annotation.common.dal.model.Term;
import cn.malgo.annotation.common.util.AssertUtil;
import cn.malgo.annotation.core.service.term.TermService;
import cn.malgo.annotation.web.controller.common.BaseController;
import cn.malgo.annotation.web.controller.term.request.AddTermRequest;
import cn.malgo.annotation.web.controller.term.request.ConditionTermRequest;
import cn.malgo.annotation.web.controller.term.request.UpdateTermRequest;
import cn.malgo.annotation.web.request.PageRequest;
import cn.malgo.annotation.web.result.PageVO;
import cn.malgo.annotation.web.result.ResultVO;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Created by cjl on 2017/11/28.
 */
@RestController
@RequestMapping(value = { "/term" })
public class TermController extends BaseController {

    @Autowired
    private TermService termService;

    /**
     * 分页查询术语信息
     * @param request
     * @return
     */
    @RequestMapping(value = { "/queryAll.do" })
    public ResultVO<PageVO<Term>> queryAll(PageRequest request) {
        //分页查询
        Page<Term> page = termService.QueryAll(request.getPageNum(), request.getPageSize());
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
        Page<Term> page = termService.QueryAllByCondition(request.getPageNum(), request.getPageSize(),
                request.getTermName(),request.getTermType(),request.getLabel());
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
     * 新增术语信息
     * @param request
     * @return
     */
    @RequestMapping(value = { "/addTerm.do" })
    public ResultVO  addTerm(AddTermRequest request){
        AddTermRequest.check(request);
        termService.insertTerm(request.getConceptId(),request.getPconceptId(),
                request.getConceptName(),request.getConceptCode(),request.getConceptType(),request.getOriginName(),"","ENABLE");
        return  ResultVO.success();
    }
    /**
     * 删除术语信息
     * @param id
     * @return
     */
    @RequestMapping(value = { "/deleteTerm.do" })
    public ResultVO  deleteTerm(Integer id){
        if(id==null)
            AssertUtil.notNull(id,"主键ID为空");
        termService.deleteTerm(id,"DISABLE");
        return  ResultVO.success();
    }

}
