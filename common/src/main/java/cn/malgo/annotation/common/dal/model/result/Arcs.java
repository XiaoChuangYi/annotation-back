package cn.malgo.annotation.common.dal.model.result;

import java.util.List;

/**
 * Created by cjl on 2017/12/15.
 */
public class Arcs {
    private List<String> labels;
    private String type;
    private List<String> targets;

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getTargets() {
        return targets;
    }

    public void setTargets(List<String> targets) {
        this.targets = targets;
    }
}
