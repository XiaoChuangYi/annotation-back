package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.entity.AnnotationTaskDoc;
import cn.malgo.annotation.entity.OriginalDoc;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TaskDocServiceImplTest {
  private static final String SAMPLE_TEXT = "这是第一句话，这是第二句话，这是第三句话而且足够长足够长足够长足够长足够长";

  private AnnotationTaskBlockRepository mockTaskBlockRepository;
  private TaskDocServiceImpl taskDocService;
  private AnnotationTask task;
  private OriginalDoc doc;

  @BeforeMethod
  public void init() {
    mockTaskBlockRepository = Mockito.mock(AnnotationTaskBlockRepository.class);
    Mockito.when(mockTaskBlockRepository.save(Mockito.any())).thenAnswer(i -> i.getArguments()[0]);
    taskDocService = new TaskDocServiceImpl(mockTaskBlockRepository);
    task = new AnnotationTask("test-task");
    doc = new OriginalDoc("test-doc", SAMPLE_TEXT, "", "");
  }

  @Test
  public void testAddRelationTask() {
    final AnnotationTaskDoc taskDoc =
        taskDocService.addDocToTask(task, doc, AnnotationTypeEnum.relation);
    Assert.assertEquals(task.getTaskDocs().size(), 1);
    Assert.assertEquals(task.getTaskDocs().get(0), taskDoc);
    Assert.assertEquals(taskDoc.getBlocks().size(), 1);
    Assert.assertEquals(taskDoc.getBlocks().get(0).getBlock().getText(), SAMPLE_TEXT);
    Mockito.verify(mockTaskBlockRepository).save(Mockito.any());
  }

  @Test
  public void testAddWordTask() {
    final AnnotationTaskDoc taskDoc =
        taskDocService.addDocToTask(task, doc, AnnotationTypeEnum.wordPos);
    Assert.assertEquals(task.getTaskDocs().size(), 1);
    Assert.assertEquals(task.getTaskDocs().get(0), taskDoc);
    Assert.assertEquals(taskDoc.getBlocks().size(), 2);
    Assert.assertEquals(taskDoc.getBlocks().get(0).getBlock().getText(), "这是第一句话，这是第二句话");
    Assert.assertEquals(taskDoc.getBlocks().get(1).getBlock().getText(), "这是第三句话而且足够长足够长足够长足够长足够长");
    Mockito.verify(mockTaskBlockRepository, Mockito.times(2)).save(Mockito.any());
  }
}
