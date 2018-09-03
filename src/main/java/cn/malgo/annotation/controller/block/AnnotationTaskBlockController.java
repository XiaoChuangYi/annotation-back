package cn.malgo.annotation.controller.block;

import cn.malgo.annotation.biz.CleanOutBlockBiz;
import cn.malgo.annotation.biz.block.AnnotationBlockBatchAbandonBiz;
import cn.malgo.annotation.biz.block.AnnotationBlockResetToAnnotationBiz;
import cn.malgo.annotation.biz.block.BatchDeleteBlockBratTypeBiz;
import cn.malgo.annotation.biz.block.BatchDeleteBlockEntityMultipleBiz;
import cn.malgo.annotation.biz.block.BatchDeleteBlockRelationBiz;
import cn.malgo.annotation.biz.block.BatchUpdateBlockRelationBiz;
import cn.malgo.annotation.biz.brat.ListOverlapEntityBiz;
import cn.malgo.annotation.biz.brat.ListRelevanceAnnotationBiz;
import cn.malgo.annotation.biz.brat.block.AddBlockAnnotationBiz;
import cn.malgo.annotation.biz.brat.block.DeleteBlockAnnotationBiz;
import cn.malgo.annotation.biz.brat.block.GetAnnotationBlockBiz;
import cn.malgo.annotation.biz.brat.block.UpdateBlockAnnotationBiz;
import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.controller.BaseController;
import cn.malgo.annotation.cron.BlockNerUpdater;
import cn.malgo.annotation.dto.error.AnnotationErrorContext;
import cn.malgo.annotation.request.block.BatchAbandonBlockRequest;
import cn.malgo.annotation.request.block.BatchDeleteBlockBratTypeRequest;
import cn.malgo.annotation.request.block.BatchDeleteBlockRelationRequest;
import cn.malgo.annotation.request.block.BatchDeleteEntityMultipleRequest;
import cn.malgo.annotation.request.ListOverlapEntityRequest;
import cn.malgo.annotation.request.block.BatchUpdateBlockRelationRequest;
import cn.malgo.annotation.request.block.ListRelevanceAnnotationRequest;
import cn.malgo.annotation.request.block.ResetAnnotationBlockRequest;
import cn.malgo.annotation.request.brat.AddAnnotationGroupRequest;
import cn.malgo.annotation.request.brat.DeleteAnnotationGroupRequest;
import cn.malgo.annotation.request.brat.GetAutoAnnotationRequest;
import cn.malgo.annotation.request.brat.UpdateAnnotationGroupRequest;
import cn.malgo.annotation.result.PageVO;
import cn.malgo.annotation.vo.AnnotationBlockBratVO;
import cn.malgo.annotation.vo.ResetBlockToAnnotationResponse;
import cn.malgo.service.exception.BusinessRuleException;
import cn.malgo.service.model.Response;
import cn.malgo.service.model.UserDetails;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v2/block")
public class AnnotationTaskBlockController extends BaseController {

  private final BlockNerUpdater blockNerUpdater;
  private final AnnotationBlockResetToAnnotationBiz annotationBlockResetToAnnotationBiz;
  private final GetAnnotationBlockBiz getAnnotationBlockBiz;
  private final AddBlockAnnotationBiz addBlockAnnotationBiz;
  private final DeleteBlockAnnotationBiz deleteBlockAnnotationBiz;
  private final UpdateBlockAnnotationBiz updateBlockAnnotationBiz;
  private final ListRelevanceAnnotationBiz listRelevanceAnnotationBiz;
  private final ListOverlapEntityBiz listOverlapEntityBiz;
  private final BatchDeleteBlockEntityMultipleBiz batchDeleteBlockEntityMultipleBiz;
  private final BatchDeleteBlockRelationBiz batchDeleteBlockRelationBiz;
  private final BatchUpdateBlockRelationBiz batchUpdateBlockRelationBiz;
  private final CleanOutBlockBiz cleanOutBlockBiz;
  private final AnnotationBlockBatchAbandonBiz annotationBlockBatchAbandonBiz;
  private final BatchDeleteBlockBratTypeBiz batchDeleteBlockBratTypeBiz;

