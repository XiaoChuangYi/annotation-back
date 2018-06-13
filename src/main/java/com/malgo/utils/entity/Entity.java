package com.malgo.utils.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode
@AllArgsConstructor
public class Entity implements Serializable {
    private String tag;
    private int start;
    private int end;
    private String type;
    private String term;

}
