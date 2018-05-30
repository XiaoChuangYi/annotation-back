package com.microservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.microservice.dataAccessLayer.entity.UserAccount;
import com.microservice.result.ResultVO;
import com.microservice.service.UserAccountService;
import com.microservice.utils.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by cjl on 2018/4/16.
 */
@RestController
@RequestMapping(value = "/user")
public class UserAccountController {

  @Autowired
  private UserAccountService userAccountService;

  /**
   * 查询所有用户信息
   */
  @RequestMapping(value = "/queryUserAccount.do")
  public ResultVO<List<UserAccount>> queryUserAccount() {
    List<UserAccount> userAccountList = userAccountService.listUserAccount();
    return ResultVO.success(userAccountList);
  }

  @RequestMapping(value = "/anonymous/test.do")
  public String test() {
    return "get success!";
  }

  /**
   * 用户登录
   * 备注：@RequestBody，可以正常解析请求数据
   */
  @RequestMapping(value = "/anonymous/login.do")
  public ResultVO<UserAccount> login(@RequestBody JSONObject jsonParam, HttpServletRequest request,
      HttpServletResponse response) {
    String accountName = jsonParam.getString("accountName");
    String password = jsonParam.getString("password");
    if (StringUtils.isBlank(accountName)) {
      return ResultVO.error("请输入账户！");
    }
    if (StringUtils.isBlank(password)) {
      return ResultVO.error("请输入密码！");
    }

    UserAccount userAccountOld = userAccountService.getUserAccountByAccountName(accountName);
    if (userAccountOld == null) {
      return ResultVO.error("账户不存在！");
    }

    if (!MD5Util.checkPassword(password, userAccountOld.getPassword())) {
      return ResultVO.error("密码输入错误！");
    }

//        if(!password.equals(userAccountOld.getPassword()))
//            return ResultVO.error("密码输入错误！");

    if (userAccountOld.getState().equals("disable")) {
      return ResultVO.error("当前用户被冻结，请联系管理员！");
    }

    //添加session
    HttpSession httpSession = request.getSession();
    httpSession.setAttribute("userAccount", userAccountOld);
    httpSession.setMaxInactiveInterval(0);

    Cookie cookie = new Cookie("userId", userAccountOld.getId() + userAccountOld.getAccountName());
    cookie.setMaxAge(1 * 60 * 60);
    cookie.setPath("/");
    response.addCookie(cookie);

    return ResultVO.success(userAccountOld);
  }

  /**
   * 新增用户，新增功能，如果实指派给练习员，则默认新增所有的预标注标准集给特定的用户
   */
  @RequestMapping(value = "/addUserAccount.do")
  public ResultVO addUserAccount(@RequestBody JSONObject jsonParam) {
    String accountName = jsonParam.getString("accountName");
    String password = jsonParam.getString("password");
    String role = jsonParam.getString("role");
    userAccountService.addUserAccount(accountName, password, role);
    return ResultVO.success("成功添加新用户！");
  }

  /**
   * 修改用户密码，或者直接重置密码
   */
  @RequestMapping(value = "/updateUserAccountPassword.do")
  public ResultVO updateUserAccountPassword(@RequestBody JSONObject jsonParam) {
    String newPassword = jsonParam.getString("newPassword");
    int userId = jsonParam.getIntValue("userId");
    userAccountService.resetUserAccountPassword(newPassword, userId);
    return ResultVO.success("成功修改用户密码！");
  }

  /**
   * 设定用户的状态
   */
  @RequestMapping(value = "/setUserAccountState.do")
  public ResultVO setUserAccountState(@RequestBody JSONObject jsonParam) {
    String currentState = jsonParam.getString("currentState");
    int userId = jsonParam.getIntValue("userId");
    userAccountService.setUserAccountState(currentState, userId);
    return ResultVO.success("成功修改用户状态！");
  }

}
