package com.malgo.controller;

import com.malgo.base.ListUserAccountBiz;
import com.malgo.request.ListUserAccountRequest;
import com.malgo.result.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by cjl on 2018/5/24.
 */
@RestController
@RequestMapping(value = "/api/v2")
@Slf4j
public class UserAccountController {


  private final ListUserAccountBiz listUserAccountBiz;


  public UserAccountController(ListUserAccountBiz listUserAccountBiz){
    this.listUserAccountBiz=listUserAccountBiz;
  }
  /**
   * 用户列表查询
   */
  @RequestMapping(value = "/list-user-account",method = RequestMethod.GET)
  public Response listUserAccount(ListUserAccountRequest listUserAccountRequest){
    return new Response(listUserAccountBiz.process(listUserAccountRequest,null));
  }
  /**
   *  新增用户
   */
  @RequestMapping(value = "/add-user-account",method = RequestMethod.POST)
  public Response addUserAccount(){
    return new Response("");
  }
  /**
   * 密码更新或者重置
   */
  @RequestMapping(value = "/modify-user-password",method = RequestMethod.GET)
  public Response modifyUserPassword(){
    return new Response("");
  }
  /**
   * 设定用户状态(启用/冻结)
   */
  @RequestMapping(value = "/set-user-state",method = RequestMethod.POST)
  public Response setUserState(){
    return new Response("");
  }
}
