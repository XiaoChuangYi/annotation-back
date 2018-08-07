package cn.malgo.annotation.service;

import cn.malgo.annotation.entity.AnnotationTask;
import java.util.List;

public interface AddBlocksToTaskService {

  AnnotationTask addBlocksToTask(AnnotationTask annotationTask, List<Long> blockIds);
}
