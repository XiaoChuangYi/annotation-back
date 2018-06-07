package com.malgo.biz.brat;

import com.malgo.biz.BaseBiz;
import com.malgo.dao.AnTypeRepository;
import com.malgo.entity.AnType;
import com.malgo.exception.BusinessRuleException;
import com.malgo.exception.InvalidInputException;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Created by cjl on 2018/6/5.
 */
@Component
public class ListAnTypeBiz extends BaseBiz<Object,List<AnType>> {

  private final AnTypeRepository anTypeRepository;

  public ListAnTypeBiz(AnTypeRepository anTypeRepository){
    this.anTypeRepository=anTypeRepository;
  }

  @Override
  protected void validateRequest(Object o) throws InvalidInputException {

  }

  @Override
  protected void authorize(int userId, int role, Object o) throws BusinessRuleException {

  }

  @Override
  protected List<AnType> doBiz(Object o) {
    return anTypeRepository.findAll();
  }
}
