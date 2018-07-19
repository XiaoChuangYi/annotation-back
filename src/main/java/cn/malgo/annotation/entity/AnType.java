package cn.malgo.annotation.entity;

import cn.malgo.service.entity.BaseEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;

@Entity
@EntityListeners(AuditingEntityListener.class)
@ToString(callSuper = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnType extends BaseEntity {
  private String typeName;
  private String state;
  private String typeCode;
  private String parentId;
  private int hasChildren;
  private int taskId;
}
