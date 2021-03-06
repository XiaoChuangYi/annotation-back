package cn.malgo.annotation.dao;

import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface AnnotationTaskBlockRepository
    extends JpaRepository<AnnotationTaskBlock, Long>,
        JpaSpecificationExecutor<AnnotationTaskBlock> {

  AnnotationTaskBlock getOneByAnnotationTypeEqualsAndTextEquals(
      AnnotationTypeEnum annotationType, String text);

  /**
   * @param annotationType 标注类型
   * @param states Block状态
   * @param taskId {@link cn.malgo.annotation.entity.AnnotationTask#id}
   * @return 所有属于某个Task的Block
   */
  Set<AnnotationTaskBlock> findByAnnotationTypeAndStateInAndTaskBlocks_Task_IdEquals(
      AnnotationTypeEnum annotationType, List<AnnotationTaskState> states, long taskId);

  Set<AnnotationTaskBlock> findByAnnotationTypeEqualsAndStateIn(
      AnnotationTypeEnum annotationType, List<AnnotationTaskState> states);

  Set<AnnotationTaskBlock> findByIdInAndStateIn(Set<Long> ids, List<AnnotationTaskState> states);

  List<AnnotationTaskBlock> findByStateIn(List<AnnotationTaskState> states, Pageable pageable);

  List<AnnotationTaskBlock> findByStateIn(List<AnnotationTaskState> states, Sort sort);

  /**
   * @param taskId {@link cn.malgo.annotation.entity.AnnotationTask#id}
   * @return 所有属于某个Task的Block
   */
  Set<AnnotationTaskBlock> findByTaskBlocks_Task_IdEquals(long taskId);

  /**
   * @param taskId {@link cn.malgo.annotation.entity.AnnotationTask#id}
   * @param states Block状态
   * @return 所有属于某个Task的Block
   */
  Set<AnnotationTaskBlock> findByStateInAndTaskBlocks_Task_Id(
      List<AnnotationTaskState> states, long taskId);

  Set<AnnotationTaskBlock> findByIdInAndTaskBlocks_Task_Id(List<Long> blockIds, long taskId);

  default Pair<AnnotationTaskBlock, Boolean> getOrCreateBlock(
      final AnnotationTypeEnum annotationType, final String text) {
    final AnnotationTaskBlock block =
        getOneByAnnotationTypeEqualsAndTextEquals(annotationType, text);
    return block != null
        ? Pair.of(block, false)
        : Pair.of(save(new AnnotationTaskBlock(text, "", annotationType)), true);
  }

  List<AnnotationTaskBlock> findAllByStateInAndIdIn(
      List<AnnotationTaskState> stateList, List<Long> ids);

  List<AnnotationTaskBlock> findAllByStateIn(List<AnnotationTaskState> stateList, Pageable page);

  List<AnnotationTaskBlock> findAllByStateIn(List<AnnotationTaskState> stateList);

  List<AnnotationTaskBlock> findAllByStateInAndAnnotationTypeIn(
      List<AnnotationTaskState> stateList, List<AnnotationTypeEnum> annotationTypeEnums);

  Page<AnnotationTaskBlock> findAllByAnnotationTypeAndStateIn(
      final AnnotationTypeEnum annotationType, List<AnnotationTaskState> stateList, Pageable page);

  Page<AnnotationTaskBlock> findAllByAnnotationTypeInAndStateIn(
      List<AnnotationTypeEnum> annotationTypeEnums,
      List<AnnotationTaskState> stateList,
      Pageable page);

  int countAnnotationTaskBlocksByStateIn(List<AnnotationTaskState> states);

  //  @Modifying
  //  @Query(
  //      value =
  //          "INSERT INTO annotation_task_block_release
  // (`annotation`,`annotation_type`,`created_time`,`state`,text)\n"
  //              + "  SELECT annotation,annotation_type,NOW() as created_time,state,text FROM
  // annotation_task_block WHERE state='FINISHED'",
  //      nativeQuery = true)
  //  @Transactional
  //  void copyDataToRelease();
}
