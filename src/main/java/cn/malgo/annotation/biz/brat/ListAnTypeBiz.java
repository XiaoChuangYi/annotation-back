package cn.malgo.annotation.biz.brat;

import cn.malgo.annotation.dao.AnTypeRepository;
import cn.malgo.annotation.entity.AnType;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListAnTypeBiz extends BaseBiz<Object, List<AnType>> {
  private final AnTypeRepository anTypeRepository;

  public ListAnTypeBiz(AnTypeRepository anTypeRepository) {
    this.anTypeRepository = anTypeRepository;
  }

  @Override
  protected void validateRequest(Object o) throws InvalidInputException {}

  @Override
  protected List<AnType> doBiz(Object o) {
    return anTypeRepository.findAll();
  }
}
