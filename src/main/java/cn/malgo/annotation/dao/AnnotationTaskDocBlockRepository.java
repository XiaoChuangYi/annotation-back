package cn.malgo.annotation.dao;

import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.entity.AnnotationTaskDocBlock;
import cn.malgo.annotation.entity.AnnotationTaskDocBlockId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnotationTaskDocBlockRepository
    extends JpaRepository<AnnotationTaskDocBlock, AnnotationTaskDocBlockId> {

  List<AnnotationTaskDocBlock> findByBlockEquals(AnnotationTaskBlock block);

  List<AnnotationTaskDocBlock> findAllByBlockIn(List<AnnotationTaskBlock> annotationTaskBlocks);
}
