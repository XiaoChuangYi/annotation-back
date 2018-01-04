package cn.malgo.annotation.common.dal.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import cn.malgo.annotation.common.dal.model.Annotation;
import cn.malgo.annotation.common.dal.util.CommonMapper;

public interface AnnotationMapper extends CommonMapper<Annotation> {


    List<Annotation> selectByStateAndUserId(@Param("state") String state,@Param("userId") String userId);
    /**
     * 根据术语状态查询术语
     * @param state
     * @param modifier
     * @param term
     * @return
     */
    List<Annotation> selectByStateAndTermFuzzy(@Param("state") String state,
                                           @Param("modifier") String modifier,@Param("term") String term);


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
     * 根据术语状态查询术语
     * @param idList
     * @return
     */
    List<Annotation> selectByIdList(@Param("idList") List<String> idList);
    /**
     *根据状态后台实现分页查询
//     * @param state
//     * @param pageIndex
//     * @param pageSize
     */
    List<Annotation> selectFinalAnnotationByPagination(@Param("state") String state,@Param("start") int start,@Param("pageSize") int pageSize);

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

    /**
     * 批量更新标准表的用户ID'
     */
    int batchUpdateAnnotationUserId(@Param("idsList") List<String> idsList,@Param("modifier") String modifier);

    List<String> selectIDsByNum(@Param("state") String state,@Param("userId") String userId);
}