package cn.malgo.annotation.entity;

import cn.malgo.service.entity.BaseEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
    name = "user_account",
    uniqueConstraints = {@UniqueConstraint(columnNames = "accountName")})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class UserAccount extends BaseEntity {
  private String accountName;
  private String password;
  private int roleId;
  private String role;
  private String state;
}
