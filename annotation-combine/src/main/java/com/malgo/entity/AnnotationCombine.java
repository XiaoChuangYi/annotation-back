package com.malgo.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by cjl on 2018/5/29.
 */
@Entity
@Table(name="annotation_combine")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnnotationCombine extends BaseEntity{


    private String finalAnnotation;
    private String manualAnnotation;
    private String reviewedAnnotation;
    private String state;
    private int isTask;

}
