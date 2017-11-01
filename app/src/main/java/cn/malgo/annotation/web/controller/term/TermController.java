package cn.malgo.annotation.web.controller.term;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.malgo.annotation.common.dal.model.CrmAccount;
import cn.malgo.annotation.core.service.annotation.AnnotationService;
import cn.malgo.annotation.core.service.term.AtomicTermService;
import cn.malgo.annotation.core.service.term.TermService;
import cn.malgo.annotation.web.controller.common.BaseController;
import cn.malgo.annotation.web.result.ResultVO;

/**
 * @author 张钟
 * @date 2017/10/24
 */
@RestController
@RequestMapping(value = "/task")
public class TermController extends BaseController {

    @Autowired
    private AtomicTermService atomicTermService;

    @Autowired
    private AnnotationService annotationService;

    @Autowired
    private TermService       termService;

    /**
     * 批量加密原子术语库
     * @return
     */
    @RequestMapping(value = "/batchCrypt.do")
    public ResultVO batchCrypt() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                atomicTermService.batchCrypt();
            }
        }).start();
        return ResultVO.success();
    }

    /**
     * 批量自动标注
     * @return
     */
    @RequestMapping(value = "/batchAutoAnnotation.do")
    public ResultVO batchAutoAnnotation() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                termService.batchAutoAnnotation();
            }
        }).start();
        return ResultVO.success();
    }

    /**
     * 批量加密标注信息
     * @return
     */
    @RequestMapping(value = "/encrypt.do")
    public ResultVO encryptAnnotation(@ModelAttribute("currentAccount") CrmAccount crmAccount) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                annotationService.updateUNEncryptedAnnotation(crmAccount.getId());
            }
        }).start();

        return ResultVO.success();

    }

    /**
     * 批量检查标注是否存在二义性
     * @return
     */
    @RequestMapping(value = "/batchCheckAmbiguity.do")
    public ResultVO batchCheckAmbiguity() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                annotationService.batchCheckAmbiguity();
            }
        }).start();

        return ResultVO.success();
    }


    /**
     * 批量检查标注和原子术语的关联关系
     * @return
     */
    @RequestMapping(value = "/batchCheckRelation.do")
    public ResultVO batchCheckRelation(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                atomicTermService.batchCheckRelation();
            }
        }).start();

        return ResultVO.success();
    }

}
