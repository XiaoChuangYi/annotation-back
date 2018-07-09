package cn.malgo.annotation.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
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

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserAccount {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private String accountName;
  private String password;
  private String role;
  private String state;
  private int roleId;

  @CreatedDate
  @Column(name = "gmt_created", updatable = false, nullable = false)
  @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
  private Date gmtCreated;

  @LastModifiedDate
  @Column(name = "gmt_modified", nullable = false)
  @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
  private Date gmtModified;
}
