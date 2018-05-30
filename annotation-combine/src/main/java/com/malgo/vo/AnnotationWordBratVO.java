package com.malgo.vo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.Date;

/**
 * Created by cjl on 2018/5/24.
 */
@Data
public class AnnotationWordBratVO {
    private String     id;

    private String     state;

    private Date gmtCreated;

    private Date       gmtModified;

    private String      modifier;

    private JSONArray memo;

    private JSONArray  newTerms;

    private JSONObject bratData;
}
