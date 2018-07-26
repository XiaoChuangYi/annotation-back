package cn.malgo.annotation.biz.task;

import static org.testng.Assert.assertEquals;

import cn.malgo.annotation.AnnotationCombineApplication;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.request.task.TerminateTaskRequest;
import cn.malgo.annotation.service.impl.UserAccountServiceImpl.DefaultUserDetails;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TestTransaction;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@SpringBootTest(classes = AnnotationCombineApplication.class)
public class TerminateTaskBizTest extends AbstractTransactionalTestNGSpringContextTests {
  @Autowired private AnnotationTaskRepository annotationTaskRepository;
  @Autowired private AnnotationTaskBlockRepository annotationTaskBlockRepository;
  @Autowired private TerminateTaskBiz terminateTaskBiz;

  private long taskId;

  @BeforeClass
  public void init() {
    final AnnotationTaskBlock taskBlock1 =
        new AnnotationTaskBlock("test1", "", AnnotationTypeEnum.wordPos);
    taskBlock1.setState(AnnotationTaskState.DOING);

    final AnnotationTaskBlock taskBlock2 =
        new AnnotationTaskBlock("test2", "", AnnotationTypeEnum.wordPos);
    taskBlock2.setState(AnnotationTaskState.ANNOTATED);

    final List<AnnotationTaskBlock> blocks =
        annotationTaskBlockRepository.saveAll(Arrays.asList(taskBlock1, taskBlock2));

    final AnnotationTask task = annotationTaskRepository.save(new AnnotationTask("test-task"));
    task.addBlock(blocks.get(0));
    task.addBlock(blocks.get(1));
    task.setState(AnnotationTaskState.DOING);
    taskId = annotationTaskRepository.save(task).getId();
  }

  @Test
  public void testTerminateTask() {
    assertEquals(countRowsInTable("task_block"), 2);

    terminateTaskBiz.process(new TerminateTaskRequest(taskId), DefaultUserDetails.ADMIN);
    TestTransaction.flagForCommit();
    TestTransaction.end();

    assertEquals(countRowsInTable("annotation_task_block"), 2);
    assertEquals(countRowsInTable("annotation_task"), 1);
    assertEquals(countRowsInTable("task_block"), 1);

    TestTransaction.start();

    final AnnotationTask task = annotationTaskRepository.findAll().get(0);
    assertEquals(task.getState(), AnnotationTaskState.FINISHED);
    assertEquals(task.getTaskBlocks().size(), 1);
    final AnnotationTaskBlock block = task.getTaskBlocks().get(0).getBlock();
    assertEquals(block.getState(), AnnotationTaskState.FINISHED);
    assertEquals(annotationTaskBlockRepository.findAll().size(), 2);
    assertEquals(
        annotationTaskBlockRepository.getOne(block.getId()).getState(),
        AnnotationTaskState.FINISHED);
    assertEquals(
        annotationTaskBlockRepository
            .findAllByStateIn(Collections.singletonList(AnnotationTaskState.DOING))
            .size(),
        1);
    assertEquals(
        annotationTaskBlockRepository
            .findAllByStateIn(Collections.singletonList(AnnotationTaskState.FINISHED))
            .size(),
        1);
    assertEquals(
        annotationTaskBlockRepository
            .findAllByStateIn(Collections.singletonList(AnnotationTaskState.ANNOTATED))
            .size(),
        0);
    assertEquals(
        annotationTaskBlockRepository
            .findAllByStateIn(Collections.singletonList(AnnotationTaskState.CREATED))
            .size(),
        0);
  }
}
