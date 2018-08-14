package cn.malgo.annotation.entity;

import cn.malgo.annotation.enums.AnnotationStateEnum;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.service.entity.BaseEntity;
import com.alibaba.fastjson.annotation.JSONField;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
    name = "annotation_new",
    indexes = {
      @Index(columnList = "annotation_type"),
      @Index(columnList = "assignee"),
      @Index(columnList = "state"),
      @Index(columnList = "delete_token"),
      @Index(columnList = "block_id"),
    })
@Getter
@Setter
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class AnnotationNew extends BaseEntity {

  @Column(name = "term", nullable = false, columnDefinition = "MEDIUMTEXT")
  private String term;

  @Enumerated(EnumType.STRING)
  @Column(name = "state", nullable = false, length = 32)
  private AnnotationStateEnum state = AnnotationStateEnum.UN_DISTRIBUTED;

  @Column(name = "assignee", nullable = false)
  private long assignee = 1L;

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "annotation_type", nullable = false, updatable = false)
  private AnnotationTypeEnum annotationType;

  @Column(name = "final_annotation", nullable = false, columnDefinition = "MEDIUMTEXT")
  private String finalAnnotation = "";

  @Column(name = "manual_annotation", nullable = false, columnDefinition = "MEDIUMTEXT")
  private String manualAnnotation = "";

  @Column(name = "block_id")
  private Long blockId;

  @Column(name = "task_id")
  private Long taskId;

  @Column(name = "comment", columnDefinition = "text")
  private String comment;

  @Column(name = "commit_timestamp", columnDefinition = "TIMESTAMP NULL")
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date commitTimestamp;

  @Column(name = "expiration_time", columnDefinition = "TIMESTAMP NULL")
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date expirationTime;

  @Column(name = "precision_rate")
  private double precisionRate = 0;

  @Column(name = "recall_rate")
  private double recallRate = 0;

  @Column(name = "delete_token")
  private long deleteToken = 0;

  @Transient private String userName;
}
