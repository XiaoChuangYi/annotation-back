package cn.malgo.annotation.web.controller.annotation.request;

import cn.malgo.annotation.common.util.AssertUtil;
import cn.malgo.annotation.common.util.exception.BaseRuntimeException;
import cn.malgo.annotation.core.model.enums.BaseResultCodeEnum;
import cn.malgo.annotation.core.model.enums.annotation.AnnotationOptionEnum;

/**
 * @author 张钟
 * @date 2017/10/25
 */
public class AddAnnotationRequest {

    /** 标注ID **/
    private String anId;

    /** 标注文本 **/
    private String text;

    /** 起始位置 **/
    private String startPosition;

    /** 结束位置 **/
    private String endPosition;

    /** 标注类型 **/
    private String annotationType;

    /**
     * 选项 新词 ,手工标注
     * @ cn.malgo.annotation.core.model.enums.annotation.AnnotationOptionEnum
     * **/
    private String option;

    public static void check(AddAnnotationRequest request){
        AssertUtil.notNull(request,"新增标注请求为空");
        AssertUtil.notBlank(request.getAnId(),"标注ID为空");
        AssertUtil.notBlank(request.getText(),"标注文本为空");
        AssertUtil.notBlank(request.getStartPosition(),"起始位置为空");
        AssertUtil.notBlank(request.getEndPosition(),"结束位置为空");
        AssertUtil.notBlank(request.getAnnotationType(),"标注类型为空");
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(String startPosition) {
        this.startPosition = startPosition;
    }

    public String getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(String endPosition) {
        this.endPosition = endPosition;
    }

    public String getAnnotationType() {
        return annotationType;
    }

    public void setAnnotationType(String annotationType) {
        this.annotationType = annotationType;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }
}
