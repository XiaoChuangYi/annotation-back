package cn.malgo.annotation.dao;

import cn.malgo.annotation.dto.UserDetails;
import cn.malgo.annotation.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserAccountRepository
    extends JpaRepository<UserAccount, Integer>, JpaSpecificationExecutor {
  UserAccount findByAccountName(String accountName);

  UserDetails getByIdEquals(int id);
}
