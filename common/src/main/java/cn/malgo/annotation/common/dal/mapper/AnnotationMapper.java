package cn.malgo.annotation.common.dal.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.malgo.annotation.common.dal.model.Annotation;
import cn.malgo.annotation.common.dal.util.CommonMapper;

public interface AnnotationMapper extends CommonMapper<Annotation> {

    /**
     * 根据术语状态查询术语
     * @param state
     * @param modifier
     * @return
     */
    List<Annotation> selectByStateModifier(@Param("state") String state,
                                           @Param("modifier") String modifier);

    /**
     * 根据术语状态和修改人查询术语
     * @param stateList
     * @param modifier
     * @return
     */
    List<Annotation> selectByStateListModifier(@Param("stateList") List<String> stateList,
                                               @Param("modifier") String modifier);
    /**
     * 根据术语状态查询术语
     * @param stateList
     * @return
     */
    List<Annotation> selectByStateList(@Param("stateList") List<String> stateList);


    /**
     * 通过TermId 查询标注信息
     * @param termId
     * @return
     */
    Annotation selectByTermId(@Param("termId") String termId);


    /**
     * 批量更新最终标注
     * @param annotationList
     * @return
     */
    int batchUpdateFinalAnnotation(@Param("annotationList") List<Annotation> annotationList);

    /**
     * 批量更新最终标注和手动标注
     * @param annotationList
     */
    int batchUpdateFinalAndManualAnnotation(@Param("annotationList") List<Annotation> annotationList);
    /**
     * 查询数据库中的总条数
     * */
    int selectTermAnnotationCount(@Param("state") String state);
}