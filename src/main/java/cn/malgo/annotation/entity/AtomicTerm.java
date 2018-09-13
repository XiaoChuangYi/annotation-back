package cn.malgo.annotation.entity;

import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.service.entity.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class AtomicTerm extends BaseEntity {
  @Column(name = "term", nullable = false)
  private String term;

  @Column(name = "an_type", nullable = false)
  private String anType;

  @Column(name = "annotation_id", nullable = false)
  private Long annotationId;

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "annotation_type", nullable = false)
  private AnnotationTypeEnum annotationType;

  public AtomicTerm(
      final String term,
      final String anType,
      final Long annotationId,
      final AnnotationTypeEnum annotationType) {
    this.term = term;
    this.anType = anType;
    this.annotationId = annotationId;
    this.annotationType = annotationType;
  }
}
