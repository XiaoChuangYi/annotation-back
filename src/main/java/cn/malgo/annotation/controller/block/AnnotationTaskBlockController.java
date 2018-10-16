package cn.malgo.annotation.controller.block;

import cn.malgo.annotation.biz.CleanOutBlockBiz;
import cn.malgo.annotation.biz.block.AnnotationBlockBatchAbandonBiz;
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
import cn.malgo.annotation.config.PermissionConstant;
import cn.malgo.annotation.cron.BlockNerUpdater;
import cn.malgo.annotation.dto.error.AnnotationErrorContext;
import cn.malgo.annotation.request.block.BatchAbandonBlockRequest;
import cn.malgo.annotation.request.block.BatchDeleteBlockBratTypeRequest;
import cn.malgo.annotation.request.block.BatchDeleteBlockRelationRequest;
import cn.malgo.annotation.request.block.BatchDeleteEntityMultipleRequest;
import cn.malgo.annotation.request.ListOverlapEntityRequest;
import cn.malgo.annotation.request.block.BatchUpdateBlockRelationRequest;
import cn.malgo.annotation.request.block.CleanOutBlockRequest;
import cn.malgo.annotation.request.block.ListRelevanceAnnotationRequest;
import cn.malgo.annotation.request.brat.AddAnnotationGroupRequest;
import cn.malgo.annotation.request.brat.DeleteAnnotationGroupRequest;
import cn.malgo.annotation.request.brat.GetAutoAnnotationRequest;
import cn.malgo.annotation.request.brat.UpdateAnnotationGroupRequest;
import cn.malgo.annotation.result.PageVO;
import cn.malgo.annotation.vo.AnnotationBlockBratVO;
import cn.malgo.common.auth.PermissionAnno;
import cn.malgo.common.auth.user.UserDetailService;
import cn.malgo.service.exception.BusinessRuleException;
import cn.malgo.service.model.Response;
import cn.malgo.service.model.UserDetails;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v2/block")
public class AnnotationTaskBlockController {

  private final BlockNerUpdater blockNerUpdater;
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

  private final UserDetailService userDetailService;

  public AnnotationTaskBlockController(
      final BlockNerUpdater blockNerUpdater,
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
      final UserDetailService userDetailService,
      final BatchDeleteBlockBratTypeBiz batchDeleteBlockBratTypeBiz) {
    this.blockNerUpdater = blockNerUpdater;
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
    this.userDetailService = userDetailService;
  }

  @PermissionAnno(PermissionConstant.ANNOTATION_UPDATE_BLOCK_NER)
  @RequestMapping(value = "/update-block-ner", method = RequestMethod.POST)
  public Response<Boolean> updateBlockNer(final HttpServletRequest request) {
    final UserDetails userDetails = userDetailService.getUserDetails(request);
    if (!userDetails.hasPermission(PermissionConstant.ANNOTATION_UPDATE_BLOCK_NER)) {
      throw new BusinessRuleException("permission-deinied", "无权限");
    }
    blockNerUpdater.updateBlockNer();
    return new Response<>(true);
  }

  @PermissionAnno(PermissionConstant.ANNOTATION_UPDATE_BLOCK_NER_RATE)
  @RequestMapping(value = "/update-block-ner-rate", method = RequestMethod.POST)
  public Response<Boolean> updateBlockNerRate(final HttpServletRequest request) {
    final UserDetails userDetails = userDetailService.getUserDetails(request);
    if (!userDetails.hasPermission(PermissionConstant.ANNOTATION_UPDATE_BLOCK_NER_RATE)) {
      throw new BusinessRuleException("permission-deinied", "无权限");
    }

    blockNerUpdater.updateBlockNerRate();
    return new Response<>(true);
  }

  /** ANNOTATED或FINISHED状态的block可以被打回重新标注或审核 */
  //  @RequestMapping(value = "/reset-block-to-annotation", method = RequestMethod.POST)
  //  public Response<ResetBlockToAnnotationResponse> resetBlockToAnnotation(
  //      final HttpServletRequest request,
  //      @RequestBody ResetAnnotationBlockRequest resetAnnotationBlockRequest) {
  //    return new Response<>(
  //        new ResetBlockToAnnotationResponse(
  //            annotationBlockResetToAnnotationBiz.process(resetAnnotationBlockRequest,
  // userDetails)));
  //  }

  /** 获取block标注 */
  @PermissionAnno(PermissionConstant.ANNOTATION_BLOCK_DETAIL)
  @RequestMapping(value = "/get-block-annotation/{id}", method = RequestMethod.GET)
  public Response<AnnotationBlockBratVO> getBlockAnnotation(@PathVariable("id") long id) {
    return new Response<>(getAnnotationBlockBiz.process(new GetAutoAnnotationRequest(id), null));
  }

  /** 新增block标注 */
  @PermissionAnno(PermissionConstant.ANNOTATION_BLOCK_ADD)
  @RequestMapping(value = "/add-block-annotation", method = RequestMethod.POST)
  public Response<AnnotationBlockBratVO> addBlockAnnotation(
      @RequestBody AddAnnotationGroupRequest addAnnotationGroupRequest) {
    return new Response<>(addBlockAnnotationBiz.process(addAnnotationGroupRequest, null));
  }

