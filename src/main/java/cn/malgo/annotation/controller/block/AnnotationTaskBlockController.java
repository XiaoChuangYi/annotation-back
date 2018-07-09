package cn.malgo.annotation.controller.block;

import cn.malgo.annotation.biz.block.AnnotationBlockResetToAnnotationBiz;
import cn.malgo.annotation.entity.UserAccount;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.request.block.ResetAnnotationBlockRequest;
import cn.malgo.annotation.result.Response;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v2/block")
public class AnnotationTaskBlockController {

  private final AnnotationBlockResetToAnnotationBiz annotationBlockResetToAnnotationBiz;

  public AnnotationTaskBlockController(
      AnnotationBlockResetToAnnotationBiz annotationBlockResetToAnnotationBiz) {
    this.annotationBlockResetToAnnotationBiz = annotationBlockResetToAnnotationBiz;
  }

  /** ANNOTATED或FINISHED状态的block可以被打回重新标注或审核 */
  @RequestMapping(value = "/reset-block-to-annotation", method = RequestMethod.POST)
  public Response resetBlockToAnnotation(
      @ModelAttribute(value = "userAccount", binding = false) UserAccount userAccount,
      @RequestBody ResetAnnotationBlockRequest resetAnnotationBlockRequest) {
    return new Response<>(
        annotationBlockResetToAnnotationBiz.process(
            resetAnnotationBlockRequest, 0, AnnotationRoleStateEnum.admin.getRole()));
  }
}
