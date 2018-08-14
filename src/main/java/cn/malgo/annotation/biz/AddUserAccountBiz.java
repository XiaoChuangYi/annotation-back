package cn.malgo.annotation.biz;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.UserAccountRepository;
import cn.malgo.annotation.entity.UserAccount;
import cn.malgo.annotation.request.AddUserAccountRequest;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InternalServerException;
import cn.malgo.service.exception.InvalidInputException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequirePermission(Permissions.ADMIN)
public class AddUserAccountBiz extends BaseBiz<AddUserAccountRequest, UserAccount> {
  private final UserAccountRepository userAccountRepository;

  public AddUserAccountBiz(UserAccountRepository userAccountRepository) {
    this.userAccountRepository = userAccountRepository;
  }

  @Override
  protected void validateRequest(AddUserAccountRequest addUserAccountRequest)
      throws InvalidInputException {
    if (StringUtils.isBlank(addUserAccountRequest.getAccountName())) {
      throw new InvalidInputException("invalid-account-name", "用户账号不能为空");
    }
    if (StringUtils.isBlank(addUserAccountRequest.getPassword())) {
      throw new InvalidInputException("invalid-password", "用户密码不能为空");
    }
    if (StringUtils.isBlank(addUserAccountRequest.getRole())) {
      throw new InvalidInputException("invalid-role", "用户角色不能为空");
    }
    if ("管理员".equals(addUserAccountRequest.getRole())) {
      throw new InvalidInputException("invalid-role", "用户角色不能为管理员");
    }
  }

  @Override
  protected UserAccount doBiz(AddUserAccountRequest request) {
    UserAccount userAccount =
        new UserAccount(
            request.getAccountName(),
            request.getPassword(),
            request.getRoleId(),
            request.getRole(),
            "enable");

    try {
      userAccount = userAccountRepository.save(userAccount);
    } catch (Exception ex) {
      log.error(ex.getMessage());
      throw new InternalServerException("新增用户失败", ex.getCause());
    }

    return userAccount;
  }
}
