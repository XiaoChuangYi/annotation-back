package cn.malgo.annotation.dao;

import cn.malgo.annotation.entity.AtomicTerm;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AtomicTermRepository
    extends JpaRepository<AtomicTerm, Long>, JpaSpecificationExecutor {
  List<AtomicTerm> findAllByAnnotationType(final AnnotationTypeEnum annotationType);
}
