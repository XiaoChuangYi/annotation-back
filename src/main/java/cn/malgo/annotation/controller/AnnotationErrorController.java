package cn.malgo.annotation.controller;

import cn.malgo.annotation.biz.block.FindAnnotationErrorBiz;
import cn.malgo.annotation.biz.block.FixAnnotationBiz;
import cn.malgo.annotation.biz.block.SearchAnnotationBiz;
import cn.malgo.annotation.dto.error.AnnotationErrorContext;
import cn.malgo.annotation.dto.error.AnnotationWordError;
import cn.malgo.annotation.dto.error.FixAnnotationResult;
import cn.malgo.annotation.request.FindAnnotationErrorRequest;
import cn.malgo.annotation.request.FixAnnotationErrorRequest;
import cn.malgo.annotation.request.SearchAnnotationRequest;
import cn.malgo.annotation.vo.AnnotationErrorVO;
import cn.malgo.annotation.vo.FixAnnotationResponse;
import cn.malgo.service.exception.BusinessRuleException;
import cn.malgo.service.model.Response;
import cn.malgo.service.model.UserDetails;
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
      FindAnnotationErrorRequest request,
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount) {
    final List<AnnotationWordError> errors = findAnnotationErrorBiz.process(request, userAccount);
    if (errors.size() != 0 && request.getErrorIndex() >= errors.size()) {
      throw new BusinessRuleException("index-exceed", "索引超出数据范围");
    }
    return new Response<>(
        new AnnotationErrorVO(
            errors != null && errors.size() > 0
                ? errors.subList(request.getErrorIndex(), (request.getErrorIndex() + 1))
                : errors,
            errors.size()));
  }

  @RequestMapping(value = "/annotation/search", method = RequestMethod.GET)
  public Response<List<AnnotationErrorContext>> searchAnnotations(
      SearchAnnotationRequest request,
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount) {
    return new Response<>(searchAnnotationBiz.process(request, userAccount));
  }

  @RequestMapping(value = "/annotation/fix-errors", method = RequestMethod.POST)
  public Response<FixAnnotationResponse> fixAnnotationError(
      @RequestBody FixAnnotationErrorRequest request,
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount) {
    final List<FixAnnotationResult> results = this.fixAnnotationBiz.process(request, userAccount);
    return new Response<>(new FixAnnotationResponse(results));
  }
}
