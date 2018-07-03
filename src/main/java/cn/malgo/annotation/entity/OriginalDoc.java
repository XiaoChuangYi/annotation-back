package cn.malgo.annotation.entity;

import cn.malgo.annotation.enums.OriginalDocState;
import com.alibaba.fastjson.annotation.JSONType;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(
  name = "original_doc",
  indexes = {
    @Index(name = "idx_type", columnList = "type"),
    @Index(name = "idx_name", columnList = "name"),
    @Index(name = "idx_source", columnList = "source"),
    @Index(name = "idx_state", columnList = "state"),
  }
)
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(of = "id")
@ToString
@JSONType(ignores = {"gmtCreated", "gmtModified"})
public class OriginalDoc {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Getter
  private int id;

  @Column(name = "gmt_created", updatable = false, nullable = false)
  @CreatedDate
  private Date gmtCreated;

  @Column(name = "gmt_modified", nullable = false)
  @LastModifiedDate
  private Date gmtModified;

  @Column(name = "name", nullable = false, length = 512)
  @Getter
  @Setter
  @NonNull
  private String name;

  @Column(name = "text", nullable = false, columnDefinition = "MEDIUMTEXT")
  @Getter
  @Setter
  @NonNull
  private String text;

  @Column(name = "type", nullable = false, length = 16)
  @Getter
  @Setter
  @NonNull
  private String type;

  @Column(name = "source", nullable = false, length = 16)
  @Getter
  @Setter
  @NonNull
  private String source;

  @Enumerated(EnumType.STRING)
  @Column(name = "state", nullable = false, length = 16)
  @Getter
  @Setter
  private OriginalDocState state = OriginalDocState.IMPORTED;
}
