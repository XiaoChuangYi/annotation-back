package cn.malgo.annotation.controller.block;

import cn.malgo.annotation.biz.block.AnnotationBlockResetToAnnotationBiz;
import cn.malgo.annotation.biz.brat.block.AddBlockAnnotationBiz;
import cn.malgo.annotation.biz.brat.block.DeleteBlockAnnotationBiz;
import cn.malgo.annotation.biz.brat.block.GetAnnotationBlockBiz;
import cn.malgo.annotation.controller.BaseController;
import cn.malgo.annotation.dto.UserDetails;
import cn.malgo.annotation.request.block.ResetAnnotationBlockRequest;
import cn.malgo.annotation.request.brat.AddAnnotationGroupRequest;
import cn.malgo.annotation.request.brat.BaseAnnotationRequest;
import cn.malgo.annotation.request.brat.DeleteAnnotationGroupRequest;
import cn.malgo.annotation.request.brat.GetAutoAnnotationRequest;
import cn.malgo.annotation.result.Response;
import cn.malgo.annotation.vo.AnnotationBlockBratVO;
import cn.malgo.annotation.vo.ResetBlockToAnnotationResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v2/block")
public class AnnotationTaskBlockController extends BaseController {

  private final AnnotationBlockResetToAnnotationBiz annotationBlockResetToAnnotationBiz;
  private final GetAnnotationBlockBiz getAnnotationBlockBiz;
  private final AddBlockAnnotationBiz addBlockAnnotationBiz;
  private final DeleteBlockAnnotationBiz deleteBlockAnnotationBiz;

  public AnnotationTaskBlockController(
      AnnotationBlockResetToAnnotationBiz annotationBlockResetToAnnotationBiz,
      GetAnnotationBlockBiz getAnnotationBlockBiz,
      AddBlockAnnotationBiz addBlockAnnotationBiz,
      DeleteBlockAnnotationBiz deleteBlockAnnotationBiz) {
    this.annotationBlockResetToAnnotationBiz = annotationBlockResetToAnnotationBiz;
    this.getAnnotationBlockBiz = getAnnotationBlockBiz;
    this.addBlockAnnotationBiz = addBlockAnnotationBiz;
    this.deleteBlockAnnotationBiz = deleteBlockAnnotationBiz;
  }

  /** ANNOTATED或FINISHED状态的block可以被打回重新标注或审核 */
  @RequestMapping(value = "/reset-block-to-annotation", method = RequestMethod.POST)
  public Response<ResetBlockToAnnotationResponse> resetBlockToAnnotation(
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount,
      @RequestBody ResetAnnotationBlockRequest resetAnnotationBlockRequest) {
    return new Response<>(
        new ResetBlockToAnnotationResponse(
            annotationBlockResetToAnnotationBiz.process(resetAnnotationBlockRequest, userAccount)));
  }

  /** 获取block标注 */
  @RequestMapping(value = "/get-block-annotation/{id}", method = RequestMethod.GET)
  public Response<AnnotationBlockBratVO> getBlockAnnotation(
      @PathVariable("id") int id,
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount) {
    return new Response<>(
        getAnnotationBlockBiz.process(
            new GetAutoAnnotationRequest(id), userAccount.getId(), userAccount.getRoleId()));
  }

  /** 新增block标注 */
  @RequestMapping(value = "/add-block-annotation", method = RequestMethod.POST)
  public Response<AnnotationBlockBratVO> addBlockAnnotation(
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount,
      @RequestBody AddAnnotationGroupRequest addAnnotationGroupRequest) {
    return new Response<>(
        addBlockAnnotationBiz.process(
            addAnnotationGroupRequest, userAccount.getId(), userAccount.getRoleId()));
  }

  /** 删除block标注 */
  @RequestMapping(value = "delete-block-annotation", method = RequestMethod.POST)
  public Response<AnnotationBlockBratVO> deleteBlockAnnotation(
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount,
      @RequestBody DeleteAnnotationGroupRequest deleteAnnotationGroupRequest) {
    return new Response<>(
        deleteBlockAnnotationBiz.process(
            deleteAnnotationGroupRequest, userAccount.getId(), userAccount.getRoleId()));
  }
}
