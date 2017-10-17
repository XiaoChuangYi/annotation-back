package cn.malgo.annotation.manage.result;

import java.util.List;

import com.github.pagehelper.Page;

/**
 * Created by 张钟 on 2017/6/24.
 */
public class PageResult<T> extends JsonResult {

    /** 数据 **/
    private List<T> data;

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

    public PageResult() {

    }

    public PageResult(boolean isSuccess, boolean succ, String msg) {
        super(isSuccess, succ, msg);
    }

    public PageResult(Page page) {
        super(true, true, Message.SUCCESS);
        this.data = page.getResult();
        setPageNum(page.getPageNum());
        setPageSize(page.getPageSize());
        setPages(page.getPages());
        setTotal(page.getTotal());
        setHasMore(page.getPageNum()<page.getPages());
    }

    public PageResult(Page page,boolean copyData) {
        super(true, true, Message.SUCCESS);
        if(copyData){
            this.data = page.getResult();
        }
        setPageNum(page.getPageNum());
        setPageSize(page.getPageSize());
        setPages(page.getPages());
        setTotal(page.getTotal());
        setHasMore(page.getPageNum()<page.getPages());
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
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
}
