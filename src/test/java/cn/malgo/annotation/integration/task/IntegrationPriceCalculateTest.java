package cn.malgo.annotation.integration.task;

import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.enums.AnnotationStateEnum;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.service.OutsourcingPriceCalculateService;
import java.math.BigDecimal;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/** 标注价格合计计算办法 */
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class IntegrationPriceCalculateTest extends AbstractTransactionalTestNGSpringContextTests {

  @Autowired private OutsourcingPriceCalculateService outsourcingPriceCalculateService;

  @Test
  public void testPersonalPayment() {
    final String testTemp = "这是第一句话，这是第二句话，这是第三句话而且足够长足够长足够长足够长足够长";
    StringBuilder sb = new StringBuilder();
    while (sb.length() < 30000) {
      sb.append(testTemp);
    }
    final BigDecimal result =
        outsourcingPriceCalculateService.testTaskPersonalPayment(
            new AnnotationNew(
                "",
                AnnotationStateEnum.CLEANED,
                2L,
                AnnotationTypeEnum.relation,
                "",
                "",
                20235L,
                4L,
                "",
                new Date(),
                new Date(),
                0.9d,
                0.8d,
                0L,
                ""),
            sb.toString());
    assertEquals(result, BigDecimal.valueOf(640.16));
  }
}
