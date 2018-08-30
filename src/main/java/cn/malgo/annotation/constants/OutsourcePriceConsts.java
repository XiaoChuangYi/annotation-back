package cn.malgo.annotation.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.Value;

public class OutsourcePriceConsts {
  public static final List<OutsourcePriceStage> PRICE_STAGES =
      Collections.unmodifiableList(
          Arrays.asList(
              OutsourcePriceStage.of(20000, 3),
              OutsourcePriceStage.of(30000, 4),
              OutsourcePriceStage.of(40000, 5),
              OutsourcePriceStage.of(Integer.MAX_VALUE, 6)));

  @Value(staticConstructor = "of")
  public static class OutsourcePriceStage {
    private int workNum;
    private double price;
  }
}
