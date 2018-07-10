package cn.malgo.annotation.controller.block;

import cn.malgo.annotation.biz.block.AnnotationBlockResetToAnnotationBiz;
import cn.malgo.annotation.controller.BaseController;
import cn.malgo.annotation.dto.UserDetails;
import cn.malgo.annotation.request.block.ResetAnnotationBlockRequest;
import cn.malgo.annotation.result.Response;
import cn.malgo.annotation.vo.ResetBlockToAnnotationResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v2/block")
public class AnnotationTaskBlockController extends BaseController {
  private final AnnotationBlockResetToAnnotationBiz annotationBlockResetToAnnotationBiz;

  public AnnotationTaskBlockController(
      AnnotationBlockResetToAnnotationBiz annotationBlockResetToAnnotationBiz) {
    this.annotationBlockResetToAnnotationBiz = annotationBlockResetToAnnotationBiz;
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
}
