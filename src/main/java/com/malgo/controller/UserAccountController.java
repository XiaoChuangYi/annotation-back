package com.malgo.controller;

import com.malgo.biz.AddUserAccountBiz;
import com.malgo.biz.ListUserAccountBiz;
import com.malgo.biz.ModifyUserPasswordBiz;
import com.malgo.biz.SetUserStateBiz;
import com.malgo.request.AddUserAccountRequest;
import com.malgo.request.ListUserAccountRequest;
import com.malgo.request.LogOutRequest;
import com.malgo.request.LoginRequest;
import com.malgo.request.ModifyPasswordRequest;
import com.malgo.request.SetUserStateRequest;
import com.malgo.result.Response;
import com.malgo.service.UserAccountService;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by cjl on 2018/5/24.
 */
@RestController
@RequestMapping(value = "/api/v2",produces = {"application/json;charset=UTF-8"})
@Slf4j
public class UserAccountController {


  private final ListUserAccountBiz listUserAccountBiz;
  private final AddUserAccountBiz addUserAccountBiz;
  private final UserAccountService userAccountService;
  private final ModifyUserPasswordBiz modifyUserPasswordBiz;
  private final SetUserStateBiz setUserStateBiz;

  public UserAccountController(ListUserAccountBiz listUserAccountBiz,
      AddUserAccountBiz addUserAccountBiz,
      UserAccountService userAccountService,
      ModifyUserPasswordBiz modifyUserPasswordBiz,
      SetUserStateBiz setUserStateBiz
      ){
    this.listUserAccountBiz=listUserAccountBiz;
    this.addUserAccountBiz=addUserAccountBiz;
    this.userAccountService=userAccountService;
    this.modifyUserPasswordBiz=modifyUserPasswordBiz;
    this.setUserStateBiz=setUserStateBiz;
  }

  /**
   * 用户登录
   */
  @RequestMapping(value = "/user/login",method = RequestMethod.POST)
  public Response login(@RequestBody LoginRequest loginRequest,HttpServletRequest servletRequest,HttpServletResponse servletResponse){
    return new Response(userAccountService.login(loginRequest,servletRequest,servletResponse));
  }

  /**
   * 用户登出
   */
  @RequestMapping(value = "log-out",method = RequestMethod.POST)
  public Response logout(@RequestBody LogOutRequest logOutRequest,HttpServletRequest servletRequest,HttpServletResponse servletResponse){
    return new Response(userAccountService.logOut(logOutRequest,servletRequest,servletResponse));
  }

  /**
   * 用户列表查询
   */
  @RequestMapping(value = "/list-user-account",method = RequestMethod.GET)
  public Response listUserAccount(ListUserAccountRequest listUserAccountRequest){
    return new Response(listUserAccountBiz.process(listUserAccountRequest,0,0));
  }
  /**
   *  新增用户
   */
  @RequestMapping(value = "/add-user-account",method = RequestMethod.POST)
  public Response addUserAccount(@RequestBody AddUserAccountRequest addUserAccountRequest){
    return new Response(addUserAccountBiz.process(addUserAccountRequest,0,0));
  }
  /**
   * 密码更新或者重置
   */
  @RequestMapping(value = "/modify-user-password",method = RequestMethod.POST)
  public Response modifyUserPassword(@RequestBody ModifyPasswordRequest modifyPasswordRequest){
    return new Response(modifyUserPasswordBiz.process(modifyPasswordRequest,0,0));
  }
  /**
   * 设定用户状态(启用/冻结)
   */
  @RequestMapping(value = "/set-user-state",method = RequestMethod.POST)
  public Response setUserState(@RequestBody SetUserStateRequest setUserStateRequest){
    return new Response(setUserStateBiz.process(setUserStateRequest,0,0));
  }
}
