package cn.malgo.annotation.vo;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnnotationTaskVO {

  private int id;
  private Date createdTime;
  private Date lastModifiedTime;
  private String name;
  private String state;

  public AnnotationTaskVO(
      int id, Date createdTime, Date lastModifiedTime, String name, String state) {
    this.id = id;
    this.createdTime = createdTime;
    this.lastModifiedTime = lastModifiedTime;
    this.name = name;
    this.state = state;
  }

  private int totalBranch; // 批次总条数
  private int totalWordNum; // 批次总字数
  private int finishWordNum; // 已标注字数
  private int restWordNum; // 剩余标注字数
  private int finishBranch; // 已标注条数
  private int restBranch; // 剩余条数
  private double inConformity; // 批次不一致性
  private int abandonBranch; // 遗弃次数
  private int abandonWordNum; // 遗弃总次数
}
