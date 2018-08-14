package cn.malgo.annotation.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import java.util.Date;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(
    name = "original_doc_block",
    indexes = {
      @Index(name = "idx_order", columnList = "block_order"),
    })
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"doc", "block"})
@Getter
@Setter
@JSONType(ignores = {"doc", "block"})
public class OriginalDocBlock {
  @EmbeddedId private OriginalDocBlockId id;

  @CreatedDate
  @Column(
      name = "created_time",
      updatable = false,
      nullable = false,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date createdTime;

  @LastModifiedDate
  @Column(
      name = "last_modified",
      nullable = false,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date lastModified;

  @ManyToOne(fetch = FetchType.LAZY /*, cascade = CascadeType.ALL*/)
  @MapsId("docId")
  @JoinColumn(name = "doc_id")
  private OriginalDoc doc;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @MapsId("blockId")
  @JoinColumn(name = "block_id")
  private AnnotationTaskBlock block;

  public OriginalDocBlock(final OriginalDoc doc, final AnnotationTaskBlock block, final int order) {
    this.doc = doc;
    this.block = block;
    this.id = new OriginalDocBlockId(doc.getId(), block.getId(), order);
  }
}
