package cn.malgo.annotation.biz;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.UserAccountRepository;
import cn.malgo.annotation.entity.UserAccount;
import cn.malgo.annotation.request.ModifyPasswordRequest;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.exception.NotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequirePermission(Permissions.ADMIN)
public class ModifyUserPasswordBiz extends BaseBiz<ModifyPasswordRequest, UserAccount> {
  private final UserAccountRepository userAccountRepository;

  public ModifyUserPasswordBiz(UserAccountRepository userAccountRepository) {
    this.userAccountRepository = userAccountRepository;
  }

  @Override
  protected void validateRequest(ModifyPasswordRequest request) throws InvalidInputException {
    if (request.getUserId() <= 0) {
      throw new InvalidInputException("invalid-user-id", "无效的用户Id");
    }

    if (StringUtils.isBlank(request.getPassword())) {
      throw new InvalidInputException("invalid-password", "密码为空");
    }
  }

  @Override
  protected UserAccount doBiz(ModifyPasswordRequest request) {
    final Optional<UserAccount> optional = userAccountRepository.findById(request.getUserId());

    if (optional.isPresent()) {
      final UserAccount userAccount = optional.get();
      userAccount.setPassword(request.getPassword());
      return userAccountRepository.save(userAccount);
    }

    throw new NotFoundException("user-not-found", request.getUserId() + "不存在");
  }
}
