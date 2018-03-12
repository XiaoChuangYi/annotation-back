package cn.malgo.annotation.web.controller.term.result;

import cn.malgo.annotation.common.dal.model.Term;

import java.util.List;

/**
 * Created by cjl on 2018/1/2.
 */
public class TermGroupVO {
    private  List<List<Term>> mixList;
    private  int groups;

    public List<List<Term>> getMixList() {
        return mixList;
    }

    public void setMixList(List<List<Term>> mixList) {
        this.mixList = mixList;
    }

    public int getGroups() {
        return groups;
    }

    public void setGroups(int groups) {
        this.groups = groups;
    }
}
