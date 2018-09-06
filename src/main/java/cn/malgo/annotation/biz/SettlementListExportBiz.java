package cn.malgo.annotation.biz;

import cn.malgo.annotation.request.SettlementListExportRequest;
import cn.malgo.annotation.service.SettlementListExportService;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SettlementListExportBiz extends BaseBiz<SettlementListExportRequest, Object> {

  private final SettlementListExportService settlementListExportService;

  public SettlementListExportBiz(final SettlementListExportService settlementListExportService) {
    this.settlementListExportService = settlementListExportService;
  }

  @Override
  protected void validateRequest(SettlementListExportRequest request) throws InvalidInputException {
    if (request.getTaskId() == 0 && request.getAssigneeId() == 0) {
      throw new InvalidInputException(
          "parameters cannot zero at the same time", "缺少参数taskId和assigneeId");
    }
  }

  @Override
  protected Object doBiz(SettlementListExportRequest request) {
    try {
      settlementListExportService.exportPersonalSummaryInfo2Excel(
          request.getServletResponse(), request.getTaskId(), request.getAssigneeId());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
