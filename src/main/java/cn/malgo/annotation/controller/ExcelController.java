package cn.malgo.annotation.controller;

import cn.malgo.annotation.biz.SettlementListExportBiz;
import cn.malgo.annotation.config.PermissionConstant;
import cn.malgo.annotation.request.SettlementListExportRequest;
import cn.malgo.common.auth.PermissionAnno;
import cn.malgo.service.model.UserDetails;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v2")
@Slf4j
public class ExcelController extends BaseController {

  private final SettlementListExportBiz settlementListExportBiz;

  public ExcelController(final SettlementListExportBiz settlementListExportBiz) {
    this.settlementListExportBiz = settlementListExportBiz;
  }

  @PermissionAnno(PermissionConstant.ANNOTATION_SUMMARY_EXPORT_EXCEL)
  @RequestMapping(value = "/export-settlement-list", method = RequestMethod.GET)
  @ResponseBody
  public void exportSettlementList(
      SettlementListExportRequest settlementListExportRequest,
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount,
      HttpServletResponse servletResponse) {
    settlementListExportRequest.setServletResponse(servletResponse);
    settlementListExportBiz.process(settlementListExportRequest, userAccount);
  }
}
