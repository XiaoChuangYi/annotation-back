package com.malgo.entity;

import com.alibaba.fastjson.annotation.JSONType;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/** Created by cjl on 2018/5/29. */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(of = "id")
@ToString
@JSONType(ignores = {"createdTime", "modifiedTime"})
public abstract class BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "gmt_created", updatable = false, nullable = false)
  @CreatedDate
  private Date gmtCreated;

  @Column(name = "gmt_modified", nullable = false)
  @LastModifiedDate
  private Date gmtModified;

  @Setter
  @Getter
  @Column(name = "term", nullable = false, columnDefinition = "MEDIUMTEXT")
  private String term;

  @Setter @Getter private String state;

  @Setter @Getter private int assignee;

  @Setter @Getter private int creator;

  @Setter @Getter private int reviewer;

  @Setter @Getter private double deleteToken;

  @Setter @Getter private int annotationType;
}
