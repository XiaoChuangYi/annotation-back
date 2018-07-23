package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AtomicTermRepository;
import cn.malgo.annotation.dto.NewTerm;
import cn.malgo.annotation.dto.UpdateAnnotationAlgorithmRequest;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.entity.AtomicTerm;
import cn.malgo.annotation.service.ExtractAddAtomicTermService;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.core.definition.Entity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExtractAddAtomicTermServiceImpl implements ExtractAddAtomicTermService {

  private final AtomicTermRepository atomicTermRepository;

  public ExtractAddAtomicTermServiceImpl(AtomicTermRepository atomicTermRepository) {
    this.atomicTermRepository = atomicTermRepository;
  }

  @Override
  public UpdateAnnotationAlgorithmRequest extractAndAddAtomicTerm(
      AnnotationCombine annotationCombine) {
    String manualAnnotation = annotationCombine.getManualAnnotation();
    List<Entity> entities = AnnotationConvert.getEntitiesFromAnnotation(manualAnnotation);
    List<AtomicTerm> atomicTermList = atomicTermRepository.findAll();
    entities.removeIf(
        current ->
            current.getType().endsWith("-unconfirmed")
                || atomicTermList
                    .stream()
                    .anyMatch(
                        atomicTerm ->
                            current.getType().equals(atomicTerm.getAnnotationType())
                                && current.getTerm().equals(atomicTerm.getTerm())));
    UpdateAnnotationAlgorithmRequest updateAnnotationAlgorithmRequest =
        new UpdateAnnotationAlgorithmRequest();
    if (entities.size() > 0) {
      List<NewTerm> newTermList =
          entities
              .stream()
              .map(entity -> new NewTerm(entity.getTerm(), entity.getType()))
              .collect(Collectors.toList());
      updateAnnotationAlgorithmRequest.setNewTerms(newTermList);
      List<AtomicTerm> atomicTerms =
          entities
              .stream()
              .map(
                  entity ->
                      new AtomicTerm(entity.getTerm(), entity.getType(), annotationCombine.getId()))
              .collect(Collectors.toList());
      if (atomicTerms.stream().distinct().collect(Collectors.toList()).size() > 0) {
        atomicTermRepository.saveAll(atomicTerms.stream().distinct().collect(Collectors.toList()));
      }
    } else {
      updateAnnotationAlgorithmRequest.setNewTerms(new ArrayList<>());
    }
    updateAnnotationAlgorithmRequest.setId(annotationCombine.getId());
    updateAnnotationAlgorithmRequest.setText(annotationCombine.getTerm());
    updateAnnotationAlgorithmRequest.setManualAnnotation(manualAnnotation);

    return updateAnnotationAlgorithmRequest;
  }

  @Override
  public void batchExtractAndAddAtomicTerm(List<AnnotationCombine> combineList) {
    final List<AtomicTerm> atomicTermList = atomicTermRepository.findAll();
    final List<AtomicTerm> finalAtomicTerms =
        combineList
            .stream()
            .flatMap(
                annotationCombine -> {
                  List<Entity> entities =
                      AnnotationConvert.getEntitiesFromAnnotation(
                          annotationCombine.getManualAnnotation());
                  entities.removeIf(
                      current ->
                          current.getType().endsWith("-unconfirmed")
                              || atomicTermList
                                  .stream()
                                  .anyMatch(
                                      atomicTerm ->
                                          current.getType().equals(atomicTerm.getAnnotationType())
                                              && current.getTerm().equals(atomicTerm.getTerm())));
                  return entities
                      .stream()
                      .map(
                          entity ->
                              new AtomicTerm(
                                  entity.getTerm(), entity.getType(), annotationCombine.getId()))
                      .collect(Collectors.toList())
                      .stream();
                })
            .collect(Collectors.toList());
    atomicTermRepository.saveAll(finalAtomicTerms.stream().distinct().collect(Collectors.toList()));
  }
}
