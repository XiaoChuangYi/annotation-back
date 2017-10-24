package cn.malgo.annotation.web.controller.annotation.request;

import cn.malgo.annotation.common.service.integration.apiserver.vo.TermTypeVO;
import cn.malgo.annotation.common.util.AssertUtil;
import cn.malgo.annotation.web.request.PageRequest;

import java.util.List;

/**
 *
 * @author 张钟
 * @date 2017/10/19
 */
public class UpdateAnnotationRequest extends PageRequest {

    /**
     * 标注ID
     */
    private String anId;

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
        AssertUtil.notBlank(request.getAnId(),"标注ID为空");
    }

    public String getAnId() {
        return anId;
    }

    public void setAnId(String anId) {
        this.anId = anId;
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
