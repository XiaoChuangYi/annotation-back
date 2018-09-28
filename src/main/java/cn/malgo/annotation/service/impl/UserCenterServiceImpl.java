package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dto.User;
import cn.malgo.annotation.request.GetUsersRequest;
import cn.malgo.annotation.service.AuthService;
import cn.malgo.annotation.service.UserCenterService;
import cn.malgo.annotation.service.feigns.UserCenterClient;
import cn.malgo.annotation.vo.UserInfo;
import cn.malgo.service.exception.BusinessRuleException;
import java.util.List;
import lombok.Synchronized;
import org.springframework.stereotype.Service;

@Service
public class UserCenterServiceImpl implements UserCenterService {

  private final AuthService authService;
  private final UserCenterClient userCenterClient;

  public UserCenterServiceImpl(
      final AuthService authService, final UserCenterClient userCenterClient) {
    this.authService = authService;
    this.userCenterClient = userCenterClient;
  }

  @Override
  @Synchronized
  public List<User> getUsersByUserCenter() {
    if (authService.login()) {
      final UserInfo userInfo = userCenterClient.getUsers(new GetUsersRequest("annotation"));
      if (userInfo == null || userInfo.getUsers() == null) {
        throw new BusinessRuleException("", "无法获取用户中心用户信息，请刷新！");
      }
      return userInfo.getUsers();
    } else {
      throw new BusinessRuleException("", "用户中心登陆失败，无法获取用户信息！");
    }
  }
}
