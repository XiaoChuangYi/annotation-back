package cn.malgo.annotation.integration.task;

import cn.malgo.annotation.biz.brat.task.AnnotationExamineBiz;
import cn.malgo.annotation.biz.task.AddDocsToTaskBiz;
import cn.malgo.annotation.biz.task.CreateTaskBiz;
import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.dao.OriginalDocRepository;
import cn.malgo.annotation.entity.*;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.enums.OriginalDocState;
import cn.malgo.annotation.request.AnnotationStateRequest;
import cn.malgo.annotation.request.task.AddDocsToTaskRequest;
import cn.malgo.annotation.request.task.CreateTaskRequest;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TestTransaction;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.testng.Assert.*;

/** 任务系统集成测试，任务创建过程，标注过程，各种状态检查 */
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class IntegrationTaskTest extends AbstractTransactionalTestNGSpringContextTests {
  private static final String SAMPLE_TEXT = "这是第一句话，这是第二句话，这是第三句话而且足够长足够长足够长足够长足够长";

  @Autowired private CreateTaskBiz createTaskBiz;
  @Autowired private OriginalDocRepository docRepository;
  @Autowired private AddDocsToTaskBiz addDocsToTaskBiz;
  @Autowired private AnnotationTaskRepository taskRepository;
  @Autowired private AnnotationExamineBiz annotationExamineBiz;
  @Autowired private AnnotationCombineRepository annotationCombineRepository;

  /**
   *
   *
   * <ul>
   *   <li>创建任务和文档
   *   <li>添加文档为分词任务
   *   <li>分别标注两条数据
   * </ul>
   */
  @Test
  public void testTaskProcessSingleDoc() {
    final Pair<AnnotationTask, OriginalDoc> taskAndDoc = createTaskAndDoc();

    TestTransaction.start();

    addDocsToTaskBiz.process(
        new AddDocsToTaskRequest(
            taskAndDoc.getLeft().getId(),
            Collections.singleton(taskAndDoc.getRight().getId()),
            AnnotationTypeEnum.wordPos.ordinal()),
        0,
        1);

    TestTransaction.flagForCommit();
    TestTransaction.end();

    checkStateAfterAddingDoc(
        taskAndDoc.getLeft().getId(),
        taskAndDoc.getRight().getId(),
        2,
        1,
        AnnotationTaskState.DOING,
        0,
        AnnotationTaskState.DOING,
        new AnnotationTaskState[] {AnnotationTaskState.DOING, AnnotationTaskState.DOING});

    final List<AnnotationCombine> annotations = annotationCombineRepository.findAll();
    assertEquals(annotations.size(), 2);

    finishAnnotation(annotations.get(0));
    checkStateAfterAddingDoc(
        taskAndDoc.getLeft().getId(),
        taskAndDoc.getRight().getId(),
        2,
        1,
        AnnotationTaskState.DOING,
        0,
        AnnotationTaskState.DOING,
        new AnnotationTaskState[] {AnnotationTaskState.ANNOTATED, AnnotationTaskState.DOING});

    finishAnnotation(annotations.get(1));
    checkStateAfterAddingDoc(
        taskAndDoc.getLeft().getId(),
        taskAndDoc.getRight().getId(),
        2,
        1,
        AnnotationTaskState.ANNOTATED,
        0,
        AnnotationTaskState.ANNOTATED,
        new AnnotationTaskState[] {AnnotationTaskState.ANNOTATED, AnnotationTaskState.ANNOTATED});
  }

  /**
   *
   *
   * <ul>
   *   <li>创建任务和文档
   *   <li>添加文档为分词任务
   *   <li>添加文档为关联任务
   *   <li>分别标注三条数据
   * </ul>
   */
  @Test
  public void testTaskProcessMultipleDoc() {
    final Pair<AnnotationTask, OriginalDoc> taskAndDoc = createTaskAndDoc();

    TestTransaction.start();

    addDocsToTaskBiz.process(
        new AddDocsToTaskRequest(
            taskAndDoc.getLeft().getId(),
            Collections.singleton(taskAndDoc.getRight().getId()),
            AnnotationTypeEnum.wordPos.ordinal()),
        0,
        1);

    addDocsToTaskBiz.process(
        new AddDocsToTaskRequest(
            taskAndDoc.getLeft().getId(),
            Collections.singleton(taskAndDoc.getRight().getId()),
            AnnotationTypeEnum.relation.ordinal()),
        0,
        1);

    TestTransaction.flagForCommit();
    TestTransaction.end();

    checkStateAfterAddingDoc(
        taskAndDoc.getLeft().getId(),
        taskAndDoc.getRight().getId(),
        3,
        2,
        AnnotationTaskState.DOING,
        0,
        AnnotationTaskState.DOING,
        new AnnotationTaskState[] {AnnotationTaskState.DOING, AnnotationTaskState.DOING});

    checkStateAfterAddingDoc(
        taskAndDoc.getLeft().getId(),
        taskAndDoc.getRight().getId(),
        3,
        2,
        AnnotationTaskState.DOING,
        1,
        AnnotationTaskState.DOING,
        new AnnotationTaskState[] {AnnotationTaskState.DOING});

    final List<AnnotationCombine> annotations = annotationCombineRepository.findAll();
    assertEquals(annotations.size(), 3);

    finishAnnotation(annotations.get(0));
    checkStateAfterAddingDoc(
        taskAndDoc.getLeft().getId(),
        taskAndDoc.getRight().getId(),
        3,
        2,
        AnnotationTaskState.DOING,
        0,
        AnnotationTaskState.DOING,
        new AnnotationTaskState[] {AnnotationTaskState.ANNOTATED, AnnotationTaskState.DOING});
    checkStateAfterAddingDoc(
        taskAndDoc.getLeft().getId(),
        taskAndDoc.getRight().getId(),
        3,
        2,
        AnnotationTaskState.DOING,
        1,
        AnnotationTaskState.DOING,
        new AnnotationTaskState[] {AnnotationTaskState.DOING});

    finishAnnotation(annotations.get(1));
    checkStateAfterAddingDoc(
        taskAndDoc.getLeft().getId(),
        taskAndDoc.getRight().getId(),
        3,
        2,
        AnnotationTaskState.DOING,
        0,
        AnnotationTaskState.ANNOTATED,
        new AnnotationTaskState[] {AnnotationTaskState.ANNOTATED, AnnotationTaskState.ANNOTATED});
    checkStateAfterAddingDoc(
        taskAndDoc.getLeft().getId(),
        taskAndDoc.getRight().getId(),
        3,
        2,
        AnnotationTaskState.DOING,
        1,
        AnnotationTaskState.DOING,
        new AnnotationTaskState[] {AnnotationTaskState.DOING});

    finishAnnotation(annotations.get(2));
    checkStateAfterAddingDoc(
        taskAndDoc.getLeft().getId(),
        taskAndDoc.getRight().getId(),
        3,
        2,
        AnnotationTaskState.ANNOTATED,
        0,
        AnnotationTaskState.ANNOTATED,
        new AnnotationTaskState[] {AnnotationTaskState.ANNOTATED, AnnotationTaskState.ANNOTATED});
    checkStateAfterAddingDoc(
        taskAndDoc.getLeft().getId(),
        taskAndDoc.getRight().getId(),
        3,
        2,
        AnnotationTaskState.ANNOTATED,
        1,
        AnnotationTaskState.ANNOTATED,
        new AnnotationTaskState[] {AnnotationTaskState.ANNOTATED});
  }

  private Pair<AnnotationTask, OriginalDoc> createTaskAndDoc() {
    final AnnotationTask task = createTaskBiz.process(new CreateTaskRequest("test-task"), 0, 1);

    assertNotNull(task);
    assertNotEquals(task.getId(), 0);
    assertEquals(task.getState(), AnnotationTaskState.CREATED);

    final OriginalDoc doc = docRepository.save(new OriginalDoc("test-doc", SAMPLE_TEXT, "", ""));

    assertNotNull(doc);
    assertNotEquals(doc.getId(), 0);
    assertEquals(doc.getState(), OriginalDocState.IMPORTED);

    TestTransaction.flagForCommit();
    TestTransaction.end();

    return Pair.of(task, doc);
  }

  private void checkStateAfterAddingDoc(
      final int taskId,
      final int docId,
      final int annotationSize,
      final int docSize,
      final AnnotationTaskState taskState,
      final int docIndex,
      final AnnotationTaskState taskDocState,
      final AnnotationTaskState[] blockStats) {
    assertEquals(countRowsInTable("annotation_combine"), annotationSize);
    assertEquals(countRowsInTable("annotation_task_block"), annotationSize);
    assertEquals(countRowsInTable("annotation_task_doc_block"), annotationSize);
    assertEquals(countRowsInTable("annotation_task_doc"), docSize);

    TestTransaction.start();
    final AnnotationTask task = taskRepository.getOne(taskId);

    assertEquals(task.getState(), taskState);

    final List<AnnotationTaskDoc> taskDocs = task.getTaskDocs();
    assertEquals(taskDocs.size(), docSize);

    final AnnotationTaskDoc taskDoc = taskDocs.get(docIndex);
    assertEquals(taskDoc.getState(), taskDocState);
    assertEquals(taskDoc.getDoc().getId(), docId);

    final List<AnnotationTaskDocBlock> blocks = taskDoc.getBlocks();
    assertEquals(blocks.size(), blockStats.length);
    IntStream.range(0, blockStats.length)
        .forEach(index -> assertEquals(blocks.get(index).getBlock().getState(), blockStats[index]));

    TestTransaction.flagForCommit();
    TestTransaction.end();
  }

  private void finishAnnotation(final AnnotationCombine annotationCombine) {
    TestTransaction.start();
    final String annotation =
        "T1 body-structure 0 "
            + annotationCombine.getTerm().length()
            + " "
            + annotationCombine.getTerm();
    annotationCombine.setFinalAnnotation(annotation);
    annotationCombine.setReviewedAnnotation(annotation);
    annotationCombine.setState(AnnotationCombineStateEnum.preExamine.name());
    annotationCombineRepository.save(annotationCombine);
    TestTransaction.flagForCommit();
    TestTransaction.end();

    TestTransaction.start();
    annotationExamineBiz.process(new AnnotationStateRequest(annotationCombine.getId()), 0, 1);
    TestTransaction.flagForCommit();
    TestTransaction.end();
  }
}
