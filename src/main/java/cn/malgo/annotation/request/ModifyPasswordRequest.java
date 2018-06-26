package cn.malgo.annotation.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Created by cjl on 2018/5/30. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModifyPasswordRequest {
  private int userId;
  private String password;
}
