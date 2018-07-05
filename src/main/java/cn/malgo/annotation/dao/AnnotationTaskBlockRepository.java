package cn.malgo.annotation.dao;

import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AnnotationTaskBlockRepository
    extends JpaRepository<AnnotationTaskBlock, Integer>, JpaSpecificationExecutor {
  AnnotationTaskBlock getOneByAnnotationTypeEqualsAndTextEquals(
      AnnotationTypeEnum annotationType, String text);

  default Pair<AnnotationTaskBlock, Boolean> getOrCreateBlock(
      final AnnotationTypeEnum annotationType, final String text) {
    final AnnotationTaskBlock block =
        getOneByAnnotationTypeEqualsAndTextEquals(annotationType, text);
    return block != null
        ? Pair.of(block, false)
        : Pair.of(save(new AnnotationTaskBlock(text, "", annotationType)), true);
  }
}
