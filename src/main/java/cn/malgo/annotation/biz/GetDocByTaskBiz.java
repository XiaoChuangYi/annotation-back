package cn.malgo.annotation.biz;

import cn.malgo.annotation.biz.base.BaseBiz;
import cn.malgo.annotation.dao.AnnotationTaskDocRepository;
import cn.malgo.annotation.dao.OriginalDocRepository;
import cn.malgo.annotation.entity.OriginalDoc;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.GetDocByTaskRequest;
import cn.malgo.annotation.vo.OriginalDocListVO;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GetDocByTaskBiz extends BaseBiz<GetDocByTaskRequest,OriginalDocListVO> {

  private final AnnotationTaskDocRepository annotationTaskDocRepository;
  private final OriginalDocRepository originalDocRepository;

  public GetDocByTaskBiz(AnnotationTaskDocRepository annotationTaskDocRepository
      , OriginalDocRepository originalDocRepository) {
    this.annotationTaskDocRepository = annotationTaskDocRepository;
    this.originalDocRepository = originalDocRepository;
  }

  @Override
  protected void validateRequest(GetDocByTaskRequest getDocByTaskRequest)
      throws InvalidInputException {
    if (getDocByTaskRequest == null) {
      throw new InvalidInputException("invalid-request", "无效的请求");
    }
  }

  @Override
  protected OriginalDocListVO doBiz(GetDocByTaskRequest getDocByTaskRequest) {
    List<Integer> docIdList =
        annotationTaskDocRepository.findDocIdByTaskId(getDocByTaskRequest.getTaskId());

    List<OriginalDoc> originalDocList =
        docIdList.size() != 0 ? originalDocRepository.findAllById(docIdList) : new ArrayList<>();

    return new OriginalDocListVO(docIdList.size(), originalDocList);
  }
}
