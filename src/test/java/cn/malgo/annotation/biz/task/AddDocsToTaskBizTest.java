package cn.malgo.annotation.biz.task;

import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.dao.OriginalDocRepository;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.entity.AnnotationTaskDoc;
import cn.malgo.annotation.entity.OriginalDoc;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.enums.OriginalDocState;
import cn.malgo.annotation.request.task.AddDocsToTaskRequest;
import cn.malgo.annotation.service.TaskDocService;
import cn.malgo.annotation.service.impl.TaskDocServiceImpl;
import cn.malgo.annotation.service.impl.UserAccountServiceImpl;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;

public class AddDocsToTaskBizTest {
  private AddDocsToTaskBiz addDocsToTaskBiz;
  private OriginalDocRepository docRepository;
  private AnnotationTaskRepository taskRepository;
  private TaskDocService taskDocService;

  private AnnotationTask annotationTask;
  private OriginalDoc originalDoc;

  @BeforeMethod
  public void init() {
    annotationTask = new AnnotationTask("test-task");
    originalDoc = new OriginalDoc("test-doc", "test-text", "", "");

    taskRepository = Mockito.mock(AnnotationTaskRepository.class);
    Mockito.when(taskRepository.getOne(Mockito.anyLong())).thenReturn(annotationTask);

    docRepository = Mockito.mock(OriginalDocRepository.class);
    Mockito.when(docRepository.findAllById(Mockito.any()))
        .thenReturn(Collections.singletonList(originalDoc));

    taskDocService = Mockito.mock(TaskDocService.class);
    addDocsToTaskBiz = new AddDocsToTaskBiz(taskRepository, docRepository, taskDocService);
  }

  @Test
  public void testAddDocStateFromCreatedToProcessing() {
    mockAddDocToTask(1);

    Assert.assertEquals(annotationTask.getState(), AnnotationTaskState.CREATED);
    Assert.assertEquals(originalDoc.getState(), OriginalDocState.IMPORTED);

    addDocsToTaskBiz.process(
        new AddDocsToTaskRequest(1, Collections.singleton(1L), 0),
        UserAccountServiceImpl.DefaultUserDetails.ADMIN);

    Assert.assertEquals(annotationTask.getState(), AnnotationTaskState.DOING);
    Assert.assertEquals(originalDoc.getState(), OriginalDocState.PROCESSING);
  }

  @Test
  public void testAddDocStateNoBlockCreated() {
    mockAddDocToTask(0);

    Assert.assertEquals(annotationTask.getState(), AnnotationTaskState.CREATED);
    Assert.assertEquals(originalDoc.getState(), OriginalDocState.IMPORTED);

    addDocsToTaskBiz.process(
        new AddDocsToTaskRequest(1, Collections.singleton(1L), 0),
        UserAccountServiceImpl.DefaultUserDetails.ADMIN);

    Assert.assertEquals(annotationTask.getState(), AnnotationTaskState.ANNOTATED);
    Assert.assertEquals(originalDoc.getState(), OriginalDocState.PROCESSING);
  }

  @Test
  public void testAddDocStateNoBlockCreatedButAlreadyDoing() {
    mockAddDocToTask(0);

    annotationTask.setState(AnnotationTaskState.DOING);
    addDocsToTaskBiz.process(
        new AddDocsToTaskRequest(1, Collections.singleton(1L), 0),
        UserAccountServiceImpl.DefaultUserDetails.ADMIN);

    Assert.assertEquals(annotationTask.getState(), AnnotationTaskState.DOING);
    Assert.assertEquals(originalDoc.getState(), OriginalDocState.PROCESSING);
  }

  private void mockAddDocToTask(final int i) {
    Mockito.when(
            taskDocService.addDocToTask(
                Mockito.any(AnnotationTask.class), Mockito.any(OriginalDoc.class), Mockito.any()))
        .thenAnswer(
            invocation -> {
              final Object[] arguments = invocation.getArguments();
              return new TaskDocServiceImpl.AddDocResult(
                  new AnnotationTaskDoc(
                      (AnnotationTask) arguments[0],
                      (OriginalDoc) arguments[1],
                      AnnotationTypeEnum.wordPos),
                  i);
            });
  }
}
