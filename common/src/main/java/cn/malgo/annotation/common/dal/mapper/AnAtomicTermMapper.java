package cn.malgo.annotation.common.dal.mapper;

import cn.malgo.annotation.common.dal.model.AnAtomicTerm;
import cn.malgo.annotation.common.dal.util.CommonMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AnAtomicTermMapper extends CommonMapper<AnAtomicTerm> {

    /**
     * 根据原子术语的内容查询术语
     * @param term
     * @return
     */
    AnAtomicTerm selectByTerm(@Param("term") String term);

    /**
     * 查询全部原子术语
     * @return
     */
    List<AnAtomicTerm> selectByState(@Param("state") String state);
}