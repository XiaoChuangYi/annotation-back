// package cn.malgo.annotation.interceptor;
//
// import cn.malgo.annotation.dao.UserAccountRepository;
// import cn.malgo.annotation.entity.UserAccount;
// import cn.malgo.annotation.service.impl.UserAccountServiceImpl;
// import cn.malgo.service.model.UserDetails;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.web.servlet.HandlerInterceptor;
//
// import javax.persistence.EntityNotFoundException;
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;
// import javax.servlet.http.HttpSession;
//
// @Slf4j
// public class DefaultUserInterceptor implements HandlerInterceptor {
//  private final long defaultUserId;
//  private final UserAccountRepository userAccountRepository;
//
//  public DefaultUserInterceptor(
//      final long defaultUserId, final UserAccountRepository userAccountRepository) {
//    this.defaultUserId = defaultUserId;
//    this.userAccountRepository = userAccountRepository;
//  }
//
//  @Override
//  public boolean preHandle(
//      HttpServletRequest request, HttpServletResponse response, Object handler) {
//    final HttpSession session = request.getSession();
//    final UserDetails account = (UserDetails) session.getAttribute("userAccount");
//    if (account == null) {
//      final UserDetails userAccount = getUserAccount();
//      if (userAccount != null) {
//        log.info("using default user: {}", userAccount);
//        session.setAttribute("userAccount", userAccount);
//        session.setMaxInactiveInterval(0);
//      }
//    }
//
//    return true;
//  }
//
//  private UserDetails getUserAccount() {
//    if (defaultUserId != 0) {
//      try {
//        final UserAccount user = userAccountRepository.getById(defaultUserId);
//        return new UserAccountServiceImpl.DefaultUserDetails(user.getId(), user.getRoleId());
//      } catch (EntityNotFoundException ex) {
//        log.warn("wrong user id configured: {}", defaultUserId);
//      }
//    }
//
//    return null;
//  }
// }
