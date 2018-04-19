package com.microservice.vo;

import java.util.List;

/**
 * Created by cjl on 2018/4/12.
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
