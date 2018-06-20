package com.malgo.controller;

import com.malgo.biz.FindAnnotationErrorBiz;
import com.malgo.entity.UserAccount;
import com.malgo.request.GetAnnotationErrorRequest;
import com.malgo.result.Response;
import com.malgo.vo.AnnotationErrorVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v2")
@Slf4j
public class AnnotationErrorController extends BaseController {
  private final FindAnnotationErrorBiz findAnnotationErrorBiz;

  public AnnotationErrorController(FindAnnotationErrorBiz findAnnotationErrorBiz) {
    this.findAnnotationErrorBiz = findAnnotationErrorBiz;
  }

  @RequestMapping(value = "/annotation/errors", method = RequestMethod.GET)
  public Response<AnnotationErrorVO> getAnnotationErrors(
      GetAnnotationErrorRequest request,
      @ModelAttribute(value = "userAccount", binding = false) UserAccount userAccount) {
    return new Response<>(
        new AnnotationErrorVO(
            findAnnotationErrorBiz
                .process(request, userAccount.getId(), userAccount.getRoleId())
                .subList(0, 1)));
  }
}
