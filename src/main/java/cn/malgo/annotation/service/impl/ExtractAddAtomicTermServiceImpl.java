package cn.malgo.annotation.service.impl;

import cn.malgo.core.definition.Entity;
import cn.malgo.annotation.dao.AtomicTermRepository;
import cn.malgo.annotation.dto.UpdateAnnotationAlgorithm;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.entity.AtomicTerm;
import cn.malgo.annotation.service.ExtractAddAtomicTermService;
import cn.malgo.annotation.utils.AnnotationConvert;
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
    if (entities.size() > 0) {
      List<AtomicTerm> atomicTerms =
          IntStream.range(0, entities.size())
              .mapToObj(
                  (int i) ->
                      new AtomicTerm(
                          entities.get(i).getTerm(),
                          entities.get(i).getType(),
                          annotationCombine.getId()))
              .collect(Collectors.toList());
      // todo ,test
      if (atomicTerms.stream().distinct().collect(Collectors.toList()).size() > 0) {
        atomicTermRepository.saveAll(atomicTerms.stream().distinct().collect(Collectors.toList()));
      }
    }
    return null;
  }
}
