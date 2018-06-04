package com.malgo.service;


import com.malgo.entity.UserAccount;
import com.malgo.request.LogOutRequest;
import com.malgo.request.LoginRequest;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;

/**
 * Created by cjl on 2018/5/24.
 */
public interface UserAccountService {

  /**
   * 分页查询用户
   */
  Page<UserAccount> listUserAccountPaging(int pageIndex,int pageSize);

  /**
   * 直接查询所有用户
   */
  List<UserAccount> listUserAccount();

  UserAccount login(LoginRequest loginRequest,HttpServletRequest servletRequest,HttpServletResponse servletResponse);

  UserAccount logOut(LogOutRequest logOutRequest,HttpServletRequest servletRequest,HttpServletResponse servletResponse);

}
