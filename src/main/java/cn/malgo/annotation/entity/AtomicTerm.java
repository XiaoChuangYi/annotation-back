package cn.malgo.annotation.entity;

import cn.malgo.service.entity.BaseEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;

@Entity
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class AtomicTerm extends BaseEntity {
  private String term;
  private String annotationType;
  private long deleteToken;
  private long annotationId;

  public AtomicTerm(String term, String annotationType, long annotationId) {
    this.term = term;
    this.annotationType = annotationType;
    this.annotationId = annotationId;
  }
}