  /** 删除block标注 */
  @PermissionAnno(PermissionConstant.ANNOTATION_BLOCK_DELETE)
  @RequestMapping(value = "/delete-block-annotation", method = RequestMethod.POST)
  public Response<AnnotationBlockBratVO> deleteBlockAnnotation(
      @RequestBody DeleteAnnotationGroupRequest deleteAnnotationGroupRequest) {
    return new Response<>(deleteBlockAnnotationBiz.process(deleteAnnotationGroupRequest, null));
  }

  /** 更新block标注 */
  @PermissionAnno(PermissionConstant.ANNOTATION_BLOCK_UPDATE)
  @RequestMapping(value = "/update-block-annotation", method = RequestMethod.POST)
  public Response<AnnotationBlockBratVO> updateBlockAnnotation(
      @RequestBody UpdateAnnotationGroupRequest updateAnnotationGroupRequest) {
    return new Response<>(updateBlockAnnotationBiz.process(updateAnnotationGroupRequest, null));
  }

  /** 五元组查询block关联查询 */
  @PermissionAnno(PermissionConstant.ANNOTATION_BLOCK_RELATION_SEARCH)
  @RequestMapping(value = "/list-block-relation", method = RequestMethod.GET)
  public Response<PageVO<AnnotationErrorContext>> listBlockRelation(
      ListRelevanceAnnotationRequest listRelevanceAnnotationRequest) {
    return new Response<>(listRelevanceAnnotationBiz.process(listRelevanceAnnotationRequest, null));
  }

  /** overlap entity block查询 */
  @PermissionAnno(PermissionConstant.ANNOTATION_BLOCK_OVERLAP_SEARCH)
  @RequestMapping(value = "/list-overlap-entity-block", method = RequestMethod.GET)
  public Response<PageVO<AnnotationBlockBratVO>> listOverlapEntityBlock(
      ListOverlapEntityRequest listOverlapEntityRequest) {
    return new Response<>(listOverlapEntityBiz.process(listOverlapEntityRequest, null));
  }

  /** 批量删除block一词多义实体 */
  @PermissionAnno(PermissionConstant.ANNOTATION_BLOCK_BATCH_ENTITY_DELETE)
  @RequestMapping(value = "/batch-delete-multiple-entity", method = RequestMethod.POST)
  public Response<List<Long>> batchDeleteMultipleEntity(
      @RequestBody BatchDeleteEntityMultipleRequest batchDeleteEntityMultipleRequest) {
    return new Response<>(
        batchDeleteBlockEntityMultipleBiz.process(batchDeleteEntityMultipleRequest, null));
  }

  /** 批量删除关联标注关系 */
  @PermissionAnno(PermissionConstant.ANNOTATION_BLOCK_BATCH_RELATION_DELETE)
  @RequestMapping(value = "/batch-delete-relation", method = RequestMethod.POST)
  public Response<List<Long>> batchDeleteRelation(
      @RequestBody BatchDeleteBlockRelationRequest batchDeleteBlockRelationRequest) {
    return new Response<>(
        batchDeleteBlockRelationBiz.process(batchDeleteBlockRelationRequest, null));
  }

  /** 批量更新关联类型 */
  @PermissionAnno(PermissionConstant.ANNOTATION_BLOCK_BATCH_RELATION_UPDATE)
  @RequestMapping(value = "/batch-update-relation", method = RequestMethod.POST)
  public Response<List<Long>> batchUpdateRelation(
      @RequestBody BatchUpdateBlockRelationRequest request) {
    return new Response<>(batchUpdateBlockRelationBiz.process(request, null));
  }

  /** 清洗指定批次的语料 */
  @PermissionAnno(PermissionConstant.ANNOTATION_BATCH_CLEANED)
  @RequestMapping(value = "/clean-out-block", method = RequestMethod.POST)
  public Response cleanOutBlock(@RequestBody CleanOutBlockRequest request) {
    return new Response<>(cleanOutBlockBiz.process(request, null));
  }

  /** 批量放弃未处理状态的语料 */
  @PermissionAnno(PermissionConstant.ANNOTATION_BLOCK_ABANDON)
  @RequestMapping(value = "/batch-abandon-block", method = RequestMethod.POST)
  public Response batchAbandonBlock(@RequestBody BatchAbandonBlockRequest request) {
    return new Response<>(annotationBlockBatchAbandonBiz.process(request, null));
  }

  /** 批量删除指定id，对应的tag标签和rTag标签 */
  @PermissionAnno(PermissionConstant.ANNOTATION_BLOCK_BATCH_DELETE)
  @RequestMapping(value = "/batch-delete-block-brat-type", method = RequestMethod.POST)
  public Response batchDeleteBlockBratType(@RequestBody BatchDeleteBlockBratTypeRequest request) {
    return new Response<>(batchDeleteBlockBratTypeBiz.process(request, null));
  }
}
