package cn.malgo.annotation.service;

import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import java.util.Collection;
import java.util.List;

public interface AddBlocksToTaskService {
  AnnotationTask addBlocksToTask(AnnotationTask annotationTask, List<Long> blockIds);

  AnnotationTask addBlocksToTask(
      AnnotationTask annotationTask, Collection<AnnotationTaskBlock> blocks);

  AnnotationTask addBlocksToTaskFast(
          AnnotationTask annotationTask, Collection<AnnotationTaskBlock> blocks);
}
