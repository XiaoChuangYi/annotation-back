package cn.malgo.annotation.entity;

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
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/** Created by cjl on 2018/6/1. */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class AtomicTerm {

  public AtomicTerm(String term, String annotationType, int annotationId) {
    this.term = term;
    this.annotationType = annotationType;
    this.annotationId = annotationId;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private String term;
  private String annotationType;
  private long deleteToken;

  @CreatedDate
  @Column(name = "gmt_created", updatable = false, nullable = false)
  private Date gmtCreated;

  private int annotationId;
}