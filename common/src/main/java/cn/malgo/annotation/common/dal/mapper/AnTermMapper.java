package cn.malgo.annotation.common.dal.mapper;

import cn.malgo.annotation.common.dal.model.AnTerm;
import cn.malgo.annotation.common.dal.util.CommonMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AnTermMapper extends CommonMapper<AnTerm> {

    /**
     * 通过状态查询术语
     * @param state
     * @return
     */
    List<AnTerm> selectByState(@Param("state") String state);
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
    List<AnTerm> selectAnTermIDsByOldType(@Param("type") String type);
}