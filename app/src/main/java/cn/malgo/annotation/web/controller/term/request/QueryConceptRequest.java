package cn.malgo.annotation.web.controller.term.request;

/**
 * Created by cjl on 2017/12/5.
 */
public class QueryConceptRequest {
    private  int pageIndex;
    private  int pageSize;
    private  String standardName;

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getStandardName() {
        return standardName;
    }

    public void setStandardName(String standardName) {
        this.standardName = standardName;
    }
}