  public AnnotationTaskBlockController(
      final BlockNerUpdater blockNerUpdater,
      final AnnotationBlockResetToAnnotationBiz annotationBlockResetToAnnotationBiz,
      final GetAnnotationBlockBiz getAnnotationBlockBiz,
      final AddBlockAnnotationBiz addBlockAnnotationBiz,
      final DeleteBlockAnnotationBiz deleteBlockAnnotationBiz,
      final UpdateBlockAnnotationBiz updateBlockAnnotationBiz,
      final ListRelevanceAnnotationBiz listRelevanceAnnotationBiz,
      final ListOverlapEntityBiz listOverlapEntityBiz,
      final BatchDeleteBlockEntityMultipleBiz batchDeleteBlockEntityMultipleBiz,
      final BatchDeleteBlockRelationBiz batchDeleteBlockRelationBiz,
      final BatchUpdateBlockRelationBiz batchUpdateBlockRelationBiz,
      final CleanOutBlockBiz cleanOutBlockBiz,
      final AnnotationBlockBatchAbandonBiz annotationBlockBatchAbandonBiz,
      final BatchDeleteBlockBratTypeBiz batchDeleteBlockBratTypeBiz) {
    this.blockNerUpdater = blockNerUpdater;
    this.annotationBlockResetToAnnotationBiz = annotationBlockResetToAnnotationBiz;
    this.getAnnotationBlockBiz = getAnnotationBlockBiz;
    this.addBlockAnnotationBiz = addBlockAnnotationBiz;
    this.deleteBlockAnnotationBiz = deleteBlockAnnotationBiz;
    this.updateBlockAnnotationBiz = updateBlockAnnotationBiz;
    this.listRelevanceAnnotationBiz = listRelevanceAnnotationBiz;
    this.listOverlapEntityBiz = listOverlapEntityBiz;
    this.batchDeleteBlockEntityMultipleBiz = batchDeleteBlockEntityMultipleBiz;
    this.batchDeleteBlockRelationBiz = batchDeleteBlockRelationBiz;
    this.batchUpdateBlockRelationBiz = batchUpdateBlockRelationBiz;
    this.cleanOutBlockBiz = cleanOutBlockBiz;
    this.annotationBlockBatchAbandonBiz = annotationBlockBatchAbandonBiz;
    this.batchDeleteBlockBratTypeBiz = batchDeleteBlockBratTypeBiz;
  }

  // ADMIN ACTIONS
  @RequestMapping(value = "/update-block-ner", method = RequestMethod.POST)
  public Response<Boolean> updateBlockNer(
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount) {
    if (userAccount == null || !userAccount.hasPermission(Permissions.ADMIN)) {
      throw new BusinessRuleException("permission-deinied", "无权限");
    }

    blockNerUpdater.updateBlockNer();
    return new Response<>(true);
  }

  @RequestMapping(value = "/update-block-ner-rate", method = RequestMethod.POST)
  public Response<Boolean> updateBlockNerRate(
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount) {
    if (userAccount == null || !userAccount.hasPermission(Permissions.ADMIN)) {
      throw new BusinessRuleException("permission-deinied", "无权限");
    }

    blockNerUpdater.updateBlockNerRate();
    return new Response<>(true);
  }

  /** ANNOTATED或FINISHED状态的block可以被打回重新标注或审核 */
  @RequestMapping(value = "/reset-block-to-annotation", method = RequestMethod.POST)
  public Response<ResetBlockToAnnotationResponse> resetBlockToAnnotation(
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount,
      @RequestBody ResetAnnotationBlockRequest resetAnnotationBlockRequest) {
    return new Response<>(
        new ResetBlockToAnnotationResponse(
            annotationBlockResetToAnnotationBiz.process(resetAnnotationBlockRequest, userAccount)));
  }

  /** 获取block标注 */
  @RequestMapping(value = "/get-block-annotation/{id}", method = RequestMethod.GET)
  public Response<AnnotationBlockBratVO> getBlockAnnotation(
      @PathVariable("id") long id,
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount) {
    return new Response<>(
        getAnnotationBlockBiz.process(new GetAutoAnnotationRequest(id), userAccount));
  }

