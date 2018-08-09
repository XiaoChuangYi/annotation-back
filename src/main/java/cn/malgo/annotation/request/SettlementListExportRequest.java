package cn.malgo.annotation.request;

import javax.servlet.http.HttpServletResponse;
import lombok.Data;

@Data
public class SettlementListExportRequest {
  private long taskId;
  private long assigneeId;
  private HttpServletResponse servletResponse;
}
