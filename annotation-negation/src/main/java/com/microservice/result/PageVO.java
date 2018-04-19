package com.microservice.result;

import com.github.pagehelper.Page;

import java.util.List;

/**
 * Created by cjl on 2018/4/16.
 */
public class PageVO<T> {
    private long total;
    private List<T> dataList;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }

    public PageVO(Page<T> page) {
        this.setTotal(page.getTotal());
        this.setDataList(page.getResult());
    }

    public PageVO(Page<T> page, boolean setData) {
        this.setTotal(page.getTotal());
        if (setData) {
            this.setDataList(page.getResult());
        }
    }
}
