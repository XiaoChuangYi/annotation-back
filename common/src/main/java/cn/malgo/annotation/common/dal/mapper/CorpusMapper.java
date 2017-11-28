package cn.malgo.annotation.common.dal.mapper;

import cn.malgo.annotation.common.dal.model.Corpus;
import cn.malgo.annotation.common.dal.util.CommonMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CorpusMapper extends CommonMapper<Corpus> {

    /**
     * 通过状态查询术语
     * @param state
     * @return
     */
    List<Corpus> selectByState(@Param("state") String state);
    /**
     * 批量更新术语表中的type
     * @param idsList
     * @param type
     */
    int batchUpdateAnTermType(@Param("idsList") List<String> idsList,@Param("type") String type);

    /**
     * 根据type类型查询对应原子术语的ID集合
     * @param type
     */
    List<Corpus> selectAnTermIDsByOldType(@Param("type") String type);
}