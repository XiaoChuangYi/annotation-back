package cn.malgo.annotation.controller.task;

import cn.malgo.annotation.biz.doc.ListOriginalDocBiz;
import cn.malgo.annotation.biz.task.ImportDocBiz;
import cn.malgo.annotation.biz.task.ListDocDetailsBiz;
import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.controller.BaseController;
import cn.malgo.annotation.entity.OriginalDoc;
import cn.malgo.annotation.request.doc.ListDocDetailRequest;
import cn.malgo.annotation.request.doc.ListDocRequest;
import cn.malgo.annotation.request.task.ImportDocRequest;
import cn.malgo.annotation.result.PageVO;
import cn.malgo.annotation.vo.OriginalDocDetailVO;
import cn.malgo.service.exception.BusinessRuleException;
import cn.malgo.service.model.Response;
import cn.malgo.service.model.UserDetails;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v2/doc")
@Slf4j
public class OriginalDocController extends BaseController {
  private final String secretKey;
  private final ImportDocBiz importDocBiz;
  private final ListOriginalDocBiz listOriginalDocBiz;
  private final ListDocDetailsBiz listDocDetailsBiz;

  public OriginalDocController(
      @Value("${malgo.internal.secret-key}") String secretKey,
      final ImportDocBiz importDocBiz,
      final ListOriginalDocBiz listOriginalDocBiz,
      final ListDocDetailsBiz listDocDetailsBiz) {
    this.secretKey = secretKey;
    this.importDocBiz = importDocBiz;
    this.listOriginalDocBiz = listOriginalDocBiz;
    this.listDocDetailsBiz = listDocDetailsBiz;
  }

  @RequestMapping(value = "/import", method = RequestMethod.POST)
  public Response<List<OriginalDoc>> importDocs(
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount,
      @RequestBody ImportDocRequest request) {
    if (!StringUtils.equals(request.getSecretKey(), this.secretKey)
        && (userAccount == null || !userAccount.hasPermission(Permissions.ADMIN))) {
      throw new BusinessRuleException("permission-denied", "secret key or admin role required");
    }

    return new Response<>(importDocBiz.process(request, permission -> true));
  }

  /** 原始文本查询 */
  @RequestMapping(value = "/list-doc", method = RequestMethod.GET)
  public Response<PageVO<OriginalDoc>> listOriginalDoc(
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount,
      ListDocRequest listDocRequest) {
    return new Response<>(listOriginalDocBiz.process(listDocRequest, userAccount));
  }

  /** 文本detail详情查询 */
  @RequestMapping(value = "/list-doc-details/{id}", method = RequestMethod.GET)
  public Response<OriginalDocDetailVO> listDocDetails(
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount,
      @PathVariable("id") long id) {
    return new Response<>(listDocDetailsBiz.process(new ListDocDetailRequest(id), userAccount));
  }
}
