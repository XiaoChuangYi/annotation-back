package cn.malgo.annotation.web.controller.atomicterm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.Page;

import cn.malgo.annotation.common.dal.model.AnAtomicTerm;
import cn.malgo.annotation.core.service.term.AtomicTermService;
import cn.malgo.annotation.web.controller.atomicterm.request.QueryAtomicRequest;
import cn.malgo.annotation.web.result.PageVO;
import cn.malgo.annotation.web.result.ResultVO;

/**
 * @author 张钟
 * @date 2017/11/1
 */

@RestController
@RequestMapping(value = "/atomic")
public class AtomicTermController {

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

}
