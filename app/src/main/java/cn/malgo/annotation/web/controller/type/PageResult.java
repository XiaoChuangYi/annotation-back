package cn.malgo.annotation.web.controller.type;

import java.util.List;

/**
 * Created by cjl on 2017/12/4.
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
