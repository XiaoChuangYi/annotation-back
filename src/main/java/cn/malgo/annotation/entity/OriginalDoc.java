package cn.malgo.annotation.entity;

import cn.malgo.annotation.enums.OriginalDocState;
import cn.malgo.service.entity.BaseEntity;
import com.alibaba.fastjson.annotation.JSONType;
import java.util.HashSet;
import java.util.Set;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
    name = "original_doc",
    indexes = {
      @Index(name = "idx_type", columnList = "type"),
      @Index(name = "idx_name", columnList = "name"),
      @Index(name = "idx_source", columnList = "source"),
      @Index(name = "idx_state", columnList = "state"),
      @Index(columnList = "text_length"),
    })
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@ToString(
    exclude = {"tasks"},
    callSuper = true)
@Getter
@Setter
@JSONType(ignores = {"tasks"})
public class OriginalDoc extends BaseEntity {

  @Column(name = "name", nullable = false, length = 512)
  @NonNull
  private String name;

  @Column(name = "text", nullable = false, columnDefinition = "MEDIUMTEXT")
  @NonNull
  private String text;

  @Column(name = "text_length", nullable = false, columnDefinition = "int(11) default 0")
  @Getter
  @Setter
  private int textLength;

  @Column(name = "type", nullable = false, length = 16)
  @NonNull
  private String type;

  @Column(name = "source", nullable = false, length = 16)
  @NonNull
  private String source;

  @Enumerated(EnumType.STRING)
  @Column(name = "state", nullable = false, length = 16)
  private OriginalDocState state = OriginalDocState.IMPORTED;

  @OneToMany(
      fetch = FetchType.LAZY,
      mappedBy = "doc",
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<AnnotationTaskDoc> tasks = new ArrayList<>();

  //  @OneToMany(fetch = FetchType.LAZY,
  //      mappedBy = "doc",
  //      cascade = CascadeType.ALL,
  //      orphanRemoval = true
  //  )
  //  private Set<OriginalDocBlock> docBlocks = new HashSet<>();
}
