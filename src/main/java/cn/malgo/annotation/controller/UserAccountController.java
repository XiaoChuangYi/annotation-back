// package cn.malgo.annotation.controller;
//
// import cn.malgo.annotation.biz.AddUserAccountBiz;
// import cn.malgo.annotation.biz.ListUserAccountBiz;
// import cn.malgo.annotation.biz.ModifyUserPasswordBiz;
// import cn.malgo.annotation.biz.SetUserStateBiz;
// import cn.malgo.annotation.request.AddUserAccountRequest;
// import cn.malgo.annotation.request.ListUserAccountRequest;
// import cn.malgo.annotation.request.ModifyPasswordRequest;
// import cn.malgo.annotation.request.SetUserStateRequest;
// import cn.malgo.service.model.Response;
// import cn.malgo.service.model.UserDetails;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.web.bind.annotation.ModelAttribute;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestMethod;
// import org.springframework.web.bind.annotation.RestController;
//
// @RestController
// @RequestMapping(value = "/api/v2")
// @Slf4j
// public class UserAccountController extends BaseController {
//
//  private final ListUserAccountBiz listUserAccountBiz;
//  private final AddUserAccountBiz addUserAccountBiz;
//  private final ModifyUserPasswordBiz modifyUserPasswordBiz;
//  private final SetUserStateBiz setUserStateBiz;
//
//  public UserAccountController(
//      final ListUserAccountBiz listUserAccountBiz,
//      final AddUserAccountBiz addUserAccountBiz,
//      final ModifyUserPasswordBiz modifyUserPasswordBiz,
//      final SetUserStateBiz setUserStateBiz) {
//    this.listUserAccountBiz = listUserAccountBiz;
//    this.addUserAccountBiz = addUserAccountBiz;
//    this.modifyUserPasswordBiz = modifyUserPasswordBiz;
//    this.setUserStateBiz = setUserStateBiz;
//  }
//
//  /** 用户列表查询 */
//  @RequestMapping(value = "/list-user-account", method = RequestMethod.GET)
//  public Response listUserAccount(
//      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount,
//      ListUserAccountRequest listUserAccountRequest) {
//    return new Response<>(listUserAccountBiz.process(listUserAccountRequest, userAccount));
//  }
//
//  /** 新增用户 */
//  @RequestMapping(value = "/add-user-account", method = RequestMethod.POST)
//  public Response addUserAccount(
//      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount,
//      @RequestBody AddUserAccountRequest addUserAccountRequest) {
//    return new Response<>(addUserAccountBiz.process(addUserAccountRequest, userAccount));
//  }
//
//  /** 密码更新或者重置 */
//  @RequestMapping(value = "/modify-user-password", method = RequestMethod.POST)
//  public Response modifyUserPassword(
//      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount,
//      @RequestBody ModifyPasswordRequest modifyPasswordRequest) {
//    return new Response<>(modifyUserPasswordBiz.process(modifyPasswordRequest, userAccount));
//  }
//
//  /** 设定用户状态(启用/冻结) */
//  @RequestMapping(value = "/set-user-state", method = RequestMethod.POST)
//  public Response setUserState(
//      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount,
//      @RequestBody SetUserStateRequest setUserStateRequest) {
//    return new Response<>(setUserStateBiz.process(setUserStateRequest, userAccount));
//  }
// }
