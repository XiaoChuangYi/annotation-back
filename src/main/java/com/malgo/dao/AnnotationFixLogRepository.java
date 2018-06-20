package com.malgo.dao;

import cn.malgo.core.definition.brat.BratPosition;
import com.malgo.dto.Annotation;
import com.malgo.entity.AnnotationFixLog;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.persistence.criteria.Predicate;
import java.util.List;

public interface AnnotationFixLogRepository
    extends JpaRepository<AnnotationFixLog, Integer>, JpaSpecificationExecutor<AnnotationFixLog> {
  AnnotationFixLog findByAnnotationIdAndStartAndEnd(int annotationId, int start, int end);

  default List<AnnotationFixLog> findAllFixedLogs(List<Pair<Annotation, BratPosition>> fixedLogs) {
    return findAll(
        (Specification<AnnotationFixLog>)
            (root, query, criteriaBuilder) ->
                criteriaBuilder.or(
                    fixedLogs
                        .stream()
                        .map(
                            fixedLog ->
                                criteriaBuilder.and(
                                    criteriaBuilder.equal(
                                        root.get("annotationId"), fixedLog.getLeft().getId()),
                                    criteriaBuilder.equal(
                                        root.get("start"), fixedLog.getRight().getStart()),
                                    criteriaBuilder.equal(
                                        root.get("end"), fixedLog.getRight().getEnd())))
                        .toArray(Predicate[]::new)));
  }
}
