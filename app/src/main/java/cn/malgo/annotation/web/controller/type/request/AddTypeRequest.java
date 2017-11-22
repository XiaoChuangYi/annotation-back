package cn.malgo.annotation.web.controller.type.request;

import cn.malgo.annotation.common.util.AssertUtil;


/**
 * Created by cjl on 2017/11/20.
 */
public class AddTypeRequest {
    private String typeCode;
    private String typeName;
    private String parentId;

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

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
    public static void check(AddTypeRequest request){
        AssertUtil.notNull(request,"新增类型请求为空");
        AssertUtil.notBlank(request.getTypeCode(),"类型编码为空");
        AssertUtil.notBlank(request.getTypeName(),"类型名称为空");
    }
}
