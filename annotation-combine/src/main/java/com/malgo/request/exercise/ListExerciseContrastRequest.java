package com.malgo.request.exercise;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by cjl on 2018/6/4.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListExerciseContrastRequest {
    private int pageIndex;
    private int pageSize;
    private int userId;
}
