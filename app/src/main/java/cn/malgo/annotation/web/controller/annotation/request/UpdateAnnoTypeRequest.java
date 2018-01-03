package cn.malgo.annotation.web.controller.annotation.request;

import cn.malgo.annotation.common.util.AssertUtil;


/**
 * Created by cjl on 2018/1/2.
 */
public class UpdateAnnoTypeRequest {
    /** 标注ID **/
    private String anId;

    /** 标签 **/
    private String tag;

    /**新类型**/
    private String newType;

    private String oldType;

    public static void check(UpdateAnnoTypeRequest request){
        AssertUtil.notNull(request,"更新标注类型请求为空");
        AssertUtil.notBlank(request.getAnId(),"标注ID为空");
        AssertUtil.notBlank(request.getTag(),"标签为空");
        AssertUtil.notBlank(request.getNewType(),"新类型为空");

    }

    public String getOldType() {
        return oldType;
    }

    public void setOldType(String oldType) {
        this.oldType = oldType;
    }

    public String getAnId() {
        return anId;
    }

    public void setAnId(String anId) {
        this.anId = anId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getNewType() {
        return newType;
    }

    public void setNewType(String newType) {
        this.newType = newType;
    }
}
