package com.malgo.biz;

import com.malgo.dao.UserAccountRepository;
import com.malgo.entity.UserAccount;
import com.malgo.exception.BusinessRuleException;
import com.malgo.exception.InternalServiceException;
import com.malgo.exception.InvalidInputException;
import com.malgo.request.AddUserAccountRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by cjl on 2018/5/30.
 */
@Component
@Slf4j
public class AddUserAccountBiz extends BaseBiz<AddUserAccountRequest, UserAccount> {

  private final UserAccountRepository userAccountRepository;

  @Autowired
  public AddUserAccountBiz(UserAccountRepository userAccountRepository) {
    this.userAccountRepository = userAccountRepository;
  }

  @Override
  protected void validateRequest(AddUserAccountRequest addUserAccountRequest)
      throws InvalidInputException {
    if (StringUtils.isBlank(addUserAccountRequest.getAccountName())) {
      throw new InvalidInputException("invalid-accountName", "用户账号不能为空");
    }
    if (StringUtils.isBlank(addUserAccountRequest.getPassword())) {
      throw new InvalidInputException("invalid-password", "用户密码不能为空");
    }
    if (StringUtils.isBlank(addUserAccountRequest.getRole())) {
      throw new InvalidInputException("invalid-role", "用户角色不能为空");
    }
    if(addUserAccountRequest.getRoleId()==1){
      throw new InvalidInputException("invalid-role", "用户角色不能为管理员");
    }
    if ("管理员".equals(addUserAccountRequest.getRole())) {
      throw new InvalidInputException("invalid-role", "用户角色不能为管理员");
    }
  }

  @Override
  protected void authorize(int userId, int role, AddUserAccountRequest addUserAccountRequest)
      throws BusinessRuleException {

  }

  @Override
  protected UserAccount doBiz(AddUserAccountRequest addUserAccountRequest) {
    if (userAccountRepository.findByAccountName(addUserAccountRequest.getAccountName()) != null) {
      throw new BusinessRuleException("repetition accountName", "用户账号重复");
    }
    UserAccount userAccount = new UserAccount();
    userAccount.setAccountName(addUserAccountRequest.getAccountName());
    userAccount.setPassword(addUserAccountRequest.getPassword());
    userAccount.setRoleId(addUserAccountRequest.getRoleId());
    userAccount.setRole(addUserAccountRequest.getRole());
    userAccount.setState("enable");
    try {
      userAccount = userAccountRepository.save(userAccount);
    } catch (Exception ex) {
      log.error(ex.getMessage());
      throw new InternalServiceException("dao layer error", "新增用户失败", ex.getCause());
    }
    return userAccount;
  }
}
