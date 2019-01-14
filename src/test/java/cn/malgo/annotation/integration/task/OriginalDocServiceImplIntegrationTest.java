// package cn.malgo.annotation.integration.task;
//
// import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
// import cn.malgo.annotation.dao.OriginalDocRepository;
// import cn.malgo.annotation.entity.OriginalDoc;
// import cn.malgo.annotation.enums.AnnotationTaskState;
// import cn.malgo.annotation.enums.AnnotationTypeEnum;
// import cn.malgo.annotation.service.OriginalDocService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.test.annotation.DirtiesContext;
// import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
// import org.springframework.test.context.transaction.TestTransaction;
// import org.testng.Assert;
// import org.testng.annotations.BeforeMethod;
// import org.testng.annotations.Test;
//
// @SpringBootTest
// @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
// public class OriginalDocServiceImplIntegrationTest
//    extends AbstractTransactionalTestNGSpringContextTests {
//  @Autowired private OriginalDocService originalDocService;
//  @Autowired private OriginalDocRepository docRepository;
//  @Autowired private AnnotationTaskBlockRepository taskBlockRepository;
//
//  @BeforeMethod()
//  public void setUp() {
//    docRepository.save(new OriginalDoc("test-doc", "test", "", ""));
//  }
//
//  @Test
//  public void testCreateBlocks() {
//    final OriginalDoc doc = docRepository.findAll().get(0);
//    originalDocService.createBlocks(doc, AnnotationTypeEnum.wordPos);
//    TestTransaction.flagForCommit();
//    TestTransaction.end();
//
//    TestTransaction.start();
//    Assert.assertEquals(countRowsInTable("annotation_task_block"), 1);
//    Assert.assertEquals(countRowsInTable("original_doc_block"), 1);
//    Assert.assertEquals(
//        taskBlockRepository.findAll().get(0).getState(), AnnotationTaskState.CREATED);
//    TestTransaction.end();
//  }
// }
