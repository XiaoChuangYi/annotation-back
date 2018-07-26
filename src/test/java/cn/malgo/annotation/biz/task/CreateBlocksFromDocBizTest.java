package cn.malgo.annotation.biz.task;

import cn.malgo.annotation.biz.doc.CreateBlocksFromDocBiz;
import cn.malgo.annotation.dao.OriginalDocRepository;
import cn.malgo.annotation.entity.OriginalDoc;
import cn.malgo.annotation.enums.OriginalDocState;
import cn.malgo.annotation.request.task.CreateBlocksFromDocRequest;
import cn.malgo.annotation.service.OriginalDocService;
import cn.malgo.annotation.service.impl.UserAccountServiceImpl;
import java.util.Collections;
import org.apache.commons.lang3.tuple.Pair;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CreateBlocksFromDocBizTest {
  private CreateBlocksFromDocBiz createBlocksFromDocBiz;
  private OriginalDocRepository docRepository;
  private OriginalDocService originalDocService;

  private OriginalDoc originalDoc;

  @BeforeMethod
  public void init() {
    originalDoc = new OriginalDoc("test-doc", "test-text", "", "");

    docRepository = Mockito.mock(OriginalDocRepository.class);
    Mockito.when(docRepository.findAllById(Mockito.any()))
        .thenReturn(Collections.singletonList(originalDoc));

    originalDocService = Mockito.mock(OriginalDocService.class);
    createBlocksFromDocBiz = new CreateBlocksFromDocBiz(docRepository, originalDocService);
  }

  @Test
  public void testAddDocStateFromCreatedToProcessing() {
    mockAddDocToTask(1);

    Assert.assertEquals(originalDoc.getState(), OriginalDocState.IMPORTED);

    createBlocksFromDocBiz.process(
        new CreateBlocksFromDocRequest(Collections.singleton(1L), 0),
        UserAccountServiceImpl.DefaultUserDetails.ADMIN);

    Assert.assertEquals(originalDoc.getState(), OriginalDocState.PROCESSING);
  }

  @Test
  public void testAddDocStateNoBlockCreated() {
    mockAddDocToTask(0);

    Assert.assertEquals(originalDoc.getState(), OriginalDocState.IMPORTED);

    createBlocksFromDocBiz.process(
        new CreateBlocksFromDocRequest(Collections.singleton(1L), 0),
        UserAccountServiceImpl.DefaultUserDetails.ADMIN);

    Assert.assertEquals(originalDoc.getState(), OriginalDocState.PROCESSING);
  }

  private void mockAddDocToTask(final int i) {
    Mockito.when(originalDocService.createBlocks(Mockito.any(OriginalDoc.class), Mockito.any()))
        .thenAnswer(
            invocation -> {
              final Object[] arguments = invocation.getArguments();
              return Pair.of((OriginalDoc) arguments[0], i);
            });
  }
}
