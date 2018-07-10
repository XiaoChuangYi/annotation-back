package cn.malgo.annotation.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class AnType {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private String id;

  private String typeName;
  private String state;
  private String typeCode;
  private String parentId;
  private int hasChildren;
  private int taskId;

  @CreatedDate
  @Column(name = "gmt_created", updatable = false, nullable = false)
  @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
  private Date gmtCreated;

  @LastModifiedDate
  @Column(name = "gmt_modified", nullable = false)
  @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
  private Date gmtModified;
}
