package cn.malgo.annotation.entity;

import cn.malgo.service.entity.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(
    name = "an_type",
    indexes = {
      @Index(columnList = "task_id"),
      @Index(columnList = "type_code, task_id", unique = true)
    })
@EntityListeners(AuditingEntityListener.class)
@ToString(callSuper = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnType extends BaseEntity {
  @Column(name = "type_name", nullable = false, length = 64)
  private String typeName;

  @Column(name = "state", nullable = false, columnDefinition = "VARCHAR(64) default 'ENABLE'")
  private String state;

  @Column(name = "type_code", nullable = false, length = 64)
  private String typeCode;

  @Column(name = "parent_id", nullable = false, columnDefinition = "BIGINT(20) default 0")
  private Long parentId;

  @Column(name = "has_children", nullable = false, columnDefinition = "int(11) default 0")
  private int hasChildren;

  @Column(name = "task_id", nullable = false)
  private int taskId;

  @Column(name = "labels")
  private String labels;

  @Column(name = "bg_color", nullable = false, columnDefinition = "VARCHAR(255) default 'white'")
  private String bgColor;

  @Column(name = "fg_color", nullable = false, columnDefinition = "VARCHAR(255) default 'black'")
  private String fgColor;
}
