package cn.malgo.annotation.web.controller.task;

import cn.malgo.annotation.common.util.AssertUtil;
import cn.malgo.annotation.core.service.annotation.AnnotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import cn.malgo.annotation.common.dal.model.CrmAccount;
import cn.malgo.annotation.core.service.annotation.AnnotationBatchService;
import cn.malgo.annotation.core.service.corpus.AtomicTermBatchService;
import cn.malgo.annotation.core.service.corpus.CorpusService;
import cn.malgo.annotation.web.controller.common.BaseController;
import cn.malgo.annotation.web.result.ResultVO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 张钟
 * @date 2017/10/24
 */
@RestController
@RequestMapping(value = "/task")
public class TaskController extends BaseController {

    @Autowired
    private AtomicTermBatchService atomicTermBatchService;

    @Autowired
    private AnnotationBatchService annotationBatchService;

    @Autowired
    private CorpusService corpusService;

    @Autowired
    private AnnotationService annotationService;

    /**
     * 根据anId解密单条annotation表的final_annotation，auto_annotation，manual_annotation字段
     * @return
     */
    @RequestMapping(value = "/singleDecryptAnnotationByAnId.do/{anId}",method = RequestMethod.GET)
    public ResultVO singleDecryptAnnotationByAnId(@PathVariable String anId){
        annotationService.updateSingleDecryptAnnotation(anId);
        return ResultVO.success();
    }
    /**
     * 批量解密annotation表的final_annotation，auto_annotation，manual_annotation字段
     * @return
     */
     @RequestMapping(value = "/batchDecryptAnnotation.do")
     public ResultVO batchDecryptAnnotation(){
         new Thread(new Runnable() {
             @Override
             public void run() {
                 annotationBatchService.batchDecryptAnnotation();
             }
         }).start();
         return ResultVO.success();
     }

    /**
     * 批量加密原子术语库
     * @return
     */
    @RequestMapping(value = "/batchCrypt.do")
    public ResultVO batchCrypt() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                atomicTermBatchService.batchCrypt();
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
                corpusService.batchAutoAnnotation();
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
                annotationBatchService.updateUNEncryptedAnnotation(crmAccount.getId());
            }
        }).start();

        return ResultVO.success();

    }

    /**
     * 批量检查标注是否存在二义性
     * @return
     */
    @RequestMapping(value = "/batchCheckAmbiguity.do")
    public ResultVO batchCheckAmbiguity(String state) {
        AssertUtil.notBlank(state,"状态为空");
        List<String> stateList = new ArrayList<>();
        stateList.add(state);

        new Thread(new Runnable() {
            @Override
            public void run() {
                annotationBatchService.batchCheckAmbiguityAndAtomicTerm(stateList);
            }
        }).start();

        return ResultVO.success();
    }

    /**
     * 批量检查标注和原子术语的关联关系
     * @return
     */
    @RequestMapping(value = "/batchCheckRelation.do")
    public ResultVO batchCheckRelation() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                atomicTermBatchService.batchCheckRelation();
            }
        }).start();

        return ResultVO.success();
    }

}
