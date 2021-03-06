package cn.malgo.annotation.biz.brat.task;

import cn.malgo.annotation.biz.brat.task.entities.BaseAnnotationBiz;
import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.dto.AutoAnnotation;
import cn.malgo.annotation.dto.DrugAutoAnnotationRequest;
import cn.malgo.annotation.dto.UpdateAnnotationAlgorithmRequest;
import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.enums.AnnotationStateEnum;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.request.brat.GetAutoAnnotationRequest;
import cn.malgo.annotation.service.AlgorithmApiService;
import cn.malgo.annotation.service.AtomicTermSegmentService;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.annotation.utils.AnnotationDocumentManipulator;
import cn.malgo.annotation.utils.entity.AnnotationDocument;
import cn.malgo.annotation.vo.AlgorithmAnnotationVO;
import cn.malgo.core.definition.Entity;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.DependencyServiceException;
import cn.malgo.service.exception.InternalServerException;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GetAutoAnnotationBiz extends BaseBiz<GetAutoAnnotationRequest, AlgorithmAnnotationVO> {

  private final AlgorithmApiService algorithmApiService;
  private final AtomicTermSegmentService atomicTermSegmentService;
  private final AnnotationRepository annotationRepository;

  @Autowired
  public GetAutoAnnotationBiz(
      final AlgorithmApiService algorithmApiService,
      final AtomicTermSegmentService atomicTermSegmentService,
      final AnnotationRepository annotationRepository) {
    this.algorithmApiService = algorithmApiService;
    this.atomicTermSegmentService = atomicTermSegmentService;
    this.annotationRepository = annotationRepository;
  }

  @Override
  protected void validateRequest(GetAutoAnnotationRequest request) throws InvalidInputException {
    if (request.getId() <= 0) {
      throw new InvalidInputException("invalid-id", "???????????????id");
    }
  }

  private UpdateAnnotationAlgorithmRequest getUpdateAnnotationAlgorithm(
      AnnotationNew annotationNew) {
    UpdateAnnotationAlgorithmRequest updateAnnotationAlgorithmRequest =
        new UpdateAnnotationAlgorithmRequest();
    updateAnnotationAlgorithmRequest.setId(annotationNew.getId());
    updateAnnotationAlgorithmRequest.setText(annotationNew.getTerm());
    updateAnnotationAlgorithmRequest.setNewTerms(new ArrayList<>());
    updateAnnotationAlgorithmRequest.setManualAnnotation(annotationNew.getManualAnnotation());
    return updateAnnotationAlgorithmRequest;
  }

  private DrugAutoAnnotationRequest getDrugAutoAnnotationAlgorithm(AnnotationNew annotationNew) {
    final DrugAutoAnnotationRequest req = new DrugAutoAnnotationRequest();
    req.setId(annotationNew.getId());
    req.setText(annotationNew.getTerm());
    return req;
  }

  private AlgorithmAnnotationVO getWordAnnotationVO(AnnotationNew annotation) {
    if (annotation.getState() == AnnotationStateEnum.PRE_ANNOTATION) {
      final UpdateAnnotationAlgorithmRequest updateAnnotationAlgorithmRequest =
          getUpdateAnnotationAlgorithm(annotation);
      final List<AutoAnnotation> finalAnnotationList =
          algorithmApiService.listRecombineAnnotationThroughAlgorithm(
              updateAnnotationAlgorithmRequest);
      String autoAnnotation = null;
      if (finalAnnotationList != null
          && finalAnnotationList.size() > 0
          && finalAnnotationList.get(0) != null) {
        autoAnnotation = finalAnnotationList.get(0).getAnnotation();
        annotation.setFinalAnnotation(AnnotationConvert.addUncomfirmed(autoAnnotation));
        annotation.setManualAnnotation("");
        annotationRepository.save(annotation);
      } else {
        log.warn("?????????????????????????????????????????????: {}, {}", annotation.getId(), autoAnnotation);
        throw new DependencyServiceException("????????????????????????????????????????????????????????????null");
      }
    }
    return new AlgorithmAnnotationVO("", AnnotationConvert.convert2AnnotationBratVO(annotation));
  }

  private AlgorithmAnnotationVO getSentenceAnnotationVO(AnnotationNew annotation) {
    // TODO ??????????????????????????????
    return new AlgorithmAnnotationVO(
        annotation.getFinalAnnotation(), AnnotationConvert.convert2AnnotationBratVO(annotation));
  }

  private AlgorithmAnnotationVO getRelationAnnotationVO(AnnotationNew annotation) {
    // TODO ????????????????????????
    return new AlgorithmAnnotationVO(
        annotation.getFinalAnnotation(), AnnotationConvert.convert2AnnotationBratVO(annotation));
  }

  private AlgorithmAnnotationVO getDiseaseAnnotationVO(AnnotationNew annotation) {
    if (StringUtils.isBlank(annotation.getFinalAnnotation())) {
      final List<Entity> entities =
          atomicTermSegmentService.seg(annotation.getAnnotationType(), annotation.getTerm());
      if (entities.size() != 0) {
        if (annotation.getState() == AnnotationStateEnum.PRE_ANNOTATION) {
          annotation.setState(AnnotationStateEnum.ANNOTATION_PROCESSING);
        }
        annotation.setFinalAnnotation(
            AnnotationDocumentManipulator.toBratAnnotations(
                new AnnotationDocument(annotation.getTerm(), new ArrayList<>(), entities)));
        annotationRepository.save(annotation);
      }
    }

    return new AlgorithmAnnotationVO(
        annotation.getFinalAnnotation(), AnnotationConvert.convert2AnnotationBratVO(annotation));
  }

  private AlgorithmAnnotationVO getDrugAnnotationVO(AnnotationNew annotation) {
    if (annotation.getState() == AnnotationStateEnum.PRE_ANNOTATION) {
      final DrugAutoAnnotationRequest req = getDrugAutoAnnotationAlgorithm(annotation);
      final List<AutoAnnotation> autoAnnotationList =
          algorithmApiService.listDrugAnnotationByAlgorithm(req);
      String autoAnnotation = null;
      if (autoAnnotationList != null
          && autoAnnotationList.size() > 0
          && autoAnnotationList.get(0) != null) {
        autoAnnotation = autoAnnotationList.get(0).getAnnotation();
        annotation.setFinalAnnotation(AnnotationConvert.addUncomfirmed(autoAnnotation));
        annotation.setManualAnnotation("");
        annotationRepository.save(annotation);
      } else {
        log.warn("???????????????????????????????????????: {}, {}", annotation.getId(), autoAnnotation);
        throw new DependencyServiceException("????????????????????????????????????????????????????????????null");
      }
    }
    return new AlgorithmAnnotationVO(
        annotation.getFinalAnnotation(), AnnotationConvert.convert2AnnotationBratVO(annotation));
  }

  private AlgorithmAnnotationVO getMedicalBooksAnnotationVO(AnnotationNew annotation) {
    return new AlgorithmAnnotationVO(
        annotation.getFinalAnnotation(), AnnotationConvert.convert2AnnotationBratVO(annotation));
  }

  @Override
  protected AlgorithmAnnotationVO doBiz(
      final GetAutoAnnotationRequest request, final UserDetails user) {
    final Optional<AnnotationNew> optional = annotationRepository.findById(request.getId());

    if (optional.isPresent()) {
      final AnnotationNew annotation = optional.get();
      BaseAnnotationBiz.checkPermission(annotation, user);
      switch (annotation.getState()) {
        case PRE_ANNOTATION:
        case ANNOTATION_PROCESSING:
          switch (AnnotationTypeEnum.getByValue(annotation.getAnnotationType().ordinal())) {
            case wordPos:
              // ??????
              return getWordAnnotationVO(annotation);

            case sentence:
              return getSentenceAnnotationVO(annotation);

            case relation:
              return getRelationAnnotationVO(annotation);

            case disease:
              return getDiseaseAnnotationVO(annotation);
            case drug:
              return getDrugAnnotationVO(annotation);
            case medicine_books:
              return getMedicalBooksAnnotationVO(annotation);
          }

        default:
          throw new InternalServerException("????????????");
      }
    }

    throw new InvalidInputException("invalid-id", request.getId() + "?????????");
  }
}
