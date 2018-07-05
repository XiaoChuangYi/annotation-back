package cn.malgo.annotation.dao;

import cn.malgo.annotation.entity.AnnotationTaskDoc;
import cn.malgo.annotation.entity.AnnotationTaskDocId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnotationTaskDocRepository
    extends JpaRepository<AnnotationTaskDoc, AnnotationTaskDocId> {}
