package com.malgo.service.impl;

import com.malgo.dao.UserAccountRepository;
import com.malgo.entity.UserAccount;
import com.malgo.service.UserAccountService;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * Created by cjl on 2018/5/30.
 */
@Service
public class UserAccountServiceImpl implements UserAccountService {


  private final UserAccountRepository userAccountRepository;

  public UserAccountServiceImpl(UserAccountRepository userAccountRepository){
    this.userAccountRepository=userAccountRepository;
  }


  /**
   * 分页查询用户
   */
  @Override
  public Page<UserAccount> listUserAccountPaging(int pageIndex, int pageSize) {
    return userAccountRepository.findAll(PageRequest.of(pageIndex,pageSize));
  }

  /**
   * 直接查询所有用户
   */
  @Override
  public List<UserAccount> listUserAccount() {
    return userAccountRepository.findAll();
  }
}
