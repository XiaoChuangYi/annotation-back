package com.microservice.dataAccessLayer.dynamicSql;

import com.microservice.dataAccessLayer.entity.AnnotationSentenceExercise;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;



/**
 * Created by cjl on 2018/5/3.
 */
public class AnnotationSentenceExerciseDynamicSqlProvider {



    public String queryAnnotationSentExerciseSelective(final AnnotationSentenceExercise annotationSentenceExercise){
        return new SQL(){
            {
                SELECT("*");
                FROM("annotation_sentence_exercises");
                WHERE("1=1");
                if(StringUtils.isNotBlank(annotationSentenceExercise.getMemo()))
                    WHERE("memo=#{memo}");
                if(StringUtils.isNotBlank((annotationSentenceExercise.getOriginText())))
                    WHERE("origin_text like concat('%',#{originText},'%')");
                if(StringUtils.isNotBlank(annotationSentenceExercise.getAutoAnnotation()))
                    WHERE("practice_annotation=#{practiceAnnotation}");
                if(StringUtils.isNotBlank(annotationSentenceExercise.getStandardAnnotation()))
                    WHERE("standard_annotation=#{standardAnnotation}");
                if(annotationSentenceExercise.getId()>0)
                    WHERE("id=#{id}");
                if(annotationSentenceExercise.getGmtCreated()!=null)
                    WHERE("gmt_created=#{gmtCreated}");
            }
        }.toString();
    }

    public String updateAnnotationSentExercises(final AnnotationSentenceExercise annotationSentenceExercise){
        return new SQL(){
            {
                UPDATE("annotation_sentence_exercises");
                if(annotationSentenceExercise.getStandardAnnotation()!=null)
                    SET("standard_annotation=#{standardAnnotation}");
                if(annotationSentenceExercise.getAutoAnnotation()!=null)
                    SET("auto_annotation=#{autoAnnotation}");
                if(StringUtils.isNotBlank(annotationSentenceExercise.getOriginText()))
                    SET("origin_text=#{originText}");
                if(StringUtils.isNotBlank(annotationSentenceExercise.getMemo()))
                    SET("memo=#{memo}");
                if(annotationSentenceExercise.getGmtCreated()!=null)
                    SET("gmt_created=#{gmtCreated}");
                if(annotationSentenceExercise.getGmtModified()!=null)
                    SET("gmt_modified=#{gmtModified}");
                WHERE("id=#{id}");
            }
        }.toString();
    }


}
