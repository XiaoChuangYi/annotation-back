package cn.malgo.annotation.integration.task;

import cn.malgo.annotation.biz.brat.PreAnnotationRecycleBiz;
import cn.malgo.annotation.biz.brat.task.AnnotationCommitBiz;
import cn.malgo.annotation.biz.doc.CreateBlocksFromDocBiz;
import cn.malgo.annotation.biz.task.AddBlocksToTaskBiz;
import cn.malgo.annotation.biz.task.CreateTaskBiz;
import cn.malgo.annotation.biz.task.TerminateTaskBiz;
import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.dao.OriginalDocRepository;
import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.entity.OriginalDoc;
import cn.malgo.annotation.enums.AnnotationStateEnum;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.enums.OriginalDocState;
import cn.malgo.annotation.request.AnnotationRecycleRequest;
import cn.malgo.annotation.request.brat.CommitAnnotationRequest;
import cn.malgo.annotation.request.task.AddBlocksToTaskRequest;
import cn.malgo.annotation.request.task.CreateBlocksFromDocRequest;
import cn.malgo.annotation.request.task.CreateTaskRequest;
import cn.malgo.annotation.request.task.TerminateTaskRequest;
import cn.malgo.annotation.service.impl.UserAccountServiceImpl;
import cn.malgo.annotation.service.impl.UserAccountServiceImpl.DefaultUserDetails;
import cn.malgo.annotation.vo.AnnotationTaskVO;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TestTransaction;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/** 任务系统集成测试，任务创建过程，标注过程，各种状态检查 */
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class IntegrationNewTaskTest extends AbstractTransactionalTestNGSpringContextTests {

  private static final String SAMPLE_TEXT = "这是第一句话，这是第二句话，这是第三句话而且足够长足够长足够长足够长足够长";

  @Autowired private CreateTaskBiz createTaskBiz;
  @Autowired private CreateBlocksFromDocBiz createBlocksFromDocBiz;
  @Autowired private AddBlocksToTaskBiz addBlocksToTaskBiz;

  @Autowired private AnnotationCommitBiz annotationCommitBiz;

  @Autowired private PreAnnotationRecycleBiz preAnnotationRecycleBiz;

  @Autowired private TerminateTaskBiz terminateTaskBiz;

  @Autowired private AnnotationRepository annotationRepository;

  @Autowired private OriginalDocRepository originalDocRepository;

  @Autowired private AnnotationTaskBlockRepository annotationTaskBlockRepository;

  @Autowired private AnnotationTaskRepository annotationTaskRepository;

  /** 验证标准，状态和数量 创建任务和doc */
  private Pair<AnnotationTaskVO, OriginalDoc> createTaskAndDoc() {
    final AnnotationTaskVO task =
        createTaskBiz.process(
            new CreateTaskRequest("test-task-20180810"),
            UserAccountServiceImpl.DefaultUserDetails.ADMIN);
    assertNotNull(task);
    assertNotEquals(task.getId(), 0);
    assertEquals(task.getState(), AnnotationTaskState.CREATED.name());

    final OriginalDoc doc =
        originalDocRepository.save(new OriginalDoc("test-doc-20180810", SAMPLE_TEXT, "", ""));

    assertNotNull(doc);
    assertNotEquals(doc.getId(), 0);
    assertEquals(doc.getState(), OriginalDocState.IMPORTED);

    TestTransaction.flagForCommit();
    TestTransaction.end();

    return Pair.of(task, doc);
  }

  @Test
  public void testTaskProcessFlow() {
    final Pair<AnnotationTaskVO, OriginalDoc> taskAndDoc = createTaskAndDoc();

    TestTransaction.start();
    createBlocksFromDocBiz.process(
        new CreateBlocksFromDocRequest(
            Collections.singleton(taskAndDoc.getRight().getId()),
            AnnotationTypeEnum.relation.ordinal()),
        UserAccountServiceImpl.DefaultUserDetails.ADMIN);
    assertEquals(countRowsInTable("annotation_task_block"), 1);
    final List<AnnotationTaskBlock> taskBlocks = annotationTaskBlockRepository.findAll();
    assertEquals(taskBlocks.get(0).getState(), AnnotationTaskState.CREATED);
    TestTransaction.flagForCommit();
    TestTransaction.end();

    TestTransaction.start();
    addBlocksToTaskBiz.process(
        new AddBlocksToTaskRequest(
            taskAndDoc.getLeft().getId(),
            taskBlocks.stream().map(block -> block.getId()).collect(Collectors.toList())),
        DefaultUserDetails.ADMIN);
    assertEquals(
        annotationTaskRepository.getOne(taskAndDoc.getLeft().getId()).getState(),
        AnnotationTaskState.DOING);
    assertEquals(
        annotationTaskBlockRepository.findAll().get(0).getState(), AnnotationTaskState.DOING);
    assertEquals(
        annotationRepository
            .findByTaskIdEqualsAndStateIn(
                taskAndDoc.getLeft().getId(),
                Collections.singletonList(AnnotationStateEnum.UN_DISTRIBUTED))
            .size(),
        1);
    assertEquals(countRowsInTable("task_block"), 1);
    assertEquals(countRowsInTable("annotation_new"), 1);
    assertEquals(countRowsInTable("annotation_task_block"), 1);
    TestTransaction.flagForCommit();
    TestTransaction.end();

    commitAnnotation(annotationRepository.findByTermEquals(SAMPLE_TEXT));

    TestTransaction.start();
    assertEquals(
        annotationRepository
            .findByTaskIdEqualsAndStateIn(
                taskAndDoc.getLeft().getId(),
                Collections.singletonList(AnnotationStateEnum.ANNOTATION_PROCESSING))
            .size(),
        0);
    assertEquals(
        annotationRepository
            .findByTaskIdEqualsAndStateIn(
                taskAndDoc.getLeft().getId(),
                Collections.singletonList(AnnotationStateEnum.SUBMITTED))
            .size(),
        1);
    assertEquals(
        annotationTaskBlockRepository.findAll().get(0).getState(), AnnotationTaskState.ANNOTATED);
    assertEquals(
        annotationTaskRepository.getOne(taskAndDoc.getLeft().getId()).getState(),
        AnnotationTaskState.ANNOTATED);
    TestTransaction.flagForCommit();
    TestTransaction.end();

    TestTransaction.start();
    terminateTaskBiz.process(
        new TerminateTaskRequest(taskAndDoc.getLeft().getId()),
        UserAccountServiceImpl.DefaultUserDetails.ADMIN);

    assertEquals(
        annotationTaskBlockRepository.findAll().get(0).getState(), AnnotationTaskState.PRE_CLEAN);
    assertEquals(
        annotationTaskRepository.getOne(taskAndDoc.getLeft().getId()).getTaskBlocks().size(), 0);
    assertEquals(
        annotationTaskRepository.getOne(taskAndDoc.getLeft().getId()).getState(),
        AnnotationTaskState.FINISHED);
    assertEquals(
        annotationRepository
            .findByTaskIdEqualsAndStateIn(
                taskAndDoc.getLeft().getId(),
                Collections.singletonList(AnnotationStateEnum.SUBMITTED))
            .size(),
        0);
    assertEquals(
        annotationRepository
            .findByTaskIdEqualsAndStateIn(
                taskAndDoc.getLeft().getId(),
                Collections.singletonList(AnnotationStateEnum.PRE_CLEAN))
            .size(),
        1);
    assertEquals(countRowsInTable("task_block"), 0);
    TestTransaction.flagForCommit();
    TestTransaction.end();

    TestTransaction.start();
    // 语料返工
    addBlocksToTaskBiz.process(
        new AddBlocksToTaskRequest(
            taskAndDoc.getLeft().getId(),
            annotationTaskBlockRepository
                .findAll()
                .stream()
                .map(block -> block.getId())
                .collect(Collectors.toList())),
        DefaultUserDetails.ADMIN);
    assertNotEquals(
        annotationTaskBlockRepository.findAll().get(0).getState(), AnnotationTaskState.PRE_CLEAN);
    assertEquals(
        annotationTaskBlockRepository.findAll().get(0).getState(), AnnotationTaskState.DOING);
    assertEquals(
        annotationTaskRepository.getOne(taskAndDoc.getLeft().getId()).getTaskBlocks().size(), 1);
    assertEquals(
        annotationTaskRepository.getOne(taskAndDoc.getLeft().getId()).getState(),
        AnnotationTaskState.DOING);
    assertEquals(countRowsInTable("task_block"), 1);
    TestTransaction.flagForCommit();
    TestTransaction.end();
  }

  private void commitAnnotation(final AnnotationNew annotationNew) {
    TestTransaction.start();
    final String annotation =
        "T1 body-structure 0 " + annotationNew.getTerm().length() + " " + annotationNew.getTerm();
    annotationNew.setFinalAnnotation(annotation);
    annotationNew.setState(AnnotationStateEnum.ANNOTATION_PROCESSING);
    annotationRepository.save(annotationNew);
    TestTransaction.flagForCommit();
    TestTransaction.end();

    TestTransaction.start();
    preAnnotationRecycleBiz.process(
        new AnnotationRecycleRequest(
            Collections.singletonList(annotationRepository.findByTermEquals(SAMPLE_TEXT).getId())),
        DefaultUserDetails.ADMIN);
    assertEquals(
        annotationRepository
            .findByTaskIdEqualsAndStateIn(
                annotationNew.getId(),
                Collections.singletonList(AnnotationStateEnum.UN_DISTRIBUTED))
            .size(),
        1);
    TestTransaction.flagForCommit();
    TestTransaction.end();

    TestTransaction.start();
    annotationNew.setState(AnnotationStateEnum.PRE_ANNOTATION);
    annotationRepository.save(annotationNew);
    annotationCommitBiz.process(
        new CommitAnnotationRequest(annotationNew.getId()),
        UserAccountServiceImpl.DefaultUserDetails.ADMIN);
    TestTransaction.flagForCommit();
    TestTransaction.end();
  }
}
