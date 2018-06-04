package com.malgo.biz.brat.task.relations;

import com.alibaba.fastjson.JSON;
import com.malgo.biz.BaseBiz;
import com.malgo.dao.AnnotationCombineRepository;
import com.malgo.entity.AnnotationCombine;
import com.malgo.enums.AnnotationCombineStateEnum;
import com.malgo.exception.BusinessRuleException;
import com.malgo.exception.InvalidInputException;
import com.malgo.request.brat.AddRelationRequest;
import com.malgo.service.RelationOperateService;
import com.malgo.utils.AnnotationConvert;
import com.malgo.vo.AnnotationCombineBratVO;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Created by cjl on 2018/6/1.
 */
@Component
@Slf4j
public class AddRelationBiz extends BaseBiz<AddRelationRequest, AnnotationCombineBratVO> {

  private final AnnotationCombineRepository annotationCombineRepository;
  private final RelationOperateService finalRelationOperateService;
  private final RelationOperateService reviewRelationOperateService;
  private int globalRole;

  public AddRelationBiz(@Qualifier("final") RelationOperateService finalRelationOperateService,
      @Qualifier("review") RelationOperateService reviewRelationOperateService,
      AnnotationCombineRepository annotationCombineRepository) {
    this.finalRelationOperateService = finalRelationOperateService;
    this.reviewRelationOperateService = reviewRelationOperateService;
    this.annotationCombineRepository = annotationCombineRepository;
  }

  @Override
  protected void validateRequest(AddRelationRequest addRelationRequest)
      throws InvalidInputException {
    if (addRelationRequest == null) {
      throw new InvalidInputException("invalid-request", "无效的请求");
    }
    if (addRelationRequest.getId() <= 0) {
      throw new InvalidInputException("invalid-id", "无效的id");
    }
    if (StringUtils.isBlank(addRelationRequest.getSourceTag())) {
      throw new InvalidInputException("invalid-sourceTag", "参数sourceTag为空");
    }
    if (StringUtils.isBlank(addRelationRequest.getTargetTag())) {
      throw new InvalidInputException("invalid-targetTag", "参数targetTag为空");
    }
    if (StringUtils.isBlank(addRelationRequest.getRelation())) {
      throw new InvalidInputException("invalid-relation", "参数relation为空");
    }

  }

  @Override
  protected void authorize(int userId, int role, AddRelationRequest addRelationRequest)
      throws BusinessRuleException {
    globalRole = role;
    if (role > 2) {//标注人员，练习人员，需要判断是否有权限操作这一条
      Optional<AnnotationCombine> optional = annotationCombineRepository
          .findById(addRelationRequest.getId());
      if (optional.isPresent()) {
        if (optional.get().getAssignee() != userId) {
          throw new BusinessRuleException("no-authorize-current-record", "当前人员无权操作该条记录");
        }
      }
    }
  }

  @Override
  protected AnnotationCombineBratVO doBiz(AddRelationRequest addRelationRequest) {
    Optional<AnnotationCombine> optional = annotationCombineRepository
        .findById(addRelationRequest.getId());
    if (optional.isPresent()) {
      AnnotationCombine annotationCombine = optional.get();
      log.info("新增关系输入参数：{}", JSON.toJSONString(addRelationRequest));
      if (globalRole > 0 && globalRole < 3) {//管理员或者是审核人员级别
        if (annotationCombine.getAnnotationType() == 2) {
          String annotation = reviewRelationOperateService.addRelation(addRelationRequest);
          log.info("管理审核人员新增关系输出结果：{}", annotation);
          annotationCombine.setReviewedAnnotation(annotation);
          annotationCombine = annotationCombineRepository.save(annotationCombine);
          return AnnotationConvert.convert2AnnotationCombineBratVO(annotationCombine);
        }
      }
      if (globalRole >= 3) {//标注人员
        if (annotationCombine.getAnnotationType() == 2) {//当前标注类型为分句标注
          annotationCombine.setState(AnnotationCombineStateEnum.annotationProcessing.name());
          String annotation = finalRelationOperateService.addRelation(addRelationRequest);
          log.info("标注人员新增关系输出结果：{}", annotation);
          annotationCombine.setFinalAnnotation(annotation);
          annotationCombine = annotationCombineRepository.save(annotationCombine);
          return AnnotationConvert.convert2AnnotationCombineBratVO(annotationCombine);
        }
      }
    }
    return null;
  }
}
