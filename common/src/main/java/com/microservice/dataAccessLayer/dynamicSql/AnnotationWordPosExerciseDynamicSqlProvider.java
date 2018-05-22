package com.microservice.dataAccessLayer.dynamicSql;

import com.microservice.dataAccessLayer.entity.AnnotationWordPosExercise;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

/**
 * Created by cjl on 2018/5/17.
 */
public class AnnotationWordPosExerciseDynamicSqlProvider {

        public String listAnnotationWordExerciseByCondition(final AnnotationWordPosExercise annotationWordPosExercise){
            return new SQL(){
                {
                    SELECT("*");
                    FROM("annotation_word_exercise");
                    WHERE("1=1");
                    if(StringUtils.isNotBlank(annotationWordPosExercise.getMemo()))
                        WHERE("memo=#{memo}");
                    if(StringUtils.isNotBlank((annotationWordPosExercise.getOriginText())))
                        WHERE("origin_text like concat('%',#{originText},'%')");
                    if(StringUtils.isNotBlank(annotationWordPosExercise.getAutoAnnotation()))
                        WHERE("practice_annotation=#{practiceAnnotation}");
                    if(StringUtils.isNotBlank(annotationWordPosExercise.getStandardAnnotation()))
                        WHERE("standard_annotation=#{standardAnnotation}");
                    if(annotationWordPosExercise.getId()>0)
                        WHERE("id=#{id}");
                    if(annotationWordPosExercise.getGmtCreated()!=null)
                        WHERE("gmt_created=#{gmtCreated}");
                }
            }.toString();
        }

        public String updateAnnotationWordExercise(final AnnotationWordPosExercise annotationWordPosExercise){
            return new SQL(){
                {
                    UPDATE("annotation_word_exercise");
                    if(annotationWordPosExercise.getStandardAnnotation()!=null)
                        SET("standard_annotation=#{standardAnnotation}");
                    if(annotationWordPosExercise.getAutoAnnotation()!=null)
                        SET("auto_annotation=#{autoAnnotation}");
                    if(StringUtils.isNotBlank(annotationWordPosExercise.getOriginText()))
                        SET("origin_text=#{originText}");
                    if(StringUtils.isNotBlank(annotationWordPosExercise.getMemo()))
                        SET("memo=#{memo}");
                    if(annotationWordPosExercise.getGmtCreated()!=null)
                        SET("gmt_created=#{gmtCreated}");
                    if(annotationWordPosExercise.getGmtModified()!=null)
                        SET("gmt_modified=#{gmtModified}");
                    WHERE("id=#{id}");
                }
            }.toString();
        }
}
