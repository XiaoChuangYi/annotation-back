package cn.malgo.annotation.vo;

import cn.malgo.annotation.dto.User;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {

  private List<User> users;
}
