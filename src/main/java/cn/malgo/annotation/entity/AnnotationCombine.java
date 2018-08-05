package cn.malgo.annotation.entity;

import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.service.entity.BaseEntity;
import com.alibaba.fastjson.annotation.JSONField;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
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
    name = "annotation_combine",
    indexes = {
      @Index(columnList = "annotation_type"),
      @Index(columnList = "delete_token"),
      @Index(columnList = "assignee"),
      @Index(columnList = "state"),
      @Index(columnList = "reviewer"),
      @Index(columnList = "block_id"),
    })
@Getter
@Setter
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class AnnotationCombine extends BaseEntity {
  @Column(name = "term", nullable = false, columnDefinition = "MEDIUMTEXT")
  private String term;

  @Column(name = "state", nullable = false)
  private String state = AnnotationCombineStateEnum.unDistributed.name();

  @Column(name = "assignee", nullable = false)
  private long assignee = 1L;

  @Column(name = "creator", nullable = false)
  private int creator = 0;

  @Column(name = "reviewer", nullable = false)
  private int reviewer = 1;

  @Column(name = "delete_token", nullable = false)
  private long deleteToken = 0;

  @Column(name = "annotation_type", nullable = false)
  private int annotationType = 0;

  @Column(name = "final_annotation", nullable = false, columnDefinition = "MEDIUMTEXT")
  private String finalAnnotation = "";

  @Column(name = "manual_annotation", nullable = false, columnDefinition = "MEDIUMTEXT")
  private String manualAnnotation = "";

  @Column(name = "reviewed_annotation", nullable = false, columnDefinition = "MEDIUMTEXT")
  private String reviewedAnnotation = "";

  @Column(name = "block_id")
  private Long blockId;

  @Column(name = "comment", columnDefinition = "text")
  private String comment;

  @Column(name = "commit_timestamp", columnDefinition = "TIMESTAMP NULL")
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date commitTimestamp;

  @Transient private String userName;

  public AnnotationCombineStateEnum getStateEnum() {
    try {
      return AnnotationCombineStateEnum.valueOf(this.state);
    } catch (IllegalArgumentException ex) {
      log.warn("illegal annotation combine state: " + this.state + ", id: " + this.getId());
      return AnnotationCombineStateEnum.UNKNOWN;
    }
  }
}