  /** 新增block标注 */
  @RequestMapping(value = "/add-block-annotation", method = RequestMethod.POST)
  public Response<AnnotationBlockBratVO> addBlockAnnotation(
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount,
      @RequestBody AddAnnotationGroupRequest addAnnotationGroupRequest) {
    return new Response<>(addBlockAnnotationBiz.process(addAnnotationGroupRequest, userAccount));
  }

  /** 删除block标注 */
  @RequestMapping(value = "/delete-block-annotation", method = RequestMethod.POST)
  public Response<AnnotationBlockBratVO> deleteBlockAnnotation(
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount,
      @RequestBody DeleteAnnotationGroupRequest deleteAnnotationGroupRequest) {
    return new Response<>(
        deleteBlockAnnotationBiz.process(deleteAnnotationGroupRequest, userAccount));
  }

  /** 更新block标注 */
  @RequestMapping(value = "/update-block-annotation", method = RequestMethod.POST)
  public Response<AnnotationBlockBratVO> updateBlockAnnotation(
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount,
      @RequestBody UpdateAnnotationGroupRequest updateAnnotationGroupRequest) {
    return new Response<>(
        updateBlockAnnotationBiz.process(updateAnnotationGroupRequest, userAccount));
  }

  /** 五元组查询block关联查询 */
  @RequestMapping(value = "/list-block-relation", method = RequestMethod.GET)
  public Response<PageVO<AnnotationErrorContext>> listBlockRelation(
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount,
      ListRelevanceAnnotationRequest listRelevanceAnnotationRequest) {
    return new Response<>(
        listRelevanceAnnotationBiz.process(listRelevanceAnnotationRequest, userAccount));
  }

  /** overlap entity block查询 */
  @RequestMapping(value = "/list-overlap-entity-block", method = RequestMethod.GET)
  public Response<PageVO<AnnotationBlockBratVO>> listOverlapEntityBlock(
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount,
      ListOverlapEntityRequest listOverlapEntityRequest) {
    return new Response<>(listOverlapEntityBiz.process(listOverlapEntityRequest, userAccount));
  }

  /** 批量删除block一词多义实体 */
  @RequestMapping(value = "/batch-delete-multiple-entity", method = RequestMethod.POST)
  public Response<List<Long>> batchDeleteMultipleEntity(
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount,
      @RequestBody BatchDeleteEntityMultipleRequest batchDeleteEntityMultipleRequest) {
    return new Response<>(
        batchDeleteBlockEntityMultipleBiz.process(batchDeleteEntityMultipleRequest, userAccount));
  }

  /** 批量删除关联标注关系 */
  @RequestMapping(value = "/batch-delete-relation", method = RequestMethod.POST)
  public Response<List<Long>> batchDeleteRelation(
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount,
      @RequestBody BatchDeleteBlockRelationRequest batchDeleteBlockRelationRequest) {
    return new Response<>(
        batchDeleteBlockRelationBiz.process(batchDeleteBlockRelationRequest, userAccount));
  }

  /** 批量更新关联类型 */
  @RequestMapping(value = "/batch-update-relation", method = RequestMethod.POST)
  public Response<List<Long>> batchUpdateRelation(
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount,
      @RequestBody BatchUpdateBlockRelationRequest request) {
    return new Response<>(batchUpdateBlockRelationBiz.process(request, userAccount));
  }

  /** 清洗指定批次的语料 */
  @RequestMapping(value = "/clean-out-block", method = RequestMethod.POST)
  public Response cleanOutBlock(
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount) {
    return new Response<>(cleanOutBlockBiz.process(null, userAccount));
  }

  /** 批量放弃未处理状态的语料 */
  @RequestMapping(value = "/batch-abandon-block", method = RequestMethod.POST)
  public Response batchAbandonBlock(
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount,
      @RequestBody BatchAbandonBlockRequest request) {
    return new Response<>(annotationBlockBatchAbandonBiz.process(request, userAccount));
  }

  /** 批量删除指定id，对应的tag标签和rTag标签 */
  @RequestMapping(value = "/batch-delete-block-brat-type", method = RequestMethod.POST)
  public Response batchDeleteBlockBratType(@RequestBody BatchDeleteBlockBratTypeRequest request) {
    return new Response<>(batchDeleteBlockBratTypeBiz.process(request, null));
  }
}
