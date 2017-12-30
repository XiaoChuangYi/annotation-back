package cn.malgo.annotation.web.controller.annotation;

import cn.malgo.annotation.core.model.annotation.AtomicTermAnnotation;

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
