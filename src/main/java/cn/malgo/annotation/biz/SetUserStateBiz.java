package cn.malgo.annotation.biz;

import cn.malgo.annotation.dao.UserAccountRepository;
import cn.malgo.annotation.entity.UserAccount;
import cn.malgo.annotation.exception.BusinessRuleException;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.SetUserStateRequest;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Created by cjl on 2018/5/30. */
@Component
public class SetUserStateBiz extends BaseBiz<SetUserStateRequest, UserAccount> {

  private final UserAccountRepository userAccountRepository;

  @Autowired
  public SetUserStateBiz(UserAccountRepository userAccountRepository) {
    this.userAccountRepository = userAccountRepository;
  }

  @Override
  protected void validateRequest(SetUserStateRequest setUserStateRequest)
      throws InvalidInputException {
    if (setUserStateRequest.getUserId() <= 0) {
      throw new InvalidInputException("invalid-user-id", "无效的userId");
    }

    if (StringUtils.isBlank(setUserStateRequest.getCurrentState())) {
      throw new InvalidInputException("invalid-current-state", "currentState参数为空");
    }
  }

  @Override
  protected void authorize(int userId, int role, SetUserStateRequest setUserStateRequest)
      throws BusinessRuleException {}

  @Override
  protected UserAccount doBiz(SetUserStateRequest setUserStateRequest) {
    Optional<UserAccount> optional =
        userAccountRepository.findById(setUserStateRequest.getUserId());
    if (optional.isPresent()) {
      optional.get().setState(setUserStateRequest.getCurrentState());
      return userAccountRepository.save(optional.get());
    } else {
      return optional.get();
    }
  }
}
