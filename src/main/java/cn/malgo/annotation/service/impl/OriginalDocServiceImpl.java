package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.OriginalDocRepository;
import cn.malgo.annotation.dto.AutoAnnotationRequest;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.entity.OriginalDoc;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.enums.OriginalDocState;
import cn.malgo.annotation.service.AnnotationBlockService;
import cn.malgo.annotation.service.OriginalDocService;
import cn.malgo.annotation.service.feigns.AlgorithmApiClient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class OriginalDocServiceImpl implements OriginalDocService {
  private static final int MINIMUM_WORD_BLOCK_LENGTH = 10;

  private final OriginalDocRepository docRepository;
  private final AnnotationBlockService annotationBlockService;

  private final Map<AnnotationTypeEnum, BlockSplitter> splitters;

  public OriginalDocServiceImpl(
      final AlgorithmApiClient algorithmApiClient,
      final OriginalDocRepository docRepository,
      final AnnotationBlockService annotationBlockService) {
    this.docRepository = docRepository;
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
  public Pair<OriginalDoc, Integer> createBlocks(
      final OriginalDoc doc, final AnnotationTypeEnum annotationType) {
    if (splitters.containsKey(annotationType)) {
      final List<String> blocks = splitters.get(annotationType).split(doc);
      final Pair<List<AnnotationTaskBlock>, Integer> result = createBlocks(annotationType, blocks);
      IntStream.range(0, result.getLeft().size())
          .forEach(index -> doc.addBlock(result.getLeft().get(index), index));
      doc.setState(OriginalDocState.PROCESSING);
      return Pair.of(docRepository.save(doc), result.getRight());
    }

    final Pair<AnnotationTaskBlock, Boolean> result =
        annotationBlockService.getOrCreateAnnotation(annotationType, doc.getText(), false);
    doc.addBlock(result.getLeft(), 0);
    doc.setState(OriginalDocState.PROCESSING);
    return Pair.of(docRepository.save(doc), result.getRight() ? 1 : 0);
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
                    block ->
                        annotationBlockService.getOrCreateAnnotation(
                            annotationType, block, false)));

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
}
