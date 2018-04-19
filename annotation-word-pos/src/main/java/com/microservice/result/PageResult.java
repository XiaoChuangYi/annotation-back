package com.microservice.result;

import java.util.List;

/**
 * Created by cjl on 2018/4/12.
 */
public class PageResult<T> {
    /***
     * 总条数
     */
    private  int total;
    /**
     *数据列表
     */
    List<T> dataList;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }
}
