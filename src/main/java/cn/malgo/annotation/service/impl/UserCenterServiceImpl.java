package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dto.User;
import cn.malgo.annotation.service.AuthService;
import cn.malgo.annotation.service.UserCenterService;
import cn.malgo.annotation.service.feigns.UserCenterClient;
import cn.malgo.annotation.vo.UserInfo;
import cn.malgo.common.auth.AuthConstants;
import cn.malgo.common.auth.RedisConfigService;
import cn.malgo.service.exception.BusinessRuleException;
import com.alibaba.fastjson.JSONObject;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UserCenterServiceImpl implements UserCenterService {

  private final AuthService authService;
  private final UserCenterClient userCenterClient;
  private final RedisConfigService redisConfigService;

  public UserCenterServiceImpl(
      final AuthService authService,
      final UserCenterClient userCenterClient,
      final RedisConfigService redisConfigService) {
    this.authService = authService;
    this.userCenterClient = userCenterClient;
    this.redisConfigService = redisConfigService;
  }

  @Override
  public List<User> getUsersByUserCenter() {
    if (authService.login()) {
      return userCenterClient.getUsers().getUsers();
    } else {
      throw new BusinessRuleException("", "用户中心登陆失败，无法获取用户信息！");
    }
  }
}
