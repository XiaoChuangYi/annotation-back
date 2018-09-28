package cn.malgo.annotation.controller.task;

import cn.malgo.annotation.biz.doc.CreateBlocksFromDocBiz;
import cn.malgo.annotation.biz.doc.ImportDocBiz;
import cn.malgo.annotation.biz.doc.ListOriginalDocBiz;
import cn.malgo.annotation.config.PermissionConstant;
import cn.malgo.annotation.entity.OriginalDoc;
import cn.malgo.annotation.request.doc.ListDocRequest;
import cn.malgo.annotation.request.task.CreateBlocksFromDocRequest;
import cn.malgo.annotation.request.task.ImportDocRequest;
import cn.malgo.annotation.result.PageVO;
import cn.malgo.annotation.vo.CreateBlocksFromDocVO;
import cn.malgo.common.auth.PermissionAnno;
import cn.malgo.common.auth.user.UserDetailService;
import cn.malgo.service.exception.BusinessRuleException;
import cn.malgo.service.model.Response;
import cn.malgo.service.model.UserDetails;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v2/doc")
@Slf4j
public class OriginalDocController {

  private final String secretKey;
  private final ImportDocBiz importDocBiz;
  private final CreateBlocksFromDocBiz createBlocksFromDocBiz;
  private final ListOriginalDocBiz listOriginalDocBiz;
  private final UserDetailService userDetailService;

  public OriginalDocController(
      @Value("${malgo.internal.secret-key}") String secretKey,
      final ImportDocBiz importDocBiz,
      final CreateBlocksFromDocBiz createBlocksFromDocBiz,
      final ListOriginalDocBiz listOriginalDocBiz,
      final UserDetailService userDetailService) {
    this.secretKey = secretKey;
    this.importDocBiz = importDocBiz;
    this.createBlocksFromDocBiz = createBlocksFromDocBiz;
    this.listOriginalDocBiz = listOriginalDocBiz;
    this.userDetailService = userDetailService;
  }

  @PermissionAnno(PermissionConstant.ANNOTATION_DOC_IMPORT)
  @RequestMapping(value = "/import", method = RequestMethod.POST)
  public Response<List<OriginalDoc>> importDocs(
      final HttpServletRequest servletRequest, @RequestBody ImportDocRequest request) {
    final UserDetails userDetails = userDetailService.getUserDetails(servletRequest);
    if (!StringUtils.equals(request.getSecretKey(), this.secretKey)
        && !userDetails.hasPermission(PermissionConstant.ANNOTATION_DOC_IMPORT)) {
      throw new BusinessRuleException("permission-denied", "secret key or admin role required");
    }

    return new Response<>(importDocBiz.process(request, permission -> true));
  }

  @PermissionAnno(PermissionConstant.ANNOTATION_BLOCK_IMPORT)
  @RequestMapping(value = "/create-blocks", method = RequestMethod.POST)
  public Response<CreateBlocksFromDocVO> createBlocks(
      @RequestBody CreateBlocksFromDocRequest request) {
    return new Response<>(createBlocksFromDocBiz.process(request, null));
  }

  /** 原始文本查询 */
  @PermissionAnno(PermissionConstant.ANNOTATION_DOC_LIST)
  @RequestMapping(value = "/list-doc", method = RequestMethod.GET)
  public Response<PageVO<OriginalDoc>> listOriginalDoc(ListDocRequest listDocRequest) {
    return new Response<>(listOriginalDocBiz.process(listDocRequest, null));
  }
}
