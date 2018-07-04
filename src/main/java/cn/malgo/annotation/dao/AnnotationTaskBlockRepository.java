package cn.malgo.annotation.dao;

import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnotationTaskBlockRepository extends JpaRepository<AnnotationTaskBlock, Integer> {
  AnnotationTaskBlock findByAnnotationTypeEqualsAndTextEquals(
      AnnotationTypeEnum annotationType, String text);
}
