// package cn.malgo.annotation.biz;
//
// import cn.malgo.annotation.entity.UserAccount;
// import cn.malgo.annotation.request.ListUserAccountRequest;
// import cn.malgo.annotation.result.PageVO;
// import cn.malgo.annotation.service.UserAccountService;
// import cn.malgo.service.biz.BaseBiz;
// import cn.malgo.service.exception.InvalidInputException;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Component;
//
// import java.util.List;
//
// @Component
// public class ListUserAccountBiz extends BaseBiz<ListUserAccountRequest, PageVO<UserAccount>> {
//  private final UserAccountService userAccountService;
//
//  @Autowired
//  public ListUserAccountBiz(UserAccountService userAccountService) {
//    this.userAccountService = userAccountService;
//  }
//
//  @Override
//  protected void validateRequest(ListUserAccountRequest listUserAccountRequest)
//      throws InvalidInputException {
//    if (!listUserAccountRequest.isAll()) {
//      if (listUserAccountRequest.getPageIndex() < 1) {
//        throw new InvalidInputException("invalid-page-index", "pageIndex应该大于等于1");
//      }
//
//      if (listUserAccountRequest.getPageSize() <= 0) {
//        throw new InvalidInputException("invalid-page-size", "pageSize应该大于等于1");
//      }
//    }
//  }
//
//  @Override
//  protected PageVO<UserAccount> doBiz(ListUserAccountRequest request) {
//    if (request.isAll()) {
//      final List<UserAccount> userAccountList = userAccountService.listUserAccount();
//      return new PageVO<>(userAccountList.size(), userAccountList);
//    }
//
//    return new PageVO<>(
//        userAccountService.listUserAccountPaging(
//            request.getPageIndex() - 1, request.getPageSize()));
//  }
// }
