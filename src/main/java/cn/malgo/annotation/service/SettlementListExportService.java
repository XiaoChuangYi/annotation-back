package cn.malgo.annotation.service;

import javax.servlet.http.HttpServletResponse;

public interface SettlementListExportService {

  void exportPersonalSummaryInfo2Excel(HttpServletResponse response, long taskId, long assigneeId)
      throws Exception;
}
