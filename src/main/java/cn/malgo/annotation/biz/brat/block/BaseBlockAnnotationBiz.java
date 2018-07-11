package cn.malgo.annotation.biz.brat.block;

import cn.malgo.annotation.biz.base.BaseBiz;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.brat.BaseAnnotationRequest;
import java.util.Optional;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public abstract class BaseBlockAnnotationBiz<
        REQ extends BaseAnnotationRequest, AnnotationBlockBratVO>
    extends BaseBiz<REQ, AnnotationBlockBratVO> {

  @Resource private AnnotationTaskBlockRepository annotationTaskBlockRepository;

  @Override
  protected void validateRequest(REQ req) throws InvalidInputException {
    if (req.getId() <= 0) {
      throw new InvalidInputException("invalid-id", "无效的id");
    }
  }

  @Override
  protected AnnotationBlockBratVO doBiz(int userId, int role, REQ req) {
    Optional<AnnotationTaskBlock> optional = annotationTaskBlockRepository.findById(req.getId());
    if (optional.isPresent()) {
      return this.doInternalProcess(role, optional.get(), req);
    }
    return null;
  }

  abstract AnnotationBlockBratVO doInternalProcess(
      int role, AnnotationTaskBlock annotationTaskBlock, REQ req);
}
