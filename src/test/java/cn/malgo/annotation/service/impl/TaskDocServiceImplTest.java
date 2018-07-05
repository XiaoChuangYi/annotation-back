package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AnnotationTaskDocRepository;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.entity.AnnotationTaskDoc;
import cn.malgo.annotation.entity.OriginalDoc;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.service.AnnotationBlockService;
import org.apache.commons.lang3.tuple.Pair;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TaskDocServiceImplTest {
  private static final String SAMPLE_TEXT = "这是第一句话，这是第二句话，这是第三句话而且足够长足够长足够长足够长足够长";

  private static final Answer<Pair<AnnotationTaskBlock, Boolean>> BLOCK_ANSWER =
      invocation -> {
        final Object[] args = invocation.getArguments();
        final AnnotationTaskBlock block =
            new AnnotationTaskBlock((String) args[1], "", (AnnotationTypeEnum) args[0]);
        block.setState(AnnotationTaskState.DOING);
        return Pair.of(block, true);
      };

  private AnnotationTaskDocRepository mockTaskDocRepository;
  //  private AnnotationTaskDocBlockRepository mockTaskDocBlockRepository;
  private AnnotationBlockService mockBlockService;
  private TaskDocServiceImpl taskDocService;
  private AnnotationTask task;
  private OriginalDoc doc;

  @BeforeMethod
  public void init() {
    mockTaskDocRepository = Mockito.mock(AnnotationTaskDocRepository.class);
    Mockito.when(mockTaskDocRepository.updateState(Mockito.any())).thenCallRealMethod();
    Mockito.when(mockTaskDocRepository.save(Mockito.any()))
        .thenAnswer(invocation -> invocation.getArguments()[0]);

    //    mockTaskDocBlockRepository = Mockito.mock(AnnotationTaskDocBlockRepository.class);
    //    Mockito.when(mockTaskDocRepository.save(Mockito.any()))
    //            .thenAnswer(invocation -> invocation.getArguments()[0]);

    mockBlockService = Mockito.mock(AnnotationBlockService.class);
    Mockito.when(mockBlockService.getOrCreateAnnotation(Mockito.any(), Mockito.anyString()))
        .thenAnswer(BLOCK_ANSWER);

    taskDocService = new TaskDocServiceImpl(mockTaskDocRepository, mockBlockService);
    task = new AnnotationTask("test-task");
    doc = new OriginalDoc("test-doc", SAMPLE_TEXT, "", "");
  }

  @Test
  public void testAddRelationTask() {
    final TaskDocServiceImpl.AddDocResult result =
        taskDocService.addDocToTask(task, doc, AnnotationTypeEnum.relation);

    // check blocks
    Assert.assertEquals(task.getTaskDocs().size(), 1);
    Assert.assertEquals(task.getTaskDocs().get(0), result.getTaskDoc());
    Assert.assertEquals(result.getTaskDoc().getBlocks().size(), 1);
    Assert.assertEquals(result.getTaskDoc().getBlocks().get(0).getBlock().getText(), SAMPLE_TEXT);

    // check states
    Assert.assertEquals(result.getTaskDoc().getState(), AnnotationTaskState.DOING);
    Assert.assertEquals(
        result.getTaskDoc().getBlocks().get(0).getBlock().getState(), AnnotationTaskState.DOING);

    Mockito.verify(mockBlockService).getOrCreateAnnotation(Mockito.any(), Mockito.anyString());
  }

  @Test
  public void testAddWordTask() {
    final TaskDocServiceImpl.AddDocResult result =
        taskDocService.addDocToTask(task, doc, AnnotationTypeEnum.wordPos);
    final AnnotationTaskDoc taskDoc = result.getTaskDoc();
    Assert.assertEquals(task.getTaskDocs().size(), 1);
    Assert.assertEquals(task.getTaskDocs().get(0), taskDoc);
    Assert.assertEquals(taskDoc.getBlocks().size(), 2);
    Assert.assertEquals(taskDoc.getBlocks().get(0).getBlock().getText(), "这是第一句话，这是第二句话");
    Assert.assertEquals(taskDoc.getBlocks().get(1).getBlock().getText(), "这是第三句话而且足够长足够长足够长足够长足够长");
    Mockito.verify(mockBlockService, Mockito.times(2))
        .getOrCreateAnnotation(Mockito.any(), Mockito.anyString());
  }
}
