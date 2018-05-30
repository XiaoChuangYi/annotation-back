package com.malgo.service;


import com.malgo.entity.UserAccount;
import java.util.List;
import org.springframework.data.domain.Page;

/**
 * Created by cjl on 2018/5/24.
 */
public interface UserAccountService {

  /**
   * 分页查询用户
   */
  Page<UserAccount> listUserAccountPaging(int pageIndex,int pageSize);

  /**
   * 直接查询所有用户
   */
  List<UserAccount> listUserAccount();

}
