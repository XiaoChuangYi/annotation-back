package com.malgo.vo;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * Created by cjl on 2018/5/24.
 */
@Data
public class AnnotationSentenceBratVO {
    private int     id;

    private String     state;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtCreated;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtModified;

    private String      userModifier;

    private String   memo;

    private JSONObject bratData;

    private JSONObject finalBratData;
}
