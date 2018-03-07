package cn.malgo.annotation.web.controller.atomicterm;

import cn.malgo.annotation.common.util.AssertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.Page;

import cn.malgo.annotation.common.dal.model.AnAtomicTerm;
import cn.malgo.annotation.core.service.corpus.AtomicTermBatchService;
import cn.malgo.annotation.core.service.corpus.AtomicTermService;
import cn.malgo.annotation.web.controller.atomicterm.request.ChangeAtomicTermRequest;
import cn.malgo.annotation.web.controller.atomicterm.request.QueryAtomicRequest;
import cn.malgo.annotation.web.controller.common.BaseController;
import cn.malgo.annotation.web.result.PageVO;
import cn.malgo.annotation.web.result.ResultVO;

import java.util.List;

/**
 * @author 张钟
 * @date 2017/11/1
 */

@RestController
@RequestMapping(value = "/atomic")
public class AtomicTermController extends BaseController {

    @Autowired
    private AtomicTermService      atomicTermService;

    @Autowired
    private AtomicTermBatchService atomicTermBatchService;

    /**
     * 分页查询原子术语
     * @param request
     * @return
     */
    @RequestMapping(value = "/list.do")
    public ResultVO<PageVO<AnAtomicTerm>> getOnePage(QueryAtomicRequest request) {

        Page<AnAtomicTerm> page = atomicTermService.queryOnePage(request.getTerm(),
            request.getType(), request.getPageNum(), request.getPageSize());
        PageVO<AnAtomicTerm> pageVO = new PageVO(page);

        return ResultVO.success(pageVO);
    }
    /**
     * 批量更新术语表conceptId字段
     * @param atomicTermArr
     * @param conceptId
     */
    @RequestMapping(value = {"/updateBatchConceptIdOfAtomicTerm.do"})
    public ResultVO updateBatchConceptIdOfAtomicTerm(AtomicTermArr atomicTermArr,String conceptId){
        atomicTermService.updateBatchConceptIdOfAtomicTerm(atomicTermArr.getAtomicTermList(),conceptId);
        return ResultVO.success();
    }
    /**
     * 根据conceptId分页查询原子术语
     * @param request
     */
    @RequestMapping(value = "/queryPaginationByConceptId.do")
    public ResultVO<PageVO<AnAtomicTerm>> queryPaginationByConceptId(QueryAtomicRequest request) {

        Page<AnAtomicTerm> page = atomicTermService.queryOnePageByConceptId(request.getConceptId(), request.getPageNum(), request.getPageSize());
        PageVO<AnAtomicTerm> pageVO = new PageVO(page);
        return ResultVO.success(pageVO);
    }

    /**
     * 置空原子术语表的concept_id
     * @param id
     */
    @RequestMapping(value = "/clearConceptIdOfAtomicTerm.do")
    public  ResultVO clearConceptIdOfAtomicTerm(String id){
        atomicTermService.clearConceptIdOfAtomicTerm(id);
        return  ResultVO.success();
    }

    /**
     * 修改原子术语
     * @param request
     * @return
     */
    @RequestMapping(value = "/change.do")
    public ResultVO changeAtomicTerm(ChangeAtomicTermRequest request) {

        //基础参数检查
        ChangeAtomicTermRequest.check(request);

        //提交的原子术语未做任何改动,直接返回
        AnAtomicTerm anAtomicTermOld = atomicTermService
            .queryByAtomicTermId(request.getAtomicTermId());
        if (anAtomicTermOld.getType().equals(request.getType())) {
            return ResultVO.success();
        }

        //更新原子术语,先更新原子术语表中的单个数据
        atomicTermService.updateAtomicTerm(request.getAtomicTermId(), request.getType());

        AnAtomicTerm anAtomicTermNew = atomicTermService
            .queryByAtomicTermId(request.getAtomicTermId());

        atomicTermBatchService.batchReplaceAtomicTerm(anAtomicTermOld, anAtomicTermNew);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                //批处理
//                atomicTermBatchService.batchReplaceAtomicTerm(anAtomicTermOld, anAtomicTermNew);
//            }
//        }).start();

        return ResultVO.success();

    }
    /**
     * 删除原子术语，并删除对应的标注
     * @param id
     * @param term
     * @param type
     */
    @RequestMapping(value = {"/deleteAtomicTerm.do"})
    public ResultVO deleteAtomicTerm(String id,String term,String type){
        AssertUtil.notBlank(id,"主键ID为空");
        AssertUtil.notBlank(term,"term为空");
        AssertUtil.notBlank(type,"type为空");
        atomicTermBatchService.deleteAtomicTerm(id,term,type);
        return  ResultVO.success();
    }
    /**
     *查询原子术语用来初始化下拉框
     */
    @RequestMapping(value = {"/queryAtomicTermForInitSelectBox.do"})
    public ResultVO<List<AnAtomicTerm>> queryAtomicTermForInitSelectBox(String term){
        List<AnAtomicTerm> anAtomicTermList=atomicTermService.queryEnableAtomicTerm(term);
        return  ResultVO.success(anAtomicTermList);
    }

}
