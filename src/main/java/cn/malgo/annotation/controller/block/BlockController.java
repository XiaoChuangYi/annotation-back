package cn.malgo.annotation.controller.block;

import cn.malgo.annotation.biz.block.AnnotationBlockExportEntityBiz;
import cn.malgo.annotation.request.block.AnnotationBlockExportEntityRequest;
import cn.malgo.service.model.Response;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/block")
public class BlockController {

  private final AnnotationBlockExportEntityBiz annotationBlockExportEntityBiz;

  public BlockController(final AnnotationBlockExportEntityBiz annotationBlockExportEntityBiz) {
    this.annotationBlockExportEntityBiz = annotationBlockExportEntityBiz;
  }

  /** 导出entities */
  @RequestMapping(value = "/export-entities", method = RequestMethod.POST)
  public Response exportEntities(@RequestBody AnnotationBlockExportEntityRequest request) {
    return new Response<>(annotationBlockExportEntityBiz.process(request, null));
  }
}
