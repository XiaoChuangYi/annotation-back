package cn.malgo.annotation.controller;

import cn.malgo.annotation.biz.FindAnnotationErrorBiz;
import cn.malgo.annotation.biz.FixAnnotationBiz;
import cn.malgo.annotation.biz.SearchAnnotationBiz;
import cn.malgo.annotation.dto.AnnotationErrorContext;
import cn.malgo.annotation.dto.AnnotationWordError;
import cn.malgo.annotation.dto.FixAnnotationResult;
import cn.malgo.annotation.entity.UserAccount;
import cn.malgo.annotation.request.FixAnnotationErrorRequest;
import cn.malgo.annotation.request.GetAnnotationErrorRequest;
import cn.malgo.annotation.request.SearchAnnotationRequest;
import cn.malgo.annotation.result.Response;
import cn.malgo.annotation.vo.AnnotationErrorVO;
import cn.malgo.annotation.vo.FixAnnotationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v2")
@Slf4j
public class AnnotationErrorController extends BaseController {
  private final FindAnnotationErrorBiz findAnnotationErrorBiz;
  private final FixAnnotationBiz fixAnnotationBiz;
  private final SearchAnnotationBiz searchAnnotationBiz;

  public AnnotationErrorController(
      final FindAnnotationErrorBiz findAnnotationErrorBiz,
      final FixAnnotationBiz fixAnnotationBiz,
      final SearchAnnotationBiz searchAnnotationBiz) {
    this.findAnnotationErrorBiz = findAnnotationErrorBiz;
    this.fixAnnotationBiz = fixAnnotationBiz;
    this.searchAnnotationBiz = searchAnnotationBiz;
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

  @RequestMapping(value = "/annotation/search", method = RequestMethod.GET)
  public Response<List<AnnotationErrorContext>> searchAnnotations(
      SearchAnnotationRequest request,
      @ModelAttribute(value = "userAccount", binding = false) UserAccount userAccount) {
    return new Response<>(
        searchAnnotationBiz.process(request, userAccount.getId(), userAccount.getRoleId()));
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
