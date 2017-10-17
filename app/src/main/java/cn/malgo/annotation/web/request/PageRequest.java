package cn.malgo.annotation.web.request;

/**
 * Created by 张钟 on 2017/7/12.
 */
public class PageRequest {

    /** 分页大小 **/
    protected int pageSize = 10;

    /** 页码 **/
    protected int pageNum = 1;

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }
}
