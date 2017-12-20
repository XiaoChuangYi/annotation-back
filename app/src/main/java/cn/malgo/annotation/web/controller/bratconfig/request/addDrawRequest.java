package cn.malgo.annotation.web.controller.bratconfig.request;

import cn.malgo.annotation.common.util.AssertUtil;

/**
 * Created by cjl on 2017/12/20.
 */
public class addDrawRequest {
    private  int id;
    private String drawName;
    private String typeLabel;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDrawName() {
        return drawName;
    }

    public void setDrawName(String drawName) {
        this.drawName = drawName;
    }

    public String getTypeLabel() {
        return typeLabel;
    }

    public void setTypeLabel(String typeLabel) {
        this.typeLabel = typeLabel;
    }
    public  static  void check(addDrawRequest request){
        AssertUtil.notNull(request,"新增单条术语请求对象为空");
        AssertUtil.notBlank(request.getDrawName(),"渲染参数为空");
        AssertUtil.notBlank(request.getTypeLabel(),"类型别名为空");
        AssertUtil.notNull(request.getId(),"id为空");
    }
}
