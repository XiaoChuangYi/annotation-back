package cn.malgo.annotation.controller.block;

import cn.malgo.annotation.biz.block.AnnotationBlockResetToAnnotationBiz;
import cn.malgo.annotation.biz.brat.ListOverlapEntityBiz;
import cn.malgo.annotation.biz.brat.ListRelevanceAnnotationBiz;
import cn.malgo.annotation.biz.brat.block.AddBlockAnnotationBiz;
import cn.malgo.annotation.biz.brat.block.DeleteBlockAnnotationBiz;
import cn.malgo.annotation.biz.brat.block.GetAnnotationBlockBiz;
import cn.malgo.annotation.biz.brat.block.UpdateBlockAnnotationBiz;
import cn.malgo.annotation.controller.BaseController;
import cn.malgo.annotation.request.ListOverlapEntityRequest;
import cn.malgo.annotation.request.block.ListRelevanceAnnotationRequest;
import cn.malgo.annotation.request.block.ResetAnnotationBlockRequest;
import cn.malgo.annotation.request.brat.AddAnnotationGroupRequest;
import cn.malgo.annotation.request.brat.DeleteAnnotationGroupRequest;
import cn.malgo.annotation.request.brat.GetAutoAnnotationRequest;
import cn.malgo.annotation.request.brat.UpdateAnnotationGroupRequest;
import cn.malgo.annotation.result.PageVO;
import cn.malgo.annotation.vo.AnnotationBlockBratVO;
import cn.malgo.annotation.vo.ResetBlockToAnnotationResponse;
import cn.malgo.service.model.Response;
import cn.malgo.service.model.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v2/block")
public class AnnotationTaskBlockController extends BaseController {

  private final AnnotationBlockResetToAnnotationBiz annotationBlockResetToAnnotationBiz;
  private final GetAnnotationBlockBiz getAnnotationBlockBiz;
  private final AddBlockAnnotationBiz addBlockAnnotationBiz;
  private final DeleteBlockAnnotationBiz deleteBlockAnnotationBiz;
  private final UpdateBlockAnnotationBiz updateBlockAnnotationBiz;
  private final ListRelevanceAnnotationBiz listRelevanceAnnotationBiz;
  private final ListOverlapEntityBiz listOverlapEntityBiz;

  public AnnotationTaskBlockController(
      AnnotationBlockResetToAnnotationBiz annotationBlockResetToAnnotationBiz,
      GetAnnotationBlockBiz getAnnotationBlockBiz,
      AddBlockAnnotationBiz addBlockAnnotationBiz,
      DeleteBlockAnnotationBiz deleteBlockAnnotationBiz,
      UpdateBlockAnnotationBiz updateBlockAnnotationBiz,
      ListRelevanceAnnotationBiz listRelevanceAnnotationBiz,
      ListOverlapEntityBiz listOverlapEntityBiz) {
    this.annotationBlockResetToAnnotationBiz = annotationBlockResetToAnnotationBiz;
    this.getAnnotationBlockBiz = getAnnotationBlockBiz;
    this.addBlockAnnotationBiz = addBlockAnnotationBiz;
    this.deleteBlockAnnotationBiz = deleteBlockAnnotationBiz;
    this.updateBlockAnnotationBiz = updateBlockAnnotationBiz;
    this.listRelevanceAnnotationBiz = listRelevanceAnnotationBiz;
    this.listOverlapEntityBiz = listOverlapEntityBiz;
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
  public Response<PageVO<AnnotationBlockBratVO>> listBlockRelation(
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
}
