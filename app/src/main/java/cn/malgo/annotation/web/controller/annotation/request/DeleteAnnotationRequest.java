package cn.malgo.annotation.web.controller.annotation.request;

import cn.malgo.annotation.common.util.AssertUtil;
import cn.malgo.annotation.common.util.exception.BaseRuntimeException;
import cn.malgo.annotation.core.tool.enums.BaseResultCodeEnum;
import cn.malgo.annotation.core.tool.enums.annotation.AnnotationOptionEnum;

/**
 * @author 张钟
 * @date 2017/10/25
 */
public class DeleteAnnotationRequest {

    /** 标注ID **/
    private String anId;

    /** 标签 **/
    private String tag;

    /**
     * 选项 新词 ,手工标注
     * @ cn.malgo.annotation.core.tool.enums.annotation.AnnotationOptionEnum
     * **/
    private String option;

    public static void check(DeleteAnnotationRequest request){
        AssertUtil.notNull(request,"新增标注请求为空");
        AssertUtil.notBlank(request.getAnId(),"标注ID为空");
        AssertUtil.notBlank(request.getTag(),"标签为空");
        AssertUtil.notBlank(request.getOption(),"选项为空");
        try{
            AnnotationOptionEnum.valueOf(request.getOption());
        }catch (Exception e){
            throw new BaseRuntimeException(BaseResultCodeEnum.ILLEGAL_ARGUMENT, "标注选项类型有误");
        }
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

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }
}
