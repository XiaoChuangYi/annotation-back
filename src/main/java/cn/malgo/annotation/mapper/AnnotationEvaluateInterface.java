package cn.malgo.annotation.mapper;

import cn.malgo.annotation.dto.AnnotationOverview;
import cn.malgo.annotation.vo.AnnotationEstimateVO;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface AnnotationEvaluateInterface {

  List<AnnotationEstimateVO> listAnnotationEstimateSummary(
      @Param("task_id") int taskId,
      @Param("gmt_modified") Date gmtModified,
      @Param("assignee") int assignee);

  List<AnnotationOverview> listAnnotationOverviewSummary(@Param("task_id") int taskId);
}
