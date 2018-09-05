package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.constants.Permissions;
// import cn.malgo.annotation.dao.UserAccountRepository;
import cn.malgo.annotation.entity.UserAccount;
import cn.malgo.annotation.request.LogOutRequest;
import cn.malgo.annotation.request.LoginRequest;
import cn.malgo.annotation.service.UserAccountService;
import cn.malgo.service.exception.BusinessRuleException;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.List;

@Service
@Slf4j
public class UserAccountServiceImpl implements UserAccountService {

  //  private final UserAccountRepository userAccountRepository;
  //
  //  public UserAccountServiceImpl(UserAccountRepository userAccountRepository) {
  //    this.userAccountRepository = userAccountRepository;
  //  }
  //
  //  /** 分页查询用户 */
  //  @Override
  //  public Page<UserAccount> listUserAccountPaging(int pageIndex, int pageSize) {
  //    return userAccountRepository.findAll(PageRequest.of(pageIndex, pageSize));
  //  }
  //
  //  /** 直接查询所有用户 */
  //  @Override
  //  public List<UserAccount> listUserAccount() {
  //    return userAccountRepository.findAll();
  //  }
  //
  //  @Override
  //  public UserAccount login(
  //      LoginRequest loginRequest,
  //      HttpServletRequest servletRequest,
  //      HttpServletResponse servletResponse) {
  //    if (StringUtils.isBlank(loginRequest.getAccountName())) {
  //      throw new InvalidInputException("invalid-accountName", "用户账号为空");
  //    }
  //
  //    if (StringUtils.isBlank(loginRequest.getPassword())) {
  //      throw new InvalidInputException("invalid-password", "用户密码为空");
  //    }
  //
  //    UserAccount param = userAccountRepository.findByAccountName(loginRequest.getAccountName());
  //
  //    if (param == null) {
  //      throw new BusinessRuleException("no-current-account", "当前账户不存在，请联系管理员");
  //    }
  //
  //    if (!param.getPassword().equals(loginRequest.getPassword())) {
  //      throw new BusinessRuleException("password-error", "密码输入错误");
  //    }
  //
  //    if ("disable".equals(param.getState())) {
  //      throw new BusinessRuleException("account-disabled", "当前账户被冻结，请联系管理员");
  //    }
  //    HttpSession session = servletRequest.getSession();
  //    final DefaultUserDetails defaultUserDetails =
  //        new DefaultUserDetails(param.getId(), param.getRoleId());
  //    session.setAttribute("userAccount", defaultUserDetails);
  //    session.setMaxInactiveInterval(Integer.MAX_VALUE);
  //    Cookie cookie = new Cookie("userId", param.getId() + param.getAccountName());
  //    cookie.setMaxAge(1 * 60 * 60);
  //    cookie.setPath("/");
  //    servletResponse.addCookie(cookie);
  //    return param;
  //  }
  //
  //  @Override
  //  public UserAccount logOut(
  //      LogOutRequest logOutRequest,
  //      HttpServletRequest servletRequest,
  //      HttpServletResponse servletResponse) {
  //    if (logOutRequest.getUserId() <= 0) {
  //      throw new InvalidInputException("invalid-user-id", "无效的用户id");
  //    }
  //
  //    final HttpSession httpSession = servletRequest.getSession();
  //    final UserDetails account = (UserDetails) httpSession.getAttribute("userAccount");
  //
  //    if (account != null && account.getId() == logOutRequest.getUserId()) {
  //      httpSession.invalidate();
  //    } else {
  //      throw new BusinessRuleException("current-user-not-login", "当前用户并未登录");
  //    }
  //
  //    return userAccountRepository.getOne(account.getId());
  //  }

  //  @Override
  //  public UserAccount loginRefresh(
  //      HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
  //    HttpSession httpSession = servletRequest.getSession();
  //    UserDetails account = (UserDetails) httpSession.getAttribute("userAccount");
  //    if (account != null) {
  //      return userAccountRepository.getOne(account.getId());
  //    }
  //    return null;
  //  }

  @Value
  public static class DefaultUserDetails implements Serializable, UserDetails {

    public static final UserDetails ADMIN = new DefaultUserDetails(0L, 1);

    private final long id;
    private final int roleId;

    @Override
    public boolean hasPermission(final String permission) {
      switch (permission) {
        case Permissions.ANNOTATE:
          return true;

        case Permissions.EXAMINE:
          return roleId <= 2;

        case Permissions.ADMIN:
          return roleId == 1;
      }

      return false;
    }
  }
}
