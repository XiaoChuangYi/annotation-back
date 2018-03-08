package cn.malgo.annotation.common.dal.mapper;

import cn.malgo.annotation.core.business.term.GroupTerm;
import cn.malgo.annotation.core.business.mixtrue.MixtureTerm;
import cn.malgo.annotation.common.dal.model.Term;
import cn.malgo.annotation.core.business.term.TermLabel;
import cn.malgo.annotation.common.dal.util.CommonMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TermMapper extends CommonMapper<Term> {
     List<Term> selectEnableTerm();
     List<Term> selectTermByCondition(@Param("termName") String termName,
                                      @Param("termType") String termType,
                                      @Param("label") String label,
                                      @Param("checked") String checked,
                                      @Param("originName") String originName);
     List<MixtureTerm> selectAllByTermName(@Param("termName") String termName);
     List<String> selectTermType();
     int updateBatchLabelOfTerm(@Param("labelList") List<TermLabel> labelList);
     int updateBatchConceptIdOfTerm(@Param("idList") List<Integer> idList,@Param("conceptId") String conceptId);
     int coverBatchLabelOfTerm(@Param("idsList") List<Integer> idsList,@Param("label") String label);
     Term selectByPrimaryKeyID(@Param("id") int id);
     List<Term> selectByConceptId(@Param("conceptId") String conceptId);
     List<Term> selectTermByTermId(@Param("termId") String termId);
     int getGroupsByOriginName();
     List<Term> selectTermByOriginNameGroup();
     List<GroupTerm> selectGroupsAndOriginName(@Param("groupIndex") Integer groupIndex,@Param("groupSize") Integer groupSize);
     List<Term> selectTermsByOriginName(@Param("originName") String originName);
}