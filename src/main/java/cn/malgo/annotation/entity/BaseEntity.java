package cn.malgo.annotation.entity;

import com.alibaba.fastjson.annotation.JSONType;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

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

  @Setter @Getter private String state = AnnotationCombineStateEnum.unDistributed.name();

  @Setter @Getter private int assignee = 1;

  @Setter @Getter private int creator = 0;

  @Setter @Getter private int reviewer = 1;

  @Setter @Getter private long deleteToken;

  @Setter @Getter private int annotationType;
}
