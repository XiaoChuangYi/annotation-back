package cn.malgo.annotation.common.dal.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.malgo.annotation.common.dal.model.AnTermAnnotation;
import cn.malgo.annotation.common.dal.util.CommonMapper;

public interface AnTermAnnotationMapper extends CommonMapper<AnTermAnnotation> {

    /**
     * 根据术语状态查询术语
     * @param state
     * @param modifier
     * @return
     */
    List<AnTermAnnotation> selectByStateModifier(@Param("state") String state,
                                                 @Param("modifier") String modifier);

    /**
     * 根据术语状态查询术语
     * @param stateList
     * @param modifier
     * @return
     */
    List<AnTermAnnotation> selectByStateListModifier(@Param("stateList") List<String> stateList,
                                                 @Param("modifier") String modifier);

    /**
     * 通过TermId 查询标注信息
     * @param termId
     * @return
     */
    AnTermAnnotation selectByTermId(@Param("termId") String termId);


    /**
     * 批量更新最终标注
     * @param anTermAnnotationList
     * @return
     */
    int batchUpdateFinalAnnotation(@Param("anTermAnnotationList") List<AnTermAnnotation> anTermAnnotationList);

}