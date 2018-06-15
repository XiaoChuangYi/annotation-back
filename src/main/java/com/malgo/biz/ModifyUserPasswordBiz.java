package com.malgo.biz;

import com.malgo.dao.UserAccountRepository;
import com.malgo.entity.UserAccount;
import com.malgo.exception.BusinessRuleException;
import com.malgo.exception.InvalidInputException;
import com.malgo.request.ModifyPasswordRequest;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by cjl on 2018/5/30.
 */
@Component
public class ModifyUserPasswordBiz extends BaseBiz<ModifyPasswordRequest,UserAccount> {


  private final UserAccountRepository userAccountRepository;

  @Autowired
  public ModifyUserPasswordBiz(UserAccountRepository userAccountRepository){
    this.userAccountRepository=userAccountRepository;
  }

  @Override
  protected void validateRequest(ModifyPasswordRequest modifyPasswordRequest)
      throws InvalidInputException {
      if(modifyPasswordRequest.getUserId()<=0) {
        throw new InvalidInputException("invalid-user-id", "无效的用户Id");
      }
      if(StringUtils.isBlank(modifyPasswordRequest.getPassword())) {
        throw new InvalidInputException("invalid-password", "密码为空");
      }

  }

  @Override
  protected void authorize(int userId, int role, ModifyPasswordRequest modifyPasswordRequest)
      throws BusinessRuleException {

  }

  @Override
  protected UserAccount doBiz(ModifyPasswordRequest modifyPasswordRequest) {
    Optional<UserAccount> optional=userAccountRepository.findById(modifyPasswordRequest.getUserId());
    if(optional.isPresent()) {
      optional.get().setPassword(modifyPasswordRequest.getPassword());
      return userAccountRepository.save(optional.get());
    }else{
      return optional.get();
    }
  }
}
