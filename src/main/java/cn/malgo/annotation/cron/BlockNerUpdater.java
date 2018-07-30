package cn.malgo.annotation.cron;

import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.dto.AutoAnnotationRequest;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.service.feigns.AlgorithmApiClient;
import cn.malgo.annotation.utils.AnnotationDocumentManipulator;
import cn.malgo.annotation.utils.entity.AnnotationDocument;
import cn.malgo.core.definition.Document;
import cn.malgo.core.definition.Entity;
import cn.malgo.core.definition.utils.DocumentManipulator;
import cn.malgo.service.exception.DependencyServiceException;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BlockNerUpdater {
  private final int batchSize;
  private final ForkJoinPool forkJoinPool;
  private final AnnotationTaskBlockRepository taskBlockRepository;
  private final AlgorithmApiClient algorithmApiClient;

  public BlockNerUpdater(
      @Value("${malgo.config.ner-update-batch-size}") final int batchSize,
      @Value("${malgo.config.ner-update-parallel-number}") final int parallelNumber,
      final AnnotationTaskBlockRepository taskBlockRepository,
      final AlgorithmApiClient algorithmApiClient) {
    this.batchSize = batchSize;
    this.forkJoinPool = new ForkJoinPool(parallelNumber);
    this.taskBlockRepository = taskBlockRepository;
    this.algorithmApiClient = algorithmApiClient;
  }

  private String preProcessTerm(final Entity entity) {
    String term = entity.getTerm();
    if (StringUtils.equalsAny(
        entity.getType(), "Quantity", "Time", "Body-structure", "Observable-entity")) {
      term = term.replaceAll("\\d+", "");
    }
    return term;
  }

  @Scheduled(cron = "${malgo.config.block-rate-update-cron}")
  @Synchronized
  public void updateBlockRate() {
    forkJoinPool.submit(
        () -> {
          log.info("开始更新block ner rate");
          final long start = new Date().getTime();
          final AtomicInteger success = new AtomicInteger(0);
          final AtomicInteger failed = new AtomicInteger(0);

          final Set<Pair<String, String>> oldEntities =
              taskBlockRepository
                  .findAllByStateIn(
                      Arrays.asList(AnnotationTaskState.ANNOTATED, AnnotationTaskState.FINISHED))
                  .parallelStream()
                  .flatMap(
                      block -> {
                        final AnnotationDocument document = new AnnotationDocument(block.getText());
                        AnnotationDocumentManipulator.parseBratAnnotation(
                            block.getAnnotation(), document);
                        return document
                            .getEntities()
                            .parallelStream()
                            .map(entity -> Pair.of(entity.getType(), preProcessTerm(entity)));
                      })
                  .collect(Collectors.toSet());

          taskBlockRepository.saveAll(
              taskBlockRepository
                  .findAllByStateIn(Collections.singletonList(AnnotationTaskState.CREATED))
                  .parallelStream()
                  .peek(
                      block -> {
                        boolean result = false;

                        try {
                          final AnnotationDocument document =
                              new AnnotationDocument(block.getText());
                          if (StringUtils.isEmpty(block.getNerResult())) {
                            block.setNerFreshRate(1);
                            result = true;
                            return;
                          }

                          AnnotationDocumentManipulator.parseBratAnnotation(
                              block.getNerResult(), document);
                          String text = block.getText();
                          for (Entity entity : document.getEntities()) {
                            if (oldEntities.contains(
                                Pair.of(entity.getType(), preProcessTerm(entity)))) {
                              text = text.replace(entity.getTerm(), "");
                            }
                          }
                          block.setNerFreshRate(text.length() / (double) block.getText().length());
                          result = true;
                        } finally {
                          if (result) {
                            success.addAndGet(1);
                          } else {
                            failed.addAndGet(1);
                          }
                        }
                      })
                  .collect(Collectors.toList()));

          log.info(
              "更新block ner rate完成, time: {}, success: {}, failed: {}",
              new Date().getTime() - start,
              success.get(),
              failed.get());
        });
  }

  @Scheduled(cron = "${malgo.config.ner-update-cron}")
  @Synchronized
  public void updateBlockNer() {
    forkJoinPool.submit(
        () -> {
          log.info("开始更新block ner结果");
          final long start = new Date().getTime();
          final AtomicInteger success = new AtomicInteger(0);
          final AtomicInteger failed = new AtomicInteger(0);
          Lists.partition(
                  taskBlockRepository.findAllByStateIn(
                      Collections.singletonList(AnnotationTaskState.CREATED)),
                  batchSize)
              .parallelStream()
              .forEach(
                  blocks -> {
                    try {
                      final List<Document> documents =
                          algorithmApiClient.batchNer(
                              blocks
                                  .stream()
                                  .map(
                                      block ->
                                          new AutoAnnotationRequest(block.getId(), block.getText()))
                                  .collect(Collectors.toList()));

                      if (blocks.size() != documents.size()) {
                        throw new DependencyServiceException("NER返回的文档数和请求数不一致");
                      }

                      for (int i = 0; i < documents.size(); ++i) {
                        blocks
                            .get(i)
                            .setNerResult(DocumentManipulator.toBratAnnotations(documents.get(i)));
                        taskBlockRepository.save(blocks.get(i));
                      }

                      success.addAndGet(documents.size());
                    } catch (Exception ex) {
                      log.error("调用算法NER错误", ex);
                      failed.addAndGet(blocks.size());
                    }
                  });
          log.info(
              "更新block ner结果完成, time: {}, success: {}, failed: {}",
              new Date().getTime() - start,
              success.get(),
              failed.get());
        });
  }
}
