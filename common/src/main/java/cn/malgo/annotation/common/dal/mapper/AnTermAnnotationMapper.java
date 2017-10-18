package cn.malgo.annotation.common.dal.mapper;

import cn.malgo.annotation.common.dal.model.AnTermAnnotation;
import cn.malgo.annotation.common.dal.util.CommonMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AnTermAnnotationMapper extends CommonMapper<AnTermAnnotation> {

    /**
     * 根据术语状态查询术语
     * @param state
     * @return
     */
    List<AnTermAnnotation> selectByState(@Param("state") String state);

    /**
     * 通过TermId 查询标注信息
     * @param termId
     * @return
     */
    AnTermAnnotation selectByTermId(@Param("termId") String termId);

}