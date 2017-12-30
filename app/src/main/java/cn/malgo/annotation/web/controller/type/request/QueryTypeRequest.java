package cn.malgo.annotation.web.controller.type.request;

import cn.malgo.annotation.web.request.PageRequest;

/**
 * Created by cjl on 2017/12/22.
 */
public class QueryTypeRequest extends PageRequest {
    private String typeCode;
    private String typeName;

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
