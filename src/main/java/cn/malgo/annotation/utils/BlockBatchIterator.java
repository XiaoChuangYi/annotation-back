package cn.malgo.annotation.utils;

import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

@Slf4j
public final class BlockBatchIterator implements Iterator<List<AnnotationTaskBlock>> {

  private final AnnotationTaskBlockRepository taskBlockRepository;
  private final List<AnnotationTaskState> states;
  private final int pageSize;
  private final Sort sort;
  private final long totalPage;
  private final List<AnnotationTypeEnum> annotationTypeEnums;

  private AtomicInteger pageIndex = new AtomicInteger(0);

  public BlockBatchIterator(
      final AnnotationTaskBlockRepository taskBlockRepository,
      final List<AnnotationTaskState> states,
      final int pageSize) {
    this(
        taskBlockRepository,
        states,
        pageSize,
        Sort.by(Direction.ASC, "id"),
        Arrays.asList(AnnotationTypeEnum.relation));
  }

  public BlockBatchIterator(
      final AnnotationTaskBlockRepository taskBlockRepository,
      final List<AnnotationTaskState> states,
      final int pageSize,
      final Sort sort,
      final List<AnnotationTypeEnum> annotationTypeEnums) {
    this.taskBlockRepository = taskBlockRepository;
    this.states = states;
    this.pageSize = pageSize;
    this.sort = sort;
    this.annotationTypeEnums = annotationTypeEnums;

    totalPage =
        taskBlockRepository
            .findAllByAnnotationTypeInAndStateIn(
                annotationTypeEnums, states, PageRequest.of(0, pageSize, sort))
            .getTotalPages();
    log.info(
        "block batch iterator, states: {}, pageSize: {}, totalPage: {}",
        states,
        pageSize,
        totalPage);
  }

  @Override
  public boolean hasNext() {
    return pageIndex.get() < totalPage;
  }

  @Override
  public List<AnnotationTaskBlock> next() {
    final int currentPage = pageIndex.getAndAdd(1);
    return taskBlockRepository
        .findAllByAnnotationTypeInAndStateIn(
            annotationTypeEnums, states, PageRequest.of(currentPage, pageSize, sort))
        .getContent();
  }
}
