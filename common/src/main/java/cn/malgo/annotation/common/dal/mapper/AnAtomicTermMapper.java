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
    List<AnAtomicTerm> selectByTermAndTypeIsSynonyms(@Param("term") String term, @Param("type") String type,@Param("id") String id,@Param("checked") String checked);

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
     * @return
     */
    AnAtomicTerm selectByTermAndTypeNotNull(@Param("term") String term,@Param("type") String type);

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
     */
    List<AnAtomicTerm> fuzzyQueryByTermAndType(@Param("term") String term,@Param("type") String type);

    /**
     * 根据type类型查询对应原子术语的ID集合
     * @param type
     */
    List<AnAtomicTerm> selectAtomicIDsByOldType(@Param("type") String type);

    /**
     * 批量更新原子术语表中的type
     * @param idsList
     * @param type
     */
    int batchUpdateAtomicType(@Param("idsList") List<String> idsList,@Param("type") String type);

    /**
     * 批量更新原子术语表中的type
     * @param idsList
     * @param conceptId
     */
    int batchUpdateAtomicConceptId(@Param("idsList") List<String> idsList,@Param("conceptId") String conceptId);

    /**
     *更新原子术语表里的conceptId
     */
    int updateConceptIdByPrimaryKey(@Param("conceptId") String conceptId,@Param("id") String id);
    /**
     *查询总数
     */
    int selectTotalByChecked(@Param("checked") String checked);
    /**
     *带条件查询
     */
    List<AnAtomicTerm> selectAllByCondition(@Param("checked") String checked);
    /**
     *查询全部
     */
    List<AnAtomicTerm> selectAllAtomicTerm();
    /**
     *根据主键ID查询单条记录
     */
    AnAtomicTerm selectByPrimaryKeyID(@Param("id") String id);

    List<AnAtomicTerm> selectAllByConceptId(@Param("conceptId") String conceptId);


}