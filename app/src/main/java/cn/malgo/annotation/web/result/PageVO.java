package cn.malgo.annotation.web.result;

import java.util.List;

import com.github.pagehelper.Page;

/**
 * Created by 张钟 on 2017/9/23.
 */
public class PageVO<T> {

    /**
     * 页码，从1开始
     */
    private int     pageNum;
    /**
     * 页面大小
     */
    private int     pageSize;
    /**
     * 总数
     */
    private long    total;
    /**
     * 总页数
     */
    private int     pages;
    /**
     * 是否有下一页
     */
    private boolean hasMore;
    /**
     *数据列表
     */
    List<T>         dataList;

    public PageVO(Page<T> page) {
        this.setDataList(page.getResult());
        this.setPageNum(page.getPageNum());
        this.setPages(page.getPages());
        this.setPageSize(page.getPageSize());
        this.setHasMore(page.getPageNum() < page.getPages());
        this.setTotal(page.getTotal());
    }

    public PageVO(Page<T> page,boolean setData) {
        if(setData){
            this.setDataList(page.getResult());
        }
        this.setPageNum(page.getPageNum());
        this.setPages(page.getPages());
        this.setPageSize(page.getPageSize());
        this.setHasMore(page.getPageNum() < page.getPages());
        this.setTotal(page.getTotal());
    }



    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }
}
