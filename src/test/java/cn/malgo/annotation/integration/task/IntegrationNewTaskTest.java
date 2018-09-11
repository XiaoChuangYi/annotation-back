package cn.malgo.annotation.integration.task;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import cn.malgo.annotation.biz.CleanOutBlockBiz;
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
import cn.malgo.annotation.vo.AnnotationTaskVO;
import cn.malgo.service.entity.BaseEntity;
import cn.malgo.service.model.UserDetails;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TestTransaction;
import org.testng.annotations.Test;

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
  @Autowired private CleanOutBlockBiz cleanOutBlockBiz;
  @Autowired private AnnotationRepository annotationRepository;
  @Autowired private OriginalDocRepository originalDocRepository;
  @Autowired private AnnotationTaskBlockRepository annotationTaskBlockRepository;
  @Autowired private AnnotationTaskRepository annotationTaskRepository;

  @Test
  public void testTaskProcessFlow() {
    final Pair<AnnotationTaskVO, OriginalDoc> taskAndDoc = createTaskAndDoc();
    final List<AnnotationTaskBlock> taskBlocks = createBlocks(taskAndDoc.getRight());
    addBlocksToTask(taskBlocks, taskAndDoc.getLeft());

    final long taskId = taskAndDoc.getLeft().getId();
    assertStates(
        taskId,
        AnnotationTaskState.DOING,
        AnnotationStateEnum.UN_DISTRIBUTED,
        AnnotationTaskState.DOING);

    commitAnnotation(annotationRepository.findByTermEquals(SAMPLE_TEXT));

    TestTransaction.start();
    terminateTaskBiz.process(new TerminateTaskRequest(taskId), null);
    TestTransaction.flagForCommit();
    TestTransaction.end();

    assertStates(
        taskId,
        AnnotationTaskState.FINISHED,
        AnnotationStateEnum.PRE_CLEAN,
        AnnotationTaskState.PRE_CLEAN);

    TestTransaction.start();
    cleanOutBlockBiz.process(null, null);
    TestTransaction.flagForCommit();
    TestTransaction.end();

    assertStates(
        taskId,
        AnnotationTaskState.FINISHED,
        AnnotationStateEnum.CLEANED,
        AnnotationTaskState.FINISHED);
  }

  @Test
  public void testTerminateTaskProcess() {
    final Pair<AnnotationTaskVO, OriginalDoc> taskAndDoc = createTaskAndDoc();
    final List<AnnotationTaskBlock> taskBlocks = createBlocks(taskAndDoc.getRight());
    addBlocksToTask(taskBlocks, taskAndDoc.getLeft());

    final long taskId = taskAndDoc.getLeft().getId();
    assertStates(
        taskId,
        AnnotationTaskState.DOING,
        AnnotationStateEnum.UN_DISTRIBUTED,
        AnnotationTaskState.DOING);

    TestTransaction.start();
    terminateTaskBiz.process(new TerminateTaskRequest(taskId), null);
    TestTransaction.flagForCommit();
    TestTransaction.end();

    TestTransaction.start();
    assertEquals(countRowsInTable("annotation_new"), 1);
    assertEquals(countRowsInTable("annotation_task_block"), 1);
    assertEquals(countRowsInTable("annotation_task"), 1);
    assertEquals(countRowsInTable("original_doc"), 1);
    assertEquals(countRowsInTable("task_block"), 0);
    assertEquals(annotationTaskRepository.getOne(taskId).getState(), AnnotationTaskState.FINISHED);
    assertEquals(
        annotationRepository
            .findByTaskIdAndStateIn(
                taskId, Collections.singletonList(AnnotationStateEnum.UN_DISTRIBUTED))
            .size(),
        0);
    assertEquals(
        annotationTaskBlockRepository.findAll().get(0).getState(), AnnotationTaskState.CREATED);
    TestTransaction.flagForCommit();
    TestTransaction.end();
  }

  private void assertStates(
      final long taskId,
      final AnnotationTaskState taskState,
      final AnnotationStateEnum annotationState,
      final AnnotationTaskState blockState) {
    TestTransaction.start();
    assertEquals(countRowsInTable("annotation_new"), 1);
    assertEquals(countRowsInTable("annotation_task_block"), 1);
    assertEquals(countRowsInTable("annotation_task"), 1);
    assertEquals(countRowsInTable("original_doc"), 1);
    assertEquals(countRowsInTable("task_block"), 1);
    assertEquals(annotationTaskRepository.getOne(taskId).getState(), taskState);
    final List<AnnotationNew> annotations =
        annotationRepository.findByTaskIdAndStateIn(
            taskId, Collections.singletonList(annotationState));
    assertEquals(annotations.size(), 1);

    if (annotationState == AnnotationStateEnum.CLEANED) {
      assertNotNull(annotations.get(0).getPrecisionRate());
      assertNotNull(annotations.get(0).getRecallRate());
    } else {
      assertNull(annotations.get(0).getPrecisionRate());
      assertNull(annotations.get(0).getRecallRate());
    }

    assertEquals(annotationTaskBlockRepository.findAll().get(0).getState(), blockState);
    TestTransaction.flagForCommit();
    TestTransaction.end();
  }

  /** 验证标准，状态和数量 创建任务和doc */
  private Pair<AnnotationTaskVO, OriginalDoc> createTaskAndDoc() {
    final AnnotationTaskVO task =
        createTaskBiz.process(new CreateTaskRequest("test-task-20180810"), null);
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

  private void addBlocksToTask(
      final List<AnnotationTaskBlock> taskBlocks, final AnnotationTaskVO taskVO) {
    TestTransaction.start();
    addBlocksToTaskBiz.process(
        new AddBlocksToTaskRequest(
            taskVO.getId(),
            taskBlocks.stream().map(BaseEntity::getId).collect(Collectors.toList())),
        null);
    TestTransaction.flagForCommit();
    TestTransaction.end();
  }

  @NotNull
  private List<AnnotationTaskBlock> createBlocks(final OriginalDoc doc) {
    TestTransaction.start();
    createBlocksFromDocBiz.process(
        new CreateBlocksFromDocRequest(
            Collections.singleton(doc.getId()), AnnotationTypeEnum.relation.ordinal()),
        null);
    TestTransaction.flagForCommit();
    TestTransaction.end();

    assertEquals(countRowsInTable("annotation_task_block"), 1);
    final List<AnnotationTaskBlock> taskBlocks = annotationTaskBlockRepository.findAll();
    assertEquals(taskBlocks.get(0).getState(), AnnotationTaskState.CREATED);
    return taskBlocks;
  }

  private void commitAnnotation(AnnotationNew annotationNew) {
    // 标注中
    TestTransaction.start();
    final String annotation =
        "T1 body-structure 0 " + annotationNew.getTerm().length() + " " + annotationNew.getTerm();
    annotationNew.setFinalAnnotation(annotation);
    annotationNew.setState(AnnotationStateEnum.ANNOTATION_PROCESSING);
    annotationRepository.save(annotationNew);
    TestTransaction.flagForCommit();
    TestTransaction.end();

    assertStates(
        annotationNew.getTaskId(),
        AnnotationTaskState.DOING,
        AnnotationStateEnum.ANNOTATION_PROCESSING,
        AnnotationTaskState.DOING);

    // 回收标注任务
    TestTransaction.start();
    preAnnotationRecycleBiz.process(
        new AnnotationRecycleRequest(
            Collections.singletonList(annotationRepository.findByTermEquals(SAMPLE_TEXT).getId())),
        null);
    TestTransaction.flagForCommit();
    TestTransaction.end();

    assertStates(
        annotationNew.getTaskId(),
        AnnotationTaskState.DOING,
        AnnotationStateEnum.UN_DISTRIBUTED,
        AnnotationTaskState.DOING);

    // 提交标注任务
    TestTransaction.start();
    annotationNew.setState(AnnotationStateEnum.PRE_ANNOTATION);
    annotationNew = annotationRepository.save(annotationNew);
    annotationCommitBiz.process(
        new CommitAnnotationRequest(annotationNew.getId()),
        new UserDetails() {
          @Override
          public long getId() {
            return 0;
          }

          @Override
          public boolean hasPermission(String permission) {
            return false;
          }
        });
    TestTransaction.flagForCommit();
    TestTransaction.end();

    assertStates(
        annotationNew.getTaskId(),
        AnnotationTaskState.DOING,
        AnnotationStateEnum.SUBMITTED,
        AnnotationTaskState.ANNOTATED);
  }
}
