package cn.malgo.annotation.service.impl;

import static org.mockito.ArgumentMatchers.eq;

import cn.malgo.annotation.dao.OriginalDocRepository;
import cn.malgo.annotation.dto.AutoAnnotation;
import cn.malgo.annotation.dto.AutoAnnotationRequest;
import cn.malgo.annotation.dto.UpdateAnnotationAlgorithmRequest;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.entity.OriginalDoc;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.enums.OriginalDocState;
import cn.malgo.annotation.service.AnnotationBlockService;
import cn.malgo.annotation.service.feigns.AlgorithmApiClient;
import cn.malgo.core.definition.Document;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class OriginalDocServiceImplTest {
  private static final String SAMPLE_TEXT = "这是第一句话，这是第二句话，这是第三句话而且足够长足够长足够长足够长足够长";

  private static final Answer<Pair<AnnotationTaskBlock, Boolean>> BLOCK_ANSWER =
      invocation -> {
        final Object[] args = invocation.getArguments();
        final AnnotationTaskBlock block =
            new AnnotationTaskBlock((String) args[1], "", (AnnotationTypeEnum) args[0]);
        block.setState(AnnotationTaskState.DOING);
        return Pair.of(block, true);
      };

  private OriginalDocRepository mockDocRepository;
  private AnnotationBlockService mockBlockService;
  private OriginalDocServiceImpl docService;
  private OriginalDoc doc;

  @BeforeMethod
  public void init() {
    mockDocRepository = Mockito.mock(OriginalDocRepository.class);
    Mockito.when(mockDocRepository.save(Mockito.any()))
        .thenAnswer(invocation -> invocation.getArguments()[0]);

    mockBlockService = Mockito.mock(AnnotationBlockService.class);
    Mockito.when(
            mockBlockService.getOrCreateAnnotation(
                Mockito.any(), Mockito.anyString(), Mockito.anyBoolean()))
        .thenAnswer(BLOCK_ANSWER);

    docService =
        new OriginalDocServiceImpl(
            new AlgorithmApiClient() {
              @Override
              public List<AutoAnnotation> listCasePrepareAnnotation(
                  final List<AutoAnnotationRequest> annotationOriginTextRequestList) {
                return null;
              }

              @Override
              public List<AutoAnnotation> batchUpdateAnnotationTokenizePos(
                  final List<UpdateAnnotationAlgorithmRequest> updateAnnotationRequestList) {
                return null;
              }

              @Override
              public List<List<String>> batchBlockSplitter(
                  final List<AutoAnnotationRequest> updateAnnotationRequestList) {
                return updateAnnotationRequestList
                    .stream()
                    .map(request -> Collections.singletonList(request.getText()))
                    .collect(Collectors.toList());
              }

              @Override
              public List<Document> batchNer(final List<AutoAnnotationRequest> texts) {
                return null;
              }
            },
            mockDocRepository,
            mockBlockService);
    doc = new OriginalDoc("test-doc", SAMPLE_TEXT, "", "");
  }

  @Test
  public void testAddRelationTask() {
    final Pair<OriginalDoc, Integer> result =
        docService.createBlocks(doc, AnnotationTypeEnum.relation);

    // check blocks
    Assert.assertEquals(doc.getBlocks().size(), 1);
    Assert.assertEquals(doc, result.getLeft());
    Assert.assertEquals(doc.getBlocks().size(), 1);
    Assert.assertEquals(doc.getBlocks().get(0).getBlock().getText(), SAMPLE_TEXT);

    // check states
    Assert.assertEquals(doc.getState(), OriginalDocState.PROCESSING);
    Assert.assertEquals(doc.getBlocks().get(0).getBlock().getState(), AnnotationTaskState.DOING);

    Mockito.verify(mockBlockService)
        .getOrCreateAnnotation(AnnotationTypeEnum.relation, SAMPLE_TEXT, false);
  }

  @Test
  public void testAddWordTask() {
    docService.createBlocks(doc, AnnotationTypeEnum.wordPos);
    Assert.assertEquals(doc.getBlocks().size(), 2);
    Assert.assertEquals(doc.getBlocks().get(0).getBlock().getText(), "这是第一句话，这是第二句话");
    Assert.assertEquals(doc.getBlocks().get(1).getBlock().getText(), "这是第三句话而且足够长足够长足够长足够长足够长");
    Mockito.verify(mockBlockService, Mockito.times(2))
        .getOrCreateAnnotation(eq(AnnotationTypeEnum.wordPos), Mockito.anyString(), eq(false));
  }
}
