package cn.malgo.annotation.integration.task;

import cn.malgo.annotation.dao.*;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.entity.AnnotationTaskDoc;
import cn.malgo.annotation.entity.OriginalDoc;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.enums.OriginalDocState;
import cn.malgo.annotation.service.TaskDocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TestTransaction;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TaskDocServiceImplIntegrationTest
    extends AbstractTransactionalTestNGSpringContextTests {
  @Autowired private TaskDocService taskDocService;
  @Autowired private AnnotationTaskRepository taskRepository;
  @Autowired private AnnotationTaskBlockRepository taskBlockRepository;
  @Autowired private AnnotationTaskDocRepository taskDocRepository;
  @Autowired private OriginalDocRepository docRepository;

  @BeforeMethod()
  public void setUp() {
    taskRepository.save(new AnnotationTask("test-task"));
    docRepository.save(new OriginalDoc("test-doc", "test", "", ""));
  }

  @Test
  public void testAddDocsToTask() {
    final AnnotationTask task = taskRepository.findAll().get(0);
    final OriginalDoc doc = docRepository.findAll().get(0);
    taskDocService.addDocToTask(task, doc, AnnotationTypeEnum.wordPos);

    TestTransaction.flagForCommit();
    TestTransaction.end();

    TestTransaction.start();
    Assert.assertEquals(countRowsInTable("annotation_task_block"), 1);
    Assert.assertEquals(countRowsInTable("annotation_task_doc"), 1);
    Assert.assertEquals(countRowsInTable("annotation_task_doc_block"), 1);
    Assert.assertEquals(taskDocRepository.findAll().get(0).getState(), AnnotationTaskState.DOING);
    TestTransaction.end();
  }
}
