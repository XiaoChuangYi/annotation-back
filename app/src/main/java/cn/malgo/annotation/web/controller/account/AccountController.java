package cn.malgo.annotation.web.controller.account;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.malgo.annotation.common.dal.model.CrmAccount;
import cn.malgo.annotation.common.util.AssertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.malgo.annotation.core.service.account.AccountService;
import cn.malgo.annotation.web.result.ResultVO;

/**
 *
 * @author 张钟
 * @date 2017/10/23
 */
@RestController
@RequestMapping(value = "/auth")
public class AccountController {

    @Autowired
    private AccountService accountService;

    /**
     * 用户登录
     * @param accountNo
     * @param loginPwd
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/anonymous/login.do")
    public ResultVO login(String accountNo, String loginPwd, HttpServletRequest request,HttpServletResponse response){
        AssertUtil.notBlank(accountNo,"用户账号为空");
        AssertUtil.notBlank(loginPwd,"登录密码为空");

        CrmAccount crmAccount = accountService.checkPwd(accountNo,loginPwd);
        HttpSession httpSession = request.getSession();
        httpSession.setAttribute("currentAccount",crmAccount);

        Cookie cookie = new Cookie("userId",crmAccount.getId());//创建新cookie
        cookie.setMaxAge(1*60 * 60);// 设置存在时间为5分钟
        cookie.setPath("/");//设置作用域
        response.addCookie(cookie);//将cookie添加到response的cookie数组中返回给客户端

        return ResultVO.success();
    }

}
