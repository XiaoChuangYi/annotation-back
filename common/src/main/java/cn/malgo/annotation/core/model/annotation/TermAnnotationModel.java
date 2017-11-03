package cn.malgo.annotation.core.model.annotation;

import cn.malgo.annotation.common.util.AssertUtil;

/**
 * @author 张钟
 * @date 2017/10/31
 */
public class TermAnnotationModel {

    /** 标注标签 **/
    private String tag;

    /** 标注中的术语 **/
    private String term;

    /** 标注的类型 **/
    private String type;

    /** 起始位置 **/
    private int    startPosition;

    /** 结束位置 **/
    private int    endPosition;

    public static boolean isConfirmed(TermAnnotationModel termAnnotationModel){
        if(termAnnotationModel.getType().contains("-unconfirmed")){
            return false;
        }
        return true;
    }


    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }

    public int getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(int endPosition) {
        this.endPosition = endPosition;
    }
}
