package cn.malgo.annotation.biz.block;

import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.request.block.AnnotationBlockExportEntityRequest;
import cn.malgo.annotation.vo.BlockExportEntityVO;
import cn.malgo.annotation.vo.BlockExportEntityVO.EntityInfo;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
public class AnnotationBlockExportEntityBiz
    extends BaseBiz<AnnotationBlockExportEntityRequest, BlockExportEntityVO> {

  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;

  public AnnotationBlockExportEntityBiz(
      final AnnotationTaskBlockRepository annotationTaskBlockRepository) {
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
  }

  @Override
  protected void validateRequest(AnnotationBlockExportEntityRequest request)
      throws InvalidInputException {
    if (request.getAnnotationType().intValue() < 1) {
      throw new InvalidInputException("invalid-annotation-type", "无效的参数annotationType");
    }
  }

  @Override
  protected BlockExportEntityVO doBiz(
      AnnotationBlockExportEntityRequest request, UserDetails user) {
    final Page<AnnotationTaskBlock> page =
        annotationTaskBlockRepository.findAllByAnnotationTypeAndStateIn(
            AnnotationTypeEnum.getByValue(request.getAnnotationType()),
            Arrays.asList(AnnotationTaskState.FINISHED),
            PageRequest.of(0, Integer.MAX_VALUE));
    return new BlockExportEntityVO(
        page.getContent().parallelStream().map(EntityInfo::new).collect(Collectors.toList()));
  }
}
