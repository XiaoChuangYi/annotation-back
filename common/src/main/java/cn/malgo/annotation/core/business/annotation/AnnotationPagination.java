package cn.malgo.annotation.core.business.annotation;

import java.util.List;

/**
 * Created by cjl on 2017/12/6.
 */
public class AnnotationPagination<T> {
    private  int lastIndex;
    private List<T> list;

    public int getLastIndex() {
        return lastIndex;
    }

    public void setLastIndex(int lastIndex) {
        this.lastIndex = lastIndex;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
