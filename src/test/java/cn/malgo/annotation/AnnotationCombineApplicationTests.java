package cn.malgo.annotation;

import cn.malgo.annotation.entity.AtomicTerm;
import com.alibaba.fastjson.JSON;
import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.dto.AnnotationSummary;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.request.ListAnnotationCombineRequest;
import cn.malgo.annotation.service.AnnotationCombineService;
import java.util.ArrayList;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
@Slf4j
public class AnnotationCombineApplicationTests extends AbstractTestNGSpringContextTests {
  @Autowired private AnnotationCombineService annotationCombineService;
  @Autowired private AnnotationCombineRepository annotationCombineRepository;

  @Test(enabled = false)
  public void testAnnotationCombine() {
    ListAnnotationCombineRequest annotationCombineQuery = new ListAnnotationCombineRequest();
    annotationCombineQuery.setPageIndex(1);
    annotationCombineQuery.setPageSize(10);
    annotationCombineQuery.setAnnotationTypes(Arrays.asList(0, 1));
    annotationCombineQuery.setStates(Arrays.asList("a"));

    Page<AnnotationCombine> page =
        annotationCombineService.listAnnotationCombine(annotationCombineQuery);
    log.info(">>>>>>>>data:" + JSON.toJSONString(page));
  }

  @Test(enabled = false)
  public void testGroup() {
    List<AnnotationSummary> annotationSummaries = annotationCombineRepository.findByStateGroup();
    for (AnnotationSummary current : annotationSummaries) {
      log.info(">>>>>>>>state:" + current.getState() + ">>>>>>num:" + current.getNum());
    }
  }

  @Test(enabled = false)
  public void testAtomicTerm() {
    List<AtomicTerm> atomicTermList = new ArrayList<>();
    AtomicTerm atomicTerm = new AtomicTerm("a", "body-structure", 1);
    atomicTermList.add(atomicTerm);
    atomicTerm = new AtomicTerm("a", "body-structure", 2);
    atomicTermList.add(atomicTerm);
    atomicTerm = new AtomicTerm("b", "Error", 3);
    atomicTermList.add(atomicTerm);
    log.info("result：" + atomicTermList.stream().distinct().collect(Collectors.toList()));
  }
}
