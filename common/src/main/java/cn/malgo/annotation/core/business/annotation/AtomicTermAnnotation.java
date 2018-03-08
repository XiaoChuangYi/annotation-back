package cn.malgo.annotation.core.business.annotation;

/**
 * Created by cjl on 2017/12/26.
 */
public class AtomicTermAnnotation {
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

    /**选项 新词 ,手工标注**/
    private String option;

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
