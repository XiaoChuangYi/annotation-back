package cn.malgo.annotation.web.controller.type.request;

import cn.malgo.annotation.common.util.AssertUtil;

/**
 * Created by cjl on 2017/11/20.
 */
public class UpdateTypeRequest {
    private String id;
    private String  typeOld;
    private String typeNew;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTypeOld() {
        return typeOld;
    }

    public void setTypeOld(String typeOld) {
        this.typeOld = typeOld;
    }

    public String getTypeNew() {
        return typeNew;
    }

    public void setTypeNew(String typeNew) {
        this.typeNew = typeNew;
    }

    public static void check(UpdateTypeRequest request){
        AssertUtil.notNull(request,"更新类型请求为空");
        AssertUtil.notBlank(request.getId(),"类型id为空");
        AssertUtil.notBlank(request.getTypeNew(),"类型typeNew为空");
        AssertUtil.notBlank(request.getTypeOld(),"类型typeOld为空");
    }
}
