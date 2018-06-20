package com.malgo.service.impl;

import cn.malgo.core.definition.Entity;
import com.malgo.dao.AtomicTermRepository;
import com.malgo.dto.NewTerm;
import com.malgo.dto.UpdateAnnotationAlgorithm;
import com.malgo.entity.AnnotationCombine;
import com.malgo.entity.AtomicTerm;
import com.malgo.service.ExtractAddAtomicTermService;
import com.malgo.utils.AnnotationConvert;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.stereotype.Service;

/** Created by cjl on 2018/6/13. */
@Service
public class ExtractAddAtomicTermServiceImpl implements ExtractAddAtomicTermService {

  private final AtomicTermRepository atomicTermRepository;

  public ExtractAddAtomicTermServiceImpl(AtomicTermRepository atomicTermRepository) {
    this.atomicTermRepository = atomicTermRepository;
  }

  @Override
  public UpdateAnnotationAlgorithm extractAndAddAtomicTerm(AnnotationCombine annotationCombine) {
    String manualAnnotation = annotationCombine.getManualAnnotation();
    List<Entity> entities = AnnotationConvert.getEntitiesFromAnnotation(manualAnnotation);
    List<AtomicTerm> atomicTermList = atomicTermRepository.findAll();
    Iterator<Entity> iterator = entities.iterator();
    while (iterator.hasNext()) {
      Entity current = iterator.next();
      if (atomicTermList
                  .stream()
                  .filter(
                      atomicTerm ->
                          current.getType().equals(atomicTerm.getAnnotationType())
                              && current.getTerm().equals(atomicTerm.getTerm()))
                  .count()
              > 0
          || current.getType().endsWith("-unconfirmed")) {
        iterator.remove();
      }
    }
    // todo,当然还有其它的过滤规则
    UpdateAnnotationAlgorithm updateAnnotationAlgorithm = new UpdateAnnotationAlgorithm();
    if (entities.size() > 0) {
      List<NewTerm> newTermList =
          IntStream.range(0, entities.size())
              .mapToObj(
                  (int i) -> new NewTerm(entities.get(i).getTerm(), entities.get(i).getType()))
              .collect(Collectors.toList());
      updateAnnotationAlgorithm.setNewTerms(newTermList);
      List<AtomicTerm> atomicTerms =
          IntStream.range(0, entities.size())
              .mapToObj(
                  (int i) ->
                      new AtomicTerm(
                          entities.get(i).getTerm(),
                          entities.get(i).getType(),
                          annotationCombine.getId()))
              .collect(Collectors.toList());
      atomicTermRepository.saveAll(atomicTerms);
    } else {
      updateAnnotationAlgorithm.setNewTerms(Arrays.asList());
    }
    updateAnnotationAlgorithm.setId(annotationCombine.getId());
    updateAnnotationAlgorithm.setText(annotationCombine.getTerm());
    updateAnnotationAlgorithm.setManualAnnotation(manualAnnotation);
    return updateAnnotationAlgorithm;
  }
}
