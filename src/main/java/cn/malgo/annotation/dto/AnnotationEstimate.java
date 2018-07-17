package cn.malgo.annotation.dto;

import java.util.Date;

public interface AnnotationEstimate {
  int getAssignee();

  String getAccountName();

  Date getGmtModified();

  int getFinishWordNum();

  int getRestWordNum();

  int getFinishBranch();

  int getRestBranch();

  double getInConformity();
}
