package cn.malgo.annotation.service.impl;

import static org.testng.Assert.assertEquals;

import cn.malgo.annotation.dao.AtomicTermRepository;
import cn.malgo.annotation.entity.AtomicTerm;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.core.definition.Entity;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.mockito.Mockito;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class AtomicTermSegmentServiceTest {
  @DataProvider(name = "segment-data")
  public Object[][] getData() {
    return new Object[][] {
      new Object[] {
        "感染性髋关节炎", new String[] {"感染性", "髋关节", "炎"},
      },
    };
  }

  @Test(dataProvider = "segment-data")
  public void testSegment(String text, String[] words) {
    final AtomicTermRepository repository = Mockito.mock(AtomicTermRepository.class);
    Mockito.when(repository.findAll())
        .thenReturn(
            Arrays.stream(words)
                .map(word -> new AtomicTerm(word, "an-type", 0L, AnnotationTypeEnum.disease))
                .collect(Collectors.toList()));
    final AtomicTermSegmentServiceImpl service = new AtomicTermSegmentServiceImpl(repository);
    service.init();
    final List<Entity> entities = service.seg(AnnotationTypeEnum.disease, text);
    entities.sort(Comparator.comparingInt(Entity::getStart));
    assertEquals(entities.size(), words.length);
    for (int i = 0; i < words.length; ++i) {
      assertEquals(entities.get(i).getTerm(), words[i]);
    }
  }
}
