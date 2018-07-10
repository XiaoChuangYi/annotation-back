package cn.malgo.annotation.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(
    name = "relation_limit_rule",
    indexes = {
      @Index(name = "index_source_target_relation_type", columnList = "source,target,relationType"),
    })
@Data
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class RelationLimitRule {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private String source;
  private String target;
  private String relationType;
}
