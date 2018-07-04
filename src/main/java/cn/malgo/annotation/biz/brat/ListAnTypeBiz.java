package cn.malgo.annotation.biz.brat;

import cn.malgo.annotation.biz.base.BaseBiz;
import cn.malgo.annotation.dao.AnTypeRepository;
import cn.malgo.annotation.entity.AnType;
import cn.malgo.annotation.exception.BusinessRuleException;
import cn.malgo.annotation.exception.InvalidInputException;
import java.util.List;
import org.springframework.stereotype.Component;

/** Created by cjl on 2018/6/5. */
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
