package com.malgo.controller;

import com.malgo.biz.FindAnnotationErrorBiz;
import com.malgo.biz.FixAnnotationBiz;
import com.malgo.dto.AnnotationWordError;
import com.malgo.dto.FixAnnotationResult;
import com.malgo.entity.UserAccount;
import com.malgo.request.FixAnnotationErrorRequest;
import com.malgo.request.GetAnnotationErrorRequest;
import com.malgo.result.Response;
import com.malgo.vo.AnnotationErrorVO;
import com.malgo.vo.FixAnnotationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v2")
@Slf4j
public class AnnotationErrorController extends BaseController {
  private final FindAnnotationErrorBiz findAnnotationErrorBiz;
  private final FixAnnotationBiz fixAnnotationBiz;

  public AnnotationErrorController(
      final FindAnnotationErrorBiz findAnnotationErrorBiz,
      final FixAnnotationBiz fixAnnotationBiz) {
    this.findAnnotationErrorBiz = findAnnotationErrorBiz;
    this.fixAnnotationBiz = fixAnnotationBiz;
  }

  @RequestMapping(value = "/annotation/errors", method = RequestMethod.GET)
  public Response<AnnotationErrorVO> getAnnotationErrors(
      GetAnnotationErrorRequest request,
      @ModelAttribute(value = "userAccount", binding = false) UserAccount userAccount) {
    final List<AnnotationWordError> errors =
        findAnnotationErrorBiz.process(request, userAccount.getId(), userAccount.getRoleId());
    return new Response<>(
        new AnnotationErrorVO(errors != null && errors.size() > 0 ? errors.subList(0, 1) : errors));
  }

  @RequestMapping(value = "/annotation/fix-errors", method = RequestMethod.POST)
  public Response<FixAnnotationResponse> fixAnnotationError(
      @RequestBody FixAnnotationErrorRequest request,
      @ModelAttribute(value = "userAccount", binding = false) UserAccount userAccount) {
    final List<FixAnnotationResult> results =
        this.fixAnnotationBiz.process(request, userAccount.getId(), userAccount.getRoleId());
    return new Response<>(new FixAnnotationResponse(results));
  }
}
