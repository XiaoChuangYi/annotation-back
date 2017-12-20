package cn.malgo.annotation.common.dal.mapper;

import cn.malgo.annotation.common.dal.model.MixtureTerm;
import cn.malgo.annotation.common.dal.model.Term;
import cn.malgo.annotation.common.dal.model.TermLabel;
import cn.malgo.annotation.common.dal.util.CommonMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TermMapper extends CommonMapper<Term> {
     List<Term> selectEnableTerm();
     List<Term> selectTermByCondition(@Param("termName") String termName,
                                      @Param("termType") String termType,
                                      @Param("label") String label,
                                      @Param("checked") String checked);
     List<MixtureTerm> selectAllByTermName(@Param("termName") String termName);
     List<String> selectTermType();
     int updateBatchLabelOfTerm(@Param("labelList") List<TermLabel> labelList);
     int updateBatchConceptIdOfTerm(@Param("idList") List<Integer> idList,@Param("conceptId") String conceptId);
     Term selectByPrimaryKeyID(@Param("id") int id);
     List<Term> selectByConceptId(@Param("conceptId") String conceptId);
}