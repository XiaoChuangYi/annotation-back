package cn.malgo.annotation.biz;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.request.SettlementListExportRequest;
import cn.malgo.annotation.service.SettlementListExportService;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequirePermission(Permissions.ADMIN)
public class SettlementListExportBiz extends BaseBiz<SettlementListExportRequest, Object> {

  private final SettlementListExportService settlementListExportService;

  public SettlementListExportBiz(final SettlementListExportService settlementListExportService) {
    this.settlementListExportService = settlementListExportService;
  }

  @Override
  protected void validateRequest(SettlementListExportRequest request) throws InvalidInputException {
    if (request.getTaskId() != 0 && request.getAssigneeId() != 0) {
      throw new InvalidInputException(
          "parameters cannot appear at the same time", "不能同时出现参数taskId和assigneeId");
    }
  }

  @Override
  protected Object doBiz(SettlementListExportRequest request, UserDetails user) {
    try {
      settlementListExportService.exportPersonalSummaryInfo2Excel(
          request.getServletResponse(), request.getTaskId(), request.getAssigneeId());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
