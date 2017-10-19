package cn.malgo.annotation.web.controller.annotation.request;

import cn.malgo.annotation.common.util.AssertUtil;
import cn.malgo.annotation.core.model.enums.annotation.AnnotationStateEnum;
import cn.malgo.annotation.web.request.PageRequest;
import org.apache.commons.lang.StringUtils;

/**
 * Created by 张钟 on 2017/10/19.
 */
public class AnnotationQueryRequest extends PageRequest{

    /** 用户ID **/
    private String userId;

    /** 标注状态 **/
    private String state;


    public static void check(AnnotationQueryRequest request){
        AssertUtil.notNull(request,"查询标注的请求对象为空");
        AssertUtil.notBlank(request.getUserId(),"用户ID为空");
        if(StringUtils.isNotBlank(request.getState())){
           AnnotationStateEnum annotationStateEnum =  AnnotationStateEnum.valueOf(request.getState());
           AssertUtil.notNull(annotationStateEnum,"标注状态不支持");
        }
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
