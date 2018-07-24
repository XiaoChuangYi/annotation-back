package cn.malgo.annotation.dao;

import cn.malgo.annotation.AnnotationCombineApplication;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.entity.AnnotationTaskDoc;
import cn.malgo.annotation.entity.OriginalDoc;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TestTransaction;
import org.testng.annotations.Test;

import java.util.Collections;

import static org.testng.Assert.assertEquals;

@SpringBootTest(classes = AnnotationCombineApplication.class)
public class AnnotationTaskRepositoryTest extends AbstractTransactionalTestNGSpringContextTests {
  @Autowired private AnnotationTaskRepository taskRepository;
  @Autowired private AnnotationTaskDocRepository taskDocRepository;
  @Autowired private OriginalDocRepository docRepository;
  @Autowired private AnnotationTaskBlockRepository taskBlockRepository;

  @Test
  public void testEntityGraph() {
    taskBlockRepository.findByTaskDocs_TaskDoc_Task_IdEquals(3L);
  }

  @Test
  @Ignore
  public void testCreateTask() {
    final OriginalDoc doc =
        docRepository.save(new OriginalDoc("test-name", "test-text", "UNKNOWN", "UNKNOWN"));
    final AnnotationTask task = taskRepository.save(new AnnotationTask("test-task"));
    final AnnotationTaskDoc taskDoc = task.addDoc(doc, AnnotationTypeEnum.wordPos);
    taskDoc.addBlock(
        taskBlockRepository.save(
            new AnnotationTaskBlock("test-text", "", AnnotationTypeEnum.wordPos)),
        0);
    taskDocRepository.save(taskDoc);

    TestTransaction.flagForCommit();
    TestTransaction.end();

    TestTransaction.start();

    assertEquals(docRepository.count(), 1);
    assertEquals(taskRepository.count(), 1);
    assertEquals(taskDocRepository.count(), 1);
    assertEquals(taskBlockRepository.count(), 1);
    assertEquals(
        taskBlockRepository
            .findByAnnotationTypeAndStateInAndTaskDocs_TaskDoc_Task_Id(
                AnnotationTypeEnum.wordPos,
                Collections.singletonList(AnnotationTaskState.CREATED),
                task.getId())
            .size(),
        1);
    assertEquals(taskRepository.getOne(task.getId()).getTaskDocs().size(), 1);
    assertEquals(taskRepository.getOne(task.getId()).getTaskDocs().get(0).getBlocks().size(), 1);
    assertEquals(
        taskRepository
            .getOne(task.getId())
            .getTaskDocs()
            .get(0)
            .getBlocks()
            .get(0)
            .getBlock()
            .getText(),
        "test-text");
    assertEquals(
        taskRepository
            .getOne(task.getId())
            .getTaskDocs()
            .get(0)
            .getBlocks()
            .get(0)
            .getBlock()
            .getAnnotation(),
        "");
    assertEquals(
        taskRepository
            .getOne(task.getId())
            .getTaskDocs()
            .get(0)
            .getBlocks()
            .get(0)
            .getTaskDoc()
            .getDoc()
            .getText(),
        "test-text");
  }
}
