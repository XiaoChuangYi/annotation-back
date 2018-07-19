package cn.malgo.annotation.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetUserStateRequest {
  private long userId;
  private String currentState;
}
