package cn.malgo.annotation.controller;

import cn.malgo.annotation.biz.*;
import cn.malgo.annotation.biz.brat.ListAnTypeBiz;
import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.entity.UserAccount;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.exception.BusinessRuleException;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.*;
import cn.malgo.annotation.result.PageVO;
import cn.malgo.annotation.result.Response;
import cn.malgo.annotation.vo.AnnotationCombineBratVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v2")
@Slf4j
public class AnnotationCombineController extends BaseController {
  private final String secretKey;
  private final ListAnnotationBiz listAnnotationBiz;
  private final DesignateAnnotationBiz designateAnnotationBiz;
  private final GetAnnotationSummaryBiz getAnnotationSummaryBiz;
  private final GetAnnotationSummaryByAssigneeBiz getAnnotationSummaryByAssigneeBiz;
  private final RandomDesignateAnnotationBiz randomDesignateAnnotationBiz;
  private final CountAnnotationBiz countAnnotationBiz;
  private final ListAnTypeBiz listAnTypeBiz;
  private final AnnotationCombineRepository annotationCombineRepository;

  public AnnotationCombineController(
      @Value("${malgo.internal.secret-key}") String secretKey,
      ListAnnotationBiz listAnnotationBiz,
      DesignateAnnotationBiz designateAnnotationBiz,
      GetAnnotationSummaryBiz getAnnotationSummaryBiz,
      GetAnnotationSummaryByAssigneeBiz getAnnotationSummaryByAssigneeBiz,
      RandomDesignateAnnotationBiz randomDesignateAnnotationBiz,
      CountAnnotationBiz countAnnotationBiz,
      ListAnTypeBiz listAnTypeBiz,
      AnnotationCombineRepository annotationCombineRepository) {
    this.secretKey = secretKey;
    this.listAnnotationBiz = listAnnotationBiz;
    this.designateAnnotationBiz = designateAnnotationBiz;
    this.getAnnotationSummaryBiz = getAnnotationSummaryBiz;
    this.randomDesignateAnnotationBiz = randomDesignateAnnotationBiz;
    this.countAnnotationBiz = countAnnotationBiz;
    this.listAnTypeBiz = listAnTypeBiz;
    this.getAnnotationSummaryByAssigneeBiz = getAnnotationSummaryByAssigneeBiz;
    this.annotationCombineRepository = annotationCombineRepository;
  }

  /** 条件，分页查询annotation列表 */
  @RequestMapping(value = "/list-annotation", method = RequestMethod.GET)
  public Response<PageVO<AnnotationCombineBratVO>> listAnnotationCombine(
      ListAnnotationCombineRequest annotationCombineQuery,
      @ModelAttribute(value = "userAccount", binding = false) UserAccount userAccount) {
    return new Response<>(
        listAnnotationBiz.process(
            annotationCombineQuery, userAccount.getId(), userAccount.getRoleId()));
  }

  /** 根据Annotation的idList，以及用户id，批量指派给特定的用户 */
  @RequestMapping(value = "/designate-task-annotation", method = RequestMethod.POST)
  public Response designateTaskAnnotation(
      @RequestBody DesignateAnnotationRequest designateAnnotationRequest,
      @ModelAttribute(value = "userAccount", binding = false) UserAccount userAccount) {
    return new Response<>(
        designateAnnotationBiz.process(
            designateAnnotationRequest, userAccount.getId(), userAccount.getRoleId()));
  }

  /** 根据用户Id集合userIdList,以及设定的随机指派数num，标注类型列表annotationTypes */
  @RequestMapping(value = "/random-designate-task-annotation", method = RequestMethod.POST)
  public Response randomDesignateTaskAnnotation(
      @RequestBody RandomDesignateAnnotationRequest randomDesignateAnnotationRequest) {
    return new Response<>(
        randomDesignateAnnotationBiz.process(randomDesignateAnnotationRequest, 0, 0));
  }

  /** 标注预览(标注各种状态下的条数图形化展示) */
  @RequestMapping(value = "/get-annotation-summary", method = RequestMethod.GET)
  public Response getAnnotationSummary() {
    return new Response<>(getAnnotationSummaryBiz.process(null, 0, 0));
  }

  /** 根据标注类型，返回指定标注类型的未分配的总条数 */
  @RequestMapping(value = "/count-undistributed-annotation", method = RequestMethod.GET)
  public Response countUnDistributedAnnotation(CountAnnotationRequest countAnnotationRequest) {
    return new Response<>(countAnnotationBiz.process(countAnnotationRequest, 0, 0));
  }

  /** 查询分词标注类型列表 */
  @RequestMapping(value = "/list-type", method = RequestMethod.GET)
  public Response listType() {
    return new Response<>(listAnTypeBiz.process(null, 0, 0));
  }

  /** 根据被指派人userId,查询其标注的各种状态 */
  @RequestMapping(value = "/get-annotation-summary-by-assignee", method = RequestMethod.GET)
  public Response getAnnotationSummaryByAssignee(
      SetUserStateRequest setUserStateRequest,
      @ModelAttribute(value = "userAccount", binding = false) UserAccount userAccount) {
    return new Response<>(
        getAnnotationSummaryByAssigneeBiz.process(
            setUserStateRequest, userAccount.getId(), userAccount.getRoleId()));
  }

  @RequestMapping(value = "/import", method = RequestMethod.POST)
  public Response<List<AnnotationCombine>> importAnnotations(
      @RequestParam("secretKey") final String secretKey,
      @RequestParam("annotationType") final int annotationType,
      @RequestParam("file") final MultipartFile file) {
    if (annotationType < 0 || annotationType >= AnnotationTypeEnum.values().length) {
      throw new InvalidInputException("invalid-annotation-type", annotationType + " is invalid");
    }

    if (!StringUtils.equals(secretKey, this.secretKey)) {
      throw new BusinessRuleException("permission-denied", "wrong secret key");
    }

    try {
      final String contents = IOUtils.toString(file.getInputStream());
      final Set<String> terms = new HashSet<>(Arrays.asList(contents.split("\n")));
      return new Response<>(
          annotationCombineRepository.saveAll(
              terms
                  .stream()
                  .filter(StringUtils::isNotBlank)
                  .map(
                      term -> {
                        final AnnotationCombine annotationCombine = new AnnotationCombine();
                        annotationCombine.setTerm(term);
                        annotationCombine.setAnnotationType(annotationType);
                        return annotationCombine;
                      })
                  .collect(Collectors.toList())));
    } catch (IOException e) {
      log.error("get wrong file: {}", file.getName());
      throw new InvalidInputException("wrong-file", e.getMessage());
    }
  }
}
