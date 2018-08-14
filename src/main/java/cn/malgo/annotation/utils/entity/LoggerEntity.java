package cn.malgo.annotation.utils.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoggerEntity {
  private long userId;
  private String action;
  private String result;
  private String time;
  private long id;
}
