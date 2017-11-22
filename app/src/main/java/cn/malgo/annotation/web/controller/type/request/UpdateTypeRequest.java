package cn.malgo.annotation.web.controller.type.request;

import cn.malgo.annotation.common.util.AssertUtil;

/**
 * Created by cjl on 2017/11/20.
 */
public class UpdateTypeRequest {
    private String  typeId;
    private String typeName;
    private String parentId;

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
    public static void check(UpdateTypeRequest request){
        AssertUtil.notNull(request,"更新类型请求为空");
        AssertUtil.notBlank(request.getTypeId(),"类型TypeId为空");
        AssertUtil.notBlank(request.getTypeName(),"类型TypeName为空");
    }
}
