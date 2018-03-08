package cn.malgo.annotation.web.controller.annotation.request;

import cn.malgo.annotation.core.business.antomicTerm.CombineAtomicTerm;

import java.util.List;

/**
 * Created by cjl on 2018/1/4.
 */
public class CombineAtomicTermArr {
    private List<CombineAtomicTerm> combineAtomicTermList;

    public List<CombineAtomicTerm> getCombineAtomicTermList() {
        return combineAtomicTermList;
    }

    public void setCombineAtomicTermList(List<CombineAtomicTerm> combineAtomicTermList) {
        this.combineAtomicTermList = combineAtomicTermList;
    }
}
