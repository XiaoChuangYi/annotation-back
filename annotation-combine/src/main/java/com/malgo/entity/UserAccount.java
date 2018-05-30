package com.malgo.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * Created by cjl on 2018/5/30.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserAccount {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int id;
  private String accountName;
  private String password;
  private String role;
  private String state;

  @CreatedDate
  @Column(name = "gmt_created", updatable = false, nullable = false)
  private Date gmtCreated;

  @LastModifiedDate
  @Column(name = "gmt_modified", nullable = false)
  private Date gmtModified;
}
