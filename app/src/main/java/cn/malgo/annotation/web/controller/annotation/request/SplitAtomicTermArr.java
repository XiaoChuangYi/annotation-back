package cn.malgo.annotation.web.controller.annotation.request;

import cn.malgo.annotation.core.business.annotation.AtomicTermAnnotation;

import java.util.List;

/**
 * Created by cjl on 2017/12/26.
 */
public class SplitAtomicTermArr {
    private List<AtomicTermAnnotation> splitAtomicTermList;

    public List<AtomicTermAnnotation> getSplitAtomicTermList() {
        return splitAtomicTermList;
    }

    public void setSplitAtomicTermList(List<AtomicTermAnnotation> splitAtomicTermList) {
        this.splitAtomicTermList = splitAtomicTermList;
    }
}
