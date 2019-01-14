// package cn.malgo.annotation;
//
// import cn.malgo.annotation.dao.AnnotationRepository;
// import cn.malgo.annotation.dao.AnnotationTaskRepository;
// import cn.malgo.annotation.dto.AnnotationSummary;
// import cn.malgo.annotation.entity.AnnotationNew;
// import cn.malgo.annotation.entity.AnnotationTask;
// import cn.malgo.annotation.entity.AtomicTerm;
// import cn.malgo.annotation.enums.AnnotationTypeEnum;
// import cn.malgo.annotation.request.anno.ListAnnotationRequest;
// import cn.malgo.annotation.service.AnnotationService;
// import com.alibaba.fastjson.JSON;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.List;
// import java.util.stream.Collectors;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.data.domain.Page;
// import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
// import org.testng.annotations.Test;
//
// @SpringBootTest
// @Slf4j
// public class AnnotationCombineApplicationTests extends AbstractTestNGSpringContextTests {
//
//  @Autowired private AnnotationService annotationService;
//  @Autowired private AnnotationRepository annotationRepository;
//  @Autowired private AnnotationTaskRepository taskRepository;
//
//  @Test
//  public void testEntityGraph() {
//    final List<AnnotationTask> annotationTasks = taskRepository.findAll();
//    log.info("annotationTasks：" + annotationTasks);
//  }
//
//  @Test(enabled = false)
//  public void testAnnotationCombine() {
//    ListAnnotationRequest annotationCombineQuery = new ListAnnotationRequest();
//    annotationCombineQuery.setPageIndex(1);
//    annotationCombineQuery.setPageSize(10);
//    annotationCombineQuery.setAnnotationTypes(Arrays.asList(0, 1));
//    annotationCombineQuery.setStates(Arrays.asList("a"));
//
//    Page<AnnotationNew> page = annotationService.listAnnotationNew(annotationCombineQuery);
//    log.info(">>>>>>>>data:" + JSON.toJSONString(page));
//  }
//
//  @Test(enabled = false)
//  public void testGroup() {
//    List<AnnotationSummary> annotationSummaries = annotationRepository.findByStateGroup();
//    for (AnnotationSummary current : annotationSummaries) {
//      log.info(">>>>>>>>state:" + current.getState() + ">>>>>>num:" + current.getNum());
//    }
//  }
//
//  @Test(enabled = false)
//  public void testAtomicTerm() {
//    List<AtomicTerm> atomicTermList = new ArrayList<>();
//    AtomicTerm atomicTerm = new AtomicTerm("a", "body-structure", 1L, AnnotationTypeEnum.wordPos);
//    atomicTermList.add(atomicTerm);
//    atomicTerm = new AtomicTerm("a", "body-structure", 2L, AnnotationTypeEnum.wordPos);
//    atomicTermList.add(atomicTerm);
//    atomicTerm = new AtomicTerm("b", "Error", 3L, AnnotationTypeEnum.wordPos);
//    atomicTermList.add(atomicTerm);
//    log.info("result：" + atomicTermList.stream().distinct().collect(Collectors.toList()));
//  }
// }
