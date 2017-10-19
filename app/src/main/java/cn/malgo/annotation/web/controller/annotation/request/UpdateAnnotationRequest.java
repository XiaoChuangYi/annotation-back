package cn.malgo.annotation.web.controller.annotation.request;

import cn.malgo.annotation.common.service.integration.apiserver.vo.TermTypeVO;
import cn.malgo.annotation.common.util.AssertUtil;
import cn.malgo.annotation.web.request.PageRequest;

import java.util.List;

/**
 * Created by 张钟 on 2017/10/19.
 */
public class UpdateAnnotationRequest extends PageRequest {

    /**
     * 标注ID
     */
    private String id;

    /**
     * 修改人
     */
    private String userId;

    /**
     * 备注
     */
    private String memo;

    /**
     * 手工标注结果
     */
    private String manualAnnotation;

    /**
     * 新术语
     */
    private List<TermTypeVO>  newTerms;

    /**
     * 标注状态
     * 更新当前页面的其他标注时,需传入,更新单条标注,
     * 不需要刷新其他标注时,可以不传入
     */
    private String state;

    public static void check(UpdateAnnotationRequest request) {
        AssertUtil.notNull(request,"更新单条标注请求对象为空");
        AssertUtil.notBlank(request.getId(),"标注ID为空");
        AssertUtil.notBlank(request.getUserId(),"用户ID为空");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getManualAnnotation() {
        return manualAnnotation;
    }

    public void setManualAnnotation(String manualAnnotation) {
        this.manualAnnotation = manualAnnotation;
    }

    public List<TermTypeVO> getNewTerms() {
        return newTerms;
    }

    public void setNewTerms(List<TermTypeVO> newTerms) {
        this.newTerms = newTerms;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
