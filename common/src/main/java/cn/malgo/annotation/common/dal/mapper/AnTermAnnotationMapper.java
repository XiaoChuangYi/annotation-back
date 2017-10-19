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
     * 通过TermId 查询标注信息
     * @param termId
     * @return
     */
    AnTermAnnotation selectByTermId(@Param("termId") String termId);

}