package cn.malgo.annotation.dao;

import cn.malgo.annotation.dto.AnnotationEstimate;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.enums.AnnotationTypeEnum;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface AnnotationTaskBlockRepository
    extends JpaRepository<AnnotationTaskBlock, Integer>,
        JpaSpecificationExecutor<AnnotationTaskBlock> {

  AnnotationTaskBlock getOneByAnnotationTypeEqualsAndTextEquals(
      AnnotationTypeEnum annotationType, String text);

  /**
   * @param annotationType 标注类型
   * @param states Block状态
   * @param taskId {@link cn.malgo.annotation.entity.AnnotationTask#id}
   * @return 所有属于某个Task的Block
   */
  List<AnnotationTaskBlock> findByAnnotationTypeEqualsAndStateInAndTaskDocs_TaskDoc_Task_IdEquals(
      AnnotationTypeEnum annotationType, List<AnnotationTaskState> states, int taskId);

  default Pair<AnnotationTaskBlock, Boolean> getOrCreateBlock(
      final AnnotationTypeEnum annotationType, final String text) {
    final AnnotationTaskBlock block =
        getOneByAnnotationTypeEqualsAndTextEquals(annotationType, text);
    return block != null
        ? Pair.of(block, false)
        : Pair.of(save(new AnnotationTaskBlock(text, "", annotationType)), true);
  }

  List<AnnotationTaskBlock> findAllByStateInAndIdIn(
      List<AnnotationTaskState> stateList, List<Integer> ids);
}
