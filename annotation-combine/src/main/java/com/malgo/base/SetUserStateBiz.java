package com.malgo.base;

import com.malgo.dao.UserAccountRepository;
import com.malgo.entity.UserAccount;
import com.malgo.exception.BusinessRuleException;
import com.malgo.exception.InvalidInputException;
import com.malgo.request.SetUserStateRequest;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by cjl on 2018/5/30.
 */
@Component
public class SetUserStateBiz extends BaseBiz<SetUserStateRequest, UserAccount> {


  private final UserAccountRepository userAccountRepository;

  @Autowired
  public SetUserStateBiz(UserAccountRepository userAccountRepository){
    this.userAccountRepository=userAccountRepository;
  }

  @Override
  protected void validateRequest(SetUserStateRequest setUserStateRequest)
      throws InvalidInputException {
    if (setUserStateRequest.getUserId() <= 0)
      throw new InvalidInputException("invalid-userId", "无效的userId");

    if(StringUtils.isBlank(setUserStateRequest.getCurrentState()))
      throw new InvalidInputException("invalid-currentState","currentState参数为空");
  }

  @Override
  protected void authorize(String authToken, SetUserStateRequest setUserStateRequest)
      throws BusinessRuleException {

  }

  @Override
  protected UserAccount doBiz(SetUserStateRequest setUserStateRequest) {
    Optional<UserAccount> optional=userAccountRepository.findById(setUserStateRequest.getUserId());
    if(optional.isPresent()){
      optional.get().setState(setUserStateRequest.getCurrentState());
      return userAccountRepository.save(optional.get());
    }else {
      return optional.get();
    }
  }
}
