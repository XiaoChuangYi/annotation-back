package cn.malgo.annotation.vo;

import cn.malgo.annotation.biz.AnnotationEstimateQueryBiz.CurrentTaskOverviewPair;
import cn.malgo.annotation.result.PageVO;
import lombok.Value;

@Value
public class AnnotationStaffEvaluateVO {
  private PageVO pageVO;
  private CurrentTaskOverviewPair currentTaskOverviewPair;
}
