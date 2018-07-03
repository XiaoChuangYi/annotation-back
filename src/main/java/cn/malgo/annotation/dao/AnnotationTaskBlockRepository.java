package cn.malgo.annotation.dao;

import cn.malgo.annotation.entity.AnnotationTaskBlock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnotationTaskBlockRepository
    extends JpaRepository<AnnotationTaskBlock, Integer> {}
