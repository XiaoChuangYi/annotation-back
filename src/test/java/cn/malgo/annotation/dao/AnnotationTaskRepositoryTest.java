package cn.malgo.annotation.dao;

import static org.testng.Assert.assertEquals;

import cn.malgo.annotation.AnnotationCombineApplication;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TestTransaction;
import org.testng.annotations.Test;

@SpringBootTest(classes = AnnotationCombineApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AnnotationTaskRepositoryTest extends AbstractTransactionalTestNGSpringContextTests {
  @Autowired private AnnotationTaskRepository taskRepository;
  @Autowired private AnnotationTaskBlockRepository taskBlockRepository;

  @Test
  public void testCreateTask() {
    final AnnotationTask task = taskRepository.save(new AnnotationTask("test-task"));
    task.addBlock(
        taskBlockRepository.save(
            new AnnotationTaskBlock("test-text", "", AnnotationTypeEnum.wordPos)));
    taskRepository.save(task);

    TestTransaction.flagForCommit();
    TestTransaction.end();

    TestTransaction.start();

    assertEquals(countRowsInTable("task_block"), 1);
    assertEquals(taskRepository.count(), 1);
    assertEquals(taskBlockRepository.count(), 1);
    assertEquals(
        taskBlockRepository
            .findByAnnotationTypeAndStateInAndTaskBlocks_Task_IdEquals(
                AnnotationTypeEnum.wordPos,
                Collections.singletonList(AnnotationTaskState.CREATED),
                task.getId())
            .size(),
        1);
    assertEquals(taskRepository.getOne(task.getId()).getTaskBlocks().size(), 1);
    assertEquals(
        taskRepository.getOne(task.getId()).getTaskBlocks().iterator().next().getBlock().getText(),
        "test-text");
    assertEquals(
        taskRepository
            .getOne(task.getId())
            .getTaskBlocks()
            .iterator()
            .next()
            .getBlock()
            .getAnnotation(),
        "");
  }
}
