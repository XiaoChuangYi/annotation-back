package com.malgo.dao;

import com.malgo.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/** Created by cjl on 2018/5/30. */
public interface UserAccountRepository
    extends JpaRepository<UserAccount, Integer>, JpaSpecificationExecutor {

  UserAccount findByAccountName(String accountName);
}
