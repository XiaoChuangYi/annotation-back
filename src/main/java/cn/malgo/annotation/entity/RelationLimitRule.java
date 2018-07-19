package cn.malgo.annotation.entity;

import cn.malgo.service.entity.BaseEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
    name = "relation_limit_rule",
    indexes = {
      @Index(
          name = "index_source_target_relation_type",
          columnList = "source,target,relationType",
          unique = true),
    })
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class RelationLimitRule extends BaseEntity {
  private String source;
  private String target;
  private String relationType;
}
