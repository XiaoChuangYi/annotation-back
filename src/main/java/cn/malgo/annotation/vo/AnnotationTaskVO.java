package cn.malgo.annotation.vo;

import cn.malgo.annotation.entity.AnnotationTask;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AnnotationTaskVO {
  private long id;

  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date createdTime;

  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date lastModifiedTime;

  private String name;
  private String state;
  private int totalBranch; // 批次总条数
  private int totalWordNum; // 批次总字数
  private int finishBranch; // 已标注条数
  private int finishWordNum; // 已标注字数
  private int restBranch; // 剩余条数
  private int restWordNum; // 剩余标注字数
  private double inConformity; // 批次不一致性

  public AnnotationTaskVO(
      long id, Date createdTime, Date lastModifiedTime, String name, String state) {
    this.id = id;
    this.createdTime = createdTime;
    this.lastModifiedTime = lastModifiedTime;
    this.name = name;
    this.state = state;
  }

  public AnnotationTaskVO(AnnotationTask task) {
    this(
        task.getId(),
        task.getCreatedTime(),
        task.getLastModified(),
        task.getName(),
        task.getState().name(),
        task.getTotalBranchNum(),
        task.getTotalWordNum(),
        task.getAnnotatedBranchNum(),
        task.getAnnotatedWordNum(),
        task.getRestBranchNum(),
        task.getRestWordNum(),
        task.getInConformity());
  }
}
