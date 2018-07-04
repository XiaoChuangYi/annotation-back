package cn.malgo.annotation.controller.task;

import cn.malgo.annotation.biz.task.ImportDocBiz;

import cn.malgo.annotation.biz.doc.ListOriginalDocBiz;
import cn.malgo.annotation.entity.OriginalDoc;
import cn.malgo.annotation.entity.UserAccount;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.exception.BusinessRuleException;
import cn.malgo.annotation.request.task.ImportDocRequest;
import cn.malgo.annotation.request.doc.ListDocRequest;
import cn.malgo.annotation.result.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v2/doc")
@Slf4j
public class OriginalDocController {

  private final String secretKey;
  private final ImportDocBiz importDocBiz;
  private final ListOriginalDocBiz listOriginalDocBiz;

  public OriginalDocController(
      @Value("${malgo.internal.secret-key}") String secretKey,
      final ImportDocBiz importDocBiz,
      final ListOriginalDocBiz listOriginalDocBiz) {
    this.secretKey = secretKey;
    this.importDocBiz = importDocBiz;
    this.listOriginalDocBiz = listOriginalDocBiz;
  }

  @RequestMapping(value = "/import", method = RequestMethod.POST)
  public Response<List<OriginalDoc>> importDocs(
      @ModelAttribute(value = "userAccount", binding = false) UserAccount userAccount,
      @RequestBody ImportDocRequest request) {
    if (!StringUtils.equals(request.getSecretKey(), this.secretKey)
        && (userAccount == null
            || userAccount.getRoleId() != AnnotationRoleStateEnum.admin.getRole())) {
      throw new BusinessRuleException("permission-denied", "secret key or admin role required");
    }

    return new Response<>(
        importDocBiz.process(request, 0, AnnotationRoleStateEnum.admin.getRole()));
  }

  /** 原始文本查询 */
  @RequestMapping(value = "/list-doc", method = RequestMethod.GET)
  public Response listOriginalDoc(ListDocRequest listDocRequest) {
    return new Response(listOriginalDocBiz.process(listDocRequest, 0, 0));
  }
}
