package cn.malgo.annotation.biz;

import cn.malgo.annotation.entity.UserAccount;
import cn.malgo.annotation.exception.BusinessRuleException;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.ListUserAccountRequest;
import cn.malgo.annotation.result.PageVO;
import cn.malgo.annotation.service.UserAccountService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

/** Created by cjl on 2018/5/30. */
@Component
public class ListUserAccountBiz extends BaseBiz<ListUserAccountRequest, PageVO<UserAccount>> {

  private final UserAccountService userAccountService;

  @Autowired
  public ListUserAccountBiz(UserAccountService userAccountService) {
    this.userAccountService = userAccountService;
  }

  @Override
  protected void validateRequest(ListUserAccountRequest listUserAccountRequest)
      throws InvalidInputException {
    if (!listUserAccountRequest.isAll()) {
      if (listUserAccountRequest.getPageIndex() < 1) {
        throw new InvalidInputException("invalid-page-index", "pageIndex应该大于等于1");
      }
      if (listUserAccountRequest.getPageSize() <= 0) {
        throw new InvalidInputException("invalid-page-size", "pageSize应该大于等于1");
      }
    }
  }

  @Override
  protected void authorize(int userId, int role, ListUserAccountRequest listUserAccountRequest)
      throws BusinessRuleException {}

  @Override
  protected PageVO<UserAccount> doBiz(ListUserAccountRequest listUserAccountRequest) {
    PageVO pageVO = new PageVO();
    if (listUserAccountRequest.isAll()) {
      List<UserAccount> userAccountList = userAccountService.listUserAccount();
      pageVO.setTotal(userAccountList.size());
      pageVO.setDataList(userAccountList);
    } else {
      Page page =
          userAccountService.listUserAccountPaging(
              listUserAccountRequest.getPageIndex() - 1, listUserAccountRequest.getPageSize());
      pageVO.setTotal(page.getTotalElements());
      pageVO.setDataList(page.getContent());
    }
    return pageVO;
  }
}
