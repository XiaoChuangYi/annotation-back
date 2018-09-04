// package cn.malgo.annotation.biz;
//
// import cn.malgo.annotation.dao.UserAccountRepository;
// import cn.malgo.annotation.entity.UserAccount;
// import cn.malgo.annotation.request.SetUserStateRequest;
// import cn.malgo.service.biz.BaseBiz;
// import cn.malgo.service.exception.InvalidInputException;
// import cn.malgo.service.exception.NotFoundException;
// import org.apache.commons.lang3.StringUtils;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Component;
//
// import java.util.Optional;
//
// @Component
// public class SetUserStateBiz extends BaseBiz<SetUserStateRequest, UserAccount> {
//  private final UserAccountRepository userAccountRepository;
//
//  @Autowired
//  public SetUserStateBiz(UserAccountRepository userAccountRepository) {
//    this.userAccountRepository = userAccountRepository;
//  }
//
//  @Override
//  protected void validateRequest(SetUserStateRequest request) throws InvalidInputException {
//    if (request.getUserId() <= 0) {
//      throw new InvalidInputException("invalid-user-id", "无效的userId");
//    }
//
//    if (StringUtils.isBlank(request.getCurrentState())) {
//      throw new InvalidInputException("invalid-current-state", "currentState参数为空");
//    }
//  }
//
//  @Override
//  protected UserAccount doBiz(SetUserStateRequest request) {
//    final Optional<UserAccount> optional = userAccountRepository.findById(request.getUserId());
//
//    if (optional.isPresent()) {
//      final UserAccount userAccount = optional.get();
//      userAccount.setState(request.getCurrentState());
//      return userAccountRepository.save(userAccount);
//    }
//
//    throw new NotFoundException("user-not-found", request.getUserId() + "不存在");
//  }
// }
