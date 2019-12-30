package cn.malgo.annotation.controller.block;

import cn.malgo.annotation.biz.block.AnnotationBlockExportEntityBiz;
import cn.malgo.annotation.biz.export.ExportStructuredDataBiz;
import cn.malgo.annotation.request.block.AnnotationBlockExportEntityRequest;
import cn.malgo.annotation.request.export.ExportStructuredDataReq;
import cn.malgo.service.exception.BusinessRuleException;
import cn.malgo.service.model.Response;
import com.google.common.io.Files;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(value = "/api/block")
public class BlockController {

  private final AnnotationBlockExportEntityBiz annotationBlockExportEntityBiz;
  private final ExportStructuredDataBiz exportStructuredDataBiz;

  public BlockController(
      final AnnotationBlockExportEntityBiz annotationBlockExportEntityBiz,
      final ExportStructuredDataBiz exportStructuredDataBiz) {
    this.annotationBlockExportEntityBiz = annotationBlockExportEntityBiz;
    this.exportStructuredDataBiz = exportStructuredDataBiz;
  }

  /** 导出entities */
  @RequestMapping(value = "/export-entities", method = RequestMethod.POST)
  public Response exportEntities(@RequestBody AnnotationBlockExportEntityRequest request) {
    return new Response<>(annotationBlockExportEntityBiz.process(request, null));
  }

  // 导入结构化需要标注的数据
  @RequestMapping(value = "/export-structured", method = RequestMethod.POST)
  public Response exportStructured(@RequestParam("file") MultipartFile[] files) {
    final List<MultipartFile> fileList = Arrays.asList(files);
    for (MultipartFile file : fileList) {
      final String extension = Files.getFileExtension(file.getOriginalFilename());
      if (!StringUtils.equalsAnyIgnoreCase(extension, "json")) {
        throw new BusinessRuleException("invalid-files", "上传文件格式不正确，非json格式文件");
      }
    }
    return new Response<>(exportStructuredDataBiz.process(new ExportStructuredDataReq(fileList)));
  }
}
