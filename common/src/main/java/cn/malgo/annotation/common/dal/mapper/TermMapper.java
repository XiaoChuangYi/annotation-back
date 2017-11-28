package cn.malgo.annotation.common.dal.mapper;

import cn.malgo.annotation.common.dal.model.Term;
import cn.malgo.annotation.common.dal.util.CommonMapper;

import java.util.List;

public interface TermMapper extends CommonMapper<Term> {
     List<Term> selectEnableTerm();
}