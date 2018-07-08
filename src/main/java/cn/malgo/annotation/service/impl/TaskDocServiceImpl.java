package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AnnotationTaskDocRepository;
import cn.malgo.annotation.dto.AutoAnnotationRequest;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.entity.AnnotationTaskDoc;
import cn.malgo.annotation.entity.OriginalDoc;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.service.AnnotationBlockService;
import cn.malgo.annotation.service.TaskDocService;
import cn.malgo.annotation.service.feigns.AlgorithmApiClient;
import lombok.Synchronized;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional
@Slf4j
public class TaskDocServiceImpl implements TaskDocService {
  private static final int MINIMUM_WORD_BLOCK_LENGTH = 10;

  private final AlgorithmApiClient algorithmApiClient;
  private final AnnotationTaskDocRepository taskDocRepository;
  private final AnnotationBlockService annotationBlockService;

  private final Map<AnnotationTypeEnum, BlockSplitter> splitters;

  public TaskDocServiceImpl(
      final AlgorithmApiClient algorithmApiClient,
      final AnnotationTaskDocRepository taskDocRepository,
      final AnnotationBlockService annotationBlockService) {
    this.algorithmApiClient = algorithmApiClient;
    this.taskDocRepository = taskDocRepository;
    this.annotationBlockService = annotationBlockService;

    splitters = new HashMap<>();
    splitters.put(AnnotationTypeEnum.wordPos, this::createWordPosBlocks);
    splitters.put(
        AnnotationTypeEnum.relation,
        doc -> {
          List<List<String>> blocks =
              algorithmApiClient.batchBlockSplitter(
                  Collections.singletonList(new AutoAnnotationRequest(doc.getId(), doc.getText())));
          if (blocks == null
              || blocks.size() == 0
              || blocks.get(0) == null
              || blocks.get(0).size() == 0) {
            log.warn("split relation text {}, return error: {}", doc.getText(), blocks);
            return Collections.singletonList(doc.getText());
          }

          return blocks.get(0);
        });
  }

  @Override
  @Synchronized
  public AddDocResult addDocToTask(
      final AnnotationTask task, final OriginalDoc doc, final AnnotationTypeEnum annotationType) {
    final AnnotationTaskDoc taskDoc = task.addDoc(doc, annotationType);

    if (splitters.containsKey(annotationType)) {
      final List<String> blocks = splitters.get(annotationType).split(doc);
      final Pair<List<AnnotationTaskBlock>, Integer> result = createBlocks(annotationType, blocks);
      IntStream.range(0, result.getLeft().size())
          .forEach(index -> taskDoc.addBlock(result.getLeft().get(index), index));
      return new AddDocResult(taskDocRepository.updateState(taskDoc), result.getRight());
    }

    final Pair<AnnotationTaskBlock, Boolean> result =
        annotationBlockService.getOrCreateAnnotation(annotationType, doc.getText());
    taskDoc.addBlock(result.getLeft(), 0);
    return new AddDocResult(taskDocRepository.updateState(taskDoc), result.getRight() ? 1 : 0);
  }

  private Pair<List<AnnotationTaskBlock>, Integer> createBlocks(
      final AnnotationTypeEnum annotationType, final List<String> blocks) {
    // 单次block create内部必须增加去重，不然在保存的时候会出现duplicate的问题
    final Map<String, Pair<AnnotationTaskBlock, Boolean>> results =
        new HashSet<>(blocks)
            .stream()
            .collect(
                Collectors.toMap(
                    block -> block,
                    block -> annotationBlockService.getOrCreateAnnotation(annotationType, block)));

    return Pair.of(
        blocks.stream().map(block -> results.get(block).getLeft()).collect(Collectors.toList()),
        results.values().stream().mapToInt(result -> result.getRight() ? 1 : 0).sum());
  }

  private List<String> createWordPosBlocks(final OriginalDoc doc) {
    final String text = doc.getText();
    final String[] sentences = text.split("，");
    final List<String> blocks = new ArrayList<>();
    final StringBuilder current = new StringBuilder(32);

    for (int i = 0; i < sentences.length; ++i) {
      current.append(sentences[i]).append("，");

      if (current.length() > MINIMUM_WORD_BLOCK_LENGTH || i == sentences.length - 1) {
        blocks.add(current.substring(0, current.length() - 1));
        current.setLength(0);
      }
    }

    return blocks;
  }

  interface BlockSplitter {
    List<String> split(final OriginalDoc doc);
  }

  @Value
  public static final class AddDocResult {
    private final AnnotationTaskDoc taskDoc;
    private final int createdBlocks;
  }
}
