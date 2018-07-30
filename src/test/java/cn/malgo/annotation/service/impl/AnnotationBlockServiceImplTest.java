package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.service.AnnotationBlockService;
import java.util.ArrayList;
import org.apache.commons.lang3.tuple.Pair;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AnnotationBlockServiceImplTest {
  private static final Answer<AnnotationTaskBlock> BLOCK_ANSWER =
      invocation -> {
        final Object[] args = invocation.getArguments();
        final AnnotationTaskBlock taskBlock =
            new AnnotationTaskBlock((String) args[1], "", (AnnotationTypeEnum) args[0]);
        taskBlock.setState(AnnotationTaskState.DOING);
        return taskBlock;
      };

  private AnnotationCombineRepository mockAnnotationRepository;
  private AnnotationTaskBlockRepository mockBlockRepository;
  private AnnotationBlockService blockService;

  @BeforeMethod
  public void init() {
    mockAnnotationRepository = Mockito.mock(AnnotationCombineRepository.class);
    Mockito.when(mockAnnotationRepository.save(Mockito.any()))
        .thenAnswer(invocation -> invocation.getArguments()[0]);

    mockBlockRepository = Mockito.mock(AnnotationTaskBlockRepository.class);
    Mockito.when(mockBlockRepository.getOrCreateBlock(Mockito.any(), Mockito.anyString()))
        .thenCallRealMethod();
    Mockito.when(mockBlockRepository.save(Mockito.any()))
        .thenAnswer(invocation -> invocation.getArguments()[0]);

    blockService =
        new AnnotationBlockServiceImpl(mockAnnotationRepository, mockBlockRepository, null);
  }

  @Test(enabled = false)
  public void testDuplicateBlock() {
    Mockito.when(
            mockBlockRepository.getOneByAnnotationTypeEqualsAndTextEquals(
                Mockito.any(), Mockito.anyString()))
        .thenReturn(null)
        .thenAnswer(BLOCK_ANSWER);
    Mockito.when(mockBlockRepository.getOne(Mockito.any()))
        .thenReturn(
            new AnnotationTaskBlock(
                "test",
                "",
                "",
                0,
                AnnotationTaskState.DOING,
                AnnotationTypeEnum.wordPos,
                new ArrayList<>(),
                new ArrayList<>()));

    final Pair<AnnotationTaskBlock, Boolean> result1 =
        blockService.getOrCreateAnnotation(AnnotationTypeEnum.wordPos, "test", true);
    final Pair<AnnotationTaskBlock, Boolean> result2 =
        blockService.getOrCreateAnnotation(AnnotationTypeEnum.wordPos, "test", true);

    Assert.assertTrue(result1.getRight());
    Assert.assertFalse(result2.getRight());

    Assert.assertEquals(result1.getLeft().getState(), AnnotationTaskState.DOING);
    Assert.assertEquals(result2.getLeft().getState(), AnnotationTaskState.DOING);

    // save会被调用两次，第一次创建一个block，第二次修改block的状态
    Mockito.verify(mockBlockRepository, Mockito.times(2)).save(Mockito.any());
    Mockito.verify(mockAnnotationRepository).save(Mockito.any());
    Mockito.verify(mockBlockRepository, Mockito.times(1))
        .getOrCreateBlock(Mockito.any(), Mockito.anyString());
    Mockito.verify(mockBlockRepository, Mockito.times(1)).getOne(Mockito.any());
  }
}
