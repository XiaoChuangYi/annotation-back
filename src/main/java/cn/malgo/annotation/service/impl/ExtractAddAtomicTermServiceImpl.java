package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AtomicTermRepository;
import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.entity.AtomicTerm;
import cn.malgo.annotation.service.AtomicTermSegmentService;
import cn.malgo.annotation.service.ExtractAddAtomicTermService;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.core.definition.Entity;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ExtractAddAtomicTermServiceImpl implements ExtractAddAtomicTermService {
  private final AtomicTermRepository atomicTermRepository;
  private final AtomicTermSegmentService atomicTermSegmentService;

  public ExtractAddAtomicTermServiceImpl(
      final AtomicTermRepository atomicTermRepository,
      final AtomicTermSegmentService atomicTermSegmentService) {
    this.atomicTermRepository = atomicTermRepository;
    this.atomicTermSegmentService = atomicTermSegmentService;
  }

  @Override
  public void extractAndAddAtomicTerm(Annotation annotation) {
    final List<Entity> entities =
        AnnotationConvert.getEntitiesFromAnnotation(annotation.getAnnotation());
    final List<AtomicTerm> atomicTermList =
        atomicTermRepository.findAllByAnnotationType(annotation.getAnnotationType());

    entities.removeIf(
        current ->
            current.getType().endsWith("-unconfirmed")
                || atomicTermList
                    .stream()
                    .anyMatch(
                        atomicTerm ->
                            current.getType().equals(atomicTerm.getAnType())
                                && current.getTerm().equals(atomicTerm.getTerm())));

    if (entities.size() > 0) {
      List<AtomicTerm> atomicTerms =
          entities
              .stream()
              .map(
                  entity ->
                      new AtomicTerm(
                          entity.getTerm(),
                          entity.getType(),
                          annotation.getId(),
                          annotation.getAnnotationType()))
              .collect(Collectors.toList());
      if (atomicTerms.size() > 0) {
        atomicTermRepository.saveAll(atomicTerms);
        atomicTermSegmentService.addAtomicTerms(annotation.getAnnotationType(), atomicTerms);
      }
    }
  }
}
