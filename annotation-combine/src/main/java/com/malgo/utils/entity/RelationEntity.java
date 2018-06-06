package com.malgo.utils.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by cjl on 2018/5/24.
 */
@Data
@AllArgsConstructor
public class RelationEntity {
    private String tag;
    private String type;
    private String sourceTag;
    private String targetTag;
    private String source;
    private String target;
}
