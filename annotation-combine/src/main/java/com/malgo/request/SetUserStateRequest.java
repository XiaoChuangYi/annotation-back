package com.malgo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by cjl on 2018/5/30.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetUserStateRequest{
    private int userId;
    private String currentState;
}
