package cn.malgo.annotation.biz.brat;

import cn.malgo.annotation.dao.AnTypeRepository;
import cn.malgo.annotation.vo.AnTypeVO;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class ListAnTypeBiz extends BaseBiz<Object, List<AnTypeVO>> {
  private final AnTypeRepository anTypeRepository;

  public ListAnTypeBiz(AnTypeRepository anTypeRepository) {
    this.anTypeRepository = anTypeRepository;
  }

  @Override
  protected void validateRequest(Object o) throws InvalidInputException {}

  @Override
  protected List<AnTypeVO> doBiz(Object o) {
    return anTypeRepository.findAll(Sort.by(Sort.Direction.DESC, "createdTime")).stream()
        .map(AnTypeVO::new)
        .collect(Collectors.toList());
  }
}
