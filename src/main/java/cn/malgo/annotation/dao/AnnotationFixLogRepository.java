package cn.malgo.annotation.dao;

import cn.malgo.annotation.dto.error.AnnotationWithPosition;
import cn.malgo.annotation.entity.AnnotationFixLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface AnnotationFixLogRepository
    extends JpaRepository<AnnotationFixLog, Integer>, JpaSpecificationExecutor<AnnotationFixLog> {
  AnnotationFixLog findByAnnotationIdAndStartAndEnd(int annotationId, int start, int end);

  @Query(
      value = "select * from annotation_fix_log where unique_combined_id in ?1",
      nativeQuery = true)
  List<AnnotationFixLog> findAllFixedLogsByUniqueIdIn(Collection<String> ids);

  default <T extends AnnotationWithPosition> List<AnnotationFixLog> findAllFixedLogs(
      List<T> fixedLogs) {
    return findAllFixedLogs(fixedLogs.stream());
  }

  default <T extends AnnotationWithPosition> List<AnnotationFixLog> findAllFixedLogs(
      Stream<T> fixedLogs) {
    return findAllFixedLogsByUniqueIdIn(
        fixedLogs
            .map(
                fixedLog ->
                    fixedLog.getAnnotation().getId()
                        + "-"
                        + fixedLog.getPosition().getStart()
                        + "-"
                        + fixedLog.getPosition().getEnd())
            .collect(Collectors.toSet()));
  }
}
