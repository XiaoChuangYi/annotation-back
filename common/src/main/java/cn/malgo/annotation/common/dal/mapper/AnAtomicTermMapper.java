package cn.malgo.annotation.common.dal.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.malgo.annotation.common.dal.model.AnAtomicTerm;
import cn.malgo.annotation.common.dal.util.CommonMapper;

public interface AnAtomicTermMapper extends CommonMapper<AnAtomicTerm> {

    /**
     * 根据原子术语的内容查询术语
     * @param term
     * @return
     */
    List<AnAtomicTerm> selectByTerm(@Param("term") String term);

    /**
     * 查询全部原子术语
     * @param term
     * @param type
     * @return
     */
    List<AnAtomicTerm> selectByTermAndType(@Param("term") String term, @Param("type") String type);

    /**
     * 查询全部原子术语
     * @param term
     * @param type
     * @return
     */
    AnAtomicTerm selectByTermAndTypeNotNull(@Param("term") String term, @Param("type") String type);

    /**
     * 查询全部原子术语
     * @param state
     * @return
     */
    List<AnAtomicTerm> selectByState(@Param("state") String state);

    /**
     * 模糊查询全部原子术语
     * @param term
     * @param type
     * @return
     * */
    List<AnAtomicTerm> fuzzyQueryByTermAndType(@Param("term") String term,@Param("type") String type);

}