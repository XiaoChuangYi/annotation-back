package com.malgo.request.brat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by cjl on 2018/5/31.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddAnnotationRequest {
    private int id;
    private String term;
    private String annotationType;
    private int startPosition;
    private int endPosition;
    private String autoAnnotation;
}
