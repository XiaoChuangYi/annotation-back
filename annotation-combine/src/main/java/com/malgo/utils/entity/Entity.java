package com.malgo.utils.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by cjl on 2018/5/24.
 */
@Data
@AllArgsConstructor
public class Entity implements Serializable{
    private String tag;
    private int start;
    private int end;
    private String type;
    private String term;

}
