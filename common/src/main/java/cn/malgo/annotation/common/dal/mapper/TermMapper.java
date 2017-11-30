package cn.malgo.annotation.common.dal.mapper;

import cn.malgo.annotation.common.dal.model.Term;
import cn.malgo.annotation.common.dal.util.CommonMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TermMapper extends CommonMapper<Term> {
     List<Term> selectEnableTerm();
     List<Term> selectTermByCondition(@Param("termName") String termName,
                                      @Param("termType") String termType,
                                      @Param("label") String label);
}