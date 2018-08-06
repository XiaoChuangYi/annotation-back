package cn.malgo.annotation.dao;

import cn.malgo.annotation.entity.AnnotationNew;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AnnotationRepository
    extends JpaRepository<AnnotationNew, Long>, JpaSpecificationExecutor<AnnotationNew> {

  List<AnnotationNew> findAllByAnnotationTypeInAndStateEquals(
      List<Integer> annotationTypeList, String state, Pageable pageable);
}
