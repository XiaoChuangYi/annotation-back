package cn.malgo.annotation.dao;

import cn.malgo.annotation.dto.AnnotationSummary;
import cn.malgo.annotation.entity.AnnotationCombine;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface AnnotationCombineRepository
    extends JpaRepository<AnnotationCombine, Long>, JpaSpecificationExecutor<AnnotationCombine> {

  List<AnnotationCombine> findAllByBlockIdIn(Set<Long> blockIdList);

  List<AnnotationCombine> findAllByAnnotationTypeInAndStateEquals(
      List<Integer> annotationTypeList, String state, Pageable pageable);

  @Query(
      value = "select ac.state, count(ac.id) as num from annotation_combine ac group by ac.state",
      nativeQuery = true)
  List<AnnotationSummary> findByStateGroup();

  Integer countAllByAnnotationTypeInAndStateEquals(List<Integer> annotationTypes, String state);

  Integer countAllByStateIn(String state);

  AnnotationCombine findByTermEquals(String text);
}
