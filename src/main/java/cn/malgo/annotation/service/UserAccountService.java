package cn.malgo.annotation.service;

import cn.malgo.annotation.entity.UserAccount;
import cn.malgo.annotation.request.LogOutRequest;
import cn.malgo.annotation.request.LoginRequest;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;

public interface UserAccountService {
  //  /** 分页查询用户 */
  //  Page<UserAccount> listUserAccountPaging(int pageIndex, int pageSize);
  //
  //  /** 直接查询所有用户 */
  //  List<UserAccount> listUserAccount();
  //
  //  UserAccount login(
  //      LoginRequest loginRequest,
  //      HttpServletRequest servletRequest,
  //      HttpServletResponse servletResponse);
  //
  //  UserAccount logOut(
  //      LogOutRequest logOutRequest,
  //      HttpServletRequest servletRequest,
  //      HttpServletResponse servletResponse);
  //
  //  UserAccount loginRefresh(HttpServletRequest servletRequest, HttpServletResponse
  // servletResponse);
}
