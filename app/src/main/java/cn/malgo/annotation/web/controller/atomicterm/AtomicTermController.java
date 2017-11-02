package cn.malgo.annotation.web.controller.atomicterm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.Page;

import cn.malgo.annotation.common.dal.model.AnAtomicTerm;
import cn.malgo.annotation.core.service.term.AtomicTermService;
import cn.malgo.annotation.web.controller.atomicterm.request.ChangeAtomicTermRequest;
import cn.malgo.annotation.web.controller.atomicterm.request.QueryAtomicRequest;
import cn.malgo.annotation.web.controller.common.BaseController;
import cn.malgo.annotation.web.result.PageVO;
import cn.malgo.annotation.web.result.ResultVO;

/**
 * @author 张钟
 * @date 2017/11/1
 */

@RestController
@RequestMapping(value = "/atomic")
public class AtomicTermController extends BaseController {

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
            request.getType(), request.getPageNum(), request.getPageSize());
        PageVO<AnAtomicTerm> pageVO = new PageVO(page);

        return ResultVO.success(pageVO);
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

        //更新原子术语
        atomicTermService.updateAtomicTerm(request.getAtomicTermId(), request.getType());

        AnAtomicTerm anAtomicTermNew = atomicTermService
            .queryByAtomicTermId(request.getAtomicTermId());

        new Thread(new Runnable() {
            @Override
            public void run() {
                //批处理
                atomicTermService.batchReplaceAtomicTerm(anAtomicTermOld,anAtomicTermNew);
            }
        }).start();

        return ResultVO.success();

    }

}
