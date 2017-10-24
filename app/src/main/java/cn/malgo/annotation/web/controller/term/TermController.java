package cn.malgo.annotation.web.controller.term;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.malgo.annotation.core.service.term.AtomicTermService;
import cn.malgo.annotation.web.result.ResultVO;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 张钟
 * @date 2017/10/24
 */
@RestController
@RequestMapping(value = "/term")
public class TermController {

    @Autowired
    private AtomicTermService atomicTermService;

    /**
     * 批量加密原子术语库
     * @return
     */
    @RequestMapping(value = "/batchCrypt.do")
    public ResultVO batchCrypt(){
        atomicTermService.batchCrypt();
        return ResultVO.success();
    }

}
