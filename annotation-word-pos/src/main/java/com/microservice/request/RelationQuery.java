package com.microservice.request;

/**
 * Created by cjl on 2018/5/24.
 */
public class RelationQuery {

    private int pageIndex;
    private int pageSize;

    public int getPageIndex() {
        return pageIndex==0?1:pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize==0?10:pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
