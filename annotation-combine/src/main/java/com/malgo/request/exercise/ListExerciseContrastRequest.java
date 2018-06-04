package com.malgo.request.exercise;

import lombok.Data;

/**
 * Created by cjl on 2018/6/4.
 */
@Data
public class ListExerciseContrastRequest {
    private int pageIndex;
    private int pageSize;
    private int userId;
}
