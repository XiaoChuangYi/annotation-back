package com.malgo.service.impl;

import com.malgo.dao.UserAccountRepository;
import com.malgo.entity.UserAccount;
import com.malgo.exception.BusinessRuleException;
import com.malgo.exception.InvalidInputException;
import com.malgo.request.LogOutRequest;
import com.malgo.request.LoginRequest;
import com.malgo.service.UserAccountService;
import java.util.List;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/** Created by cjl on 2018/5/30. */
@Service
public class UserAccountServiceImpl implements UserAccountService {

  private final UserAccountRepository userAccountRepository;

  public UserAccountServiceImpl(UserAccountRepository userAccountRepository) {
    this.userAccountRepository = userAccountRepository;
  }

  /** 分页查询用户 */
  @Override
  public Page<UserAccount> listUserAccountPaging(int pageIndex, int pageSize) {
    return userAccountRepository.findAll(PageRequest.of(pageIndex, pageSize));
  }

  /** 直接查询所有用户 */
  @Override
  public List<UserAccount> listUserAccount() {
    return userAccountRepository.findAll();
  }

  @Override
  public UserAccount login(
      LoginRequest loginRequest,
      HttpServletRequest servletRequest,
      HttpServletResponse servletResponse) {
    if (StringUtils.isBlank(loginRequest.getAccountName())) {
      throw new InvalidInputException("invalid-accountName", "用户账号为空");
    }
    if (StringUtils.isBlank(loginRequest.getPassword())) {
      throw new InvalidInputException("invalid-password", "用户密码为空");
    }

    UserAccount param =
        userAccountRepository.findByAccountNameAndPassword(
            loginRequest.getAccountName(), loginRequest.getPassword());
    if (param == null) {
      throw new BusinessRuleException("no-current-account", "当前账户不存在，请联系管理员");
    }
    if ("disable".equals(param.getState())) {
      throw new BusinessRuleException("account-disabled", "当前账户被冻结，请联系管理员");
    }
    HttpSession session = servletRequest.getSession();
    session.setAttribute("userAccount", param);
    session.setMaxInactiveInterval(0);
    Cookie cookie = new Cookie("userId", param.getId() + param.getAccountName());
    cookie.setMaxAge(1 * 60 * 60);
    cookie.setPath("/");
    servletResponse.addCookie(cookie);
    return param;
  }

  @Override
  public UserAccount logOut(
      LogOutRequest logOutRequest,
      HttpServletRequest servletRequest,
      HttpServletResponse servletResponse) {
    if (logOutRequest.getUserId() <= 0) {
      throw new InvalidInputException("invalid-user-id", "无效的用户id");
    }
    HttpSession httpSession = servletRequest.getSession();
    UserAccount account = (UserAccount) httpSession.getAttribute("userAccount");
    if (account != null && account.getId() == logOutRequest.getUserId()) {
      httpSession.invalidate();
    } else {
      throw new BusinessRuleException("current-user-not-login", "当前用户并未登录");
    }
    return account;
  }

  @Override
  public UserAccount loginRefresh(
      HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
    HttpSession httpSession = servletRequest.getSession();
    UserAccount account = (UserAccount) httpSession.getAttribute("userAccount");
    return account;
  }
}
