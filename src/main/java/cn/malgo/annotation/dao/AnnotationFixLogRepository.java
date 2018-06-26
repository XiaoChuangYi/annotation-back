package cn.malgo.annotation.dao;

import cn.malgo.core.definition.brat.BratPosition;
import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.entity.AnnotationFixLog;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public interface AnnotationFixLogRepository
    extends JpaRepository<AnnotationFixLog, Integer>, JpaSpecificationExecutor<AnnotationFixLog> {
  AnnotationFixLog findByAnnotationIdAndStartAndEnd(int annotationId, int start, int end);

  @Query(
    value = "select * from annotation_fix_log where unique_combined_id in ?1",
    nativeQuery = true
  )
  List<AnnotationFixLog> findAllFixedLogsByUniqueIdIn(Collection<String> ids);

  default List<AnnotationFixLog> findAllFixedLogs(List<Pair<Annotation, BratPosition>> fixedLogs) {
    return findAllFixedLogsByUniqueIdIn(
        fixedLogs
            .stream()
            .map(
                fixedLog ->
                    fixedLog.getLeft().getId()
                        + "-"
                        + fixedLog.getRight().getStart()
                        + "-"
                        + fixedLog.getRight().getEnd())
            .collect(Collectors.toSet()));
  }
}
