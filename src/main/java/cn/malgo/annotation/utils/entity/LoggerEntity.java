package cn.malgo.annotation.utils.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Created by cjl on 2018/6/6. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoggerEntity {

  private int userId;
  private int roleId;
  private String action;
  private String result;
  private String time;
  private int id;
}
