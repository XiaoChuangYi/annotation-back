package com.microservice.vo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by cjl on 2018/4/11.
 */
public class BratConfigVO {
    private JSONObject ui_names;
    private JSONArray entity_types;
    private JSONArray relation_attribute_types;

    public JSONObject getUi_names() {
        return ui_names;
    }

    public void setUi_names(JSONObject ui_names) {
        this.ui_names = ui_names;
    }

    public JSONArray getEntity_types() {
        return entity_types;
    }

    public void setEntity_types(JSONArray entity_types) {
        this.entity_types = entity_types;
    }

    public JSONArray getRelation_attribute_types() {
        return relation_attribute_types;
    }

    public void setRelation_attribute_types(JSONArray relation_attribute_types) {
        this.relation_attribute_types = relation_attribute_types;
    }
}
