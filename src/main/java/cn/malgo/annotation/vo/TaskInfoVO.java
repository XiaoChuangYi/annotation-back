package cn.malgo.annotation.vo;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskInfoVO {
  private String taskName;
  private int taskAnnotatedWordNum;
  private BigDecimal taskAvailableMaximumPayment;
  private long taskParticipationStaffNum;
  private BigDecimal predictAverageHighestPayment;
  private int currentTaskRestNum;
}
