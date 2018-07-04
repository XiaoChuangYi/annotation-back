package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.service.AnnotationBlockService;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.persistence.EntityNotFoundException;

public class AnnotationBlockServiceImplTest {
  private static final Answer<AnnotationTaskBlock> BLOCK_ANSWER =
      invocation -> {
        final Object[] args = invocation.getArguments();
        return new AnnotationTaskBlock((String) args[1], "", (AnnotationTypeEnum) args[0]);
      };

  private AnnotationCombineRepository mockAnnotationRepository;
  private AnnotationTaskBlockRepository mockBlockRepository;
  private AnnotationBlockService blockService;

  @BeforeMethod
  public void init() {
    mockAnnotationRepository = Mockito.mock(AnnotationCombineRepository.class);
    mockBlockRepository = Mockito.mock(AnnotationTaskBlockRepository.class);
    Mockito.when(mockBlockRepository.getOrCreateBlock(Mockito.any(), Mockito.anyString()))
        .thenCallRealMethod();
    blockService = new AnnotationBlockServiceImpl(mockAnnotationRepository, mockBlockRepository);
  }

  @Test
  public void testDuplicateBlock() {
    Mockito.when(
            mockBlockRepository.findByAnnotationTypeEqualsAndTextEquals(
                Mockito.any(), Mockito.anyString()))
        .thenThrow(new EntityNotFoundException())
        .thenAnswer(BLOCK_ANSWER);
    Assert.assertTrue(
        blockService.getOrCreateAnnotation(AnnotationTypeEnum.wordPos, "test").getRight());
    Assert.assertFalse(
        blockService.getOrCreateAnnotation(AnnotationTypeEnum.wordPos, "test").getRight());

    Mockito.verify(mockBlockRepository).save(Mockito.any());
    Mockito.verify(mockAnnotationRepository).save(Mockito.any());
    Mockito.verify(mockBlockRepository, Mockito.times(2))
        .getOrCreateBlock(Mockito.any(), Mockito.anyString());
  }
}
