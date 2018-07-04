package cn.malgo.annotation.dao;

import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.persistence.EntityNotFoundException;

public interface AnnotationTaskBlockRepository extends JpaRepository<AnnotationTaskBlock, Integer>, JpaSpecificationExecutor {
  AnnotationTaskBlock findByAnnotationTypeEqualsAndTextEquals(
      AnnotationTypeEnum annotationType, String text);

  default Pair<AnnotationTaskBlock, Boolean> getOrCreateBlock(
      final AnnotationTypeEnum annotationType, final String text) {
    try {
      return Pair.of(findByAnnotationTypeEqualsAndTextEquals(annotationType, text), false);
    } catch (EntityNotFoundException ex) {
      return Pair.of(save(new AnnotationTaskBlock(text, "", annotationType)), true);
    }
  }
}
