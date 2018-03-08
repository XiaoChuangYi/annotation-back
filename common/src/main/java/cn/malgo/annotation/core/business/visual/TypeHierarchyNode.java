package cn.malgo.annotation.core.business.visual;

import java.util.List;
import java.util.Map;

/**
 * Created by cjl on 2017/12/13.
 */
public class TypeHierarchyNode {

    private List<String> terms;
    private List<String> args;
    private boolean unused;
    private List<TypeHierarchyNode> children;
    private Map<String,List<String>> arguments;
    private Map<String,List<String>> special_arguments;
    private List<String> arg_list;
    private Map<String,Integer> arg_max_count;
    private Map<String,Integer> arg_min_count;
    private Map<String,List<String>> keys_by_type;

    public List<String> getTerms() {
        return terms;
    }

    public void setTerms(List<String> terms) {
        this.terms = terms;
    }

    public List<String> getArgs() {
        return args;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }

    public boolean isUnused() {
        return unused;
    }

    public void setUnused(boolean unused) {
        this.unused = unused;
    }

    public List<TypeHierarchyNode> getChildren() {
        return children;
    }

    public void setChildren(List<TypeHierarchyNode> children) {
        this.children = children;
    }

    public Map<String, List<String>> getArguments() {
        return arguments;
    }

    public void setArguments(Map<String, List<String>> arguments) {
        this.arguments = arguments;
    }

    public Map<String, List<String>> getSpecial_arguments() {
        return special_arguments;
    }

    public void setSpecial_arguments(Map<String, List<String>> special_arguments) {
        this.special_arguments = special_arguments;
    }

    public List<String> getArg_list() {
        return arg_list;
    }

    public void setArg_list(List<String> arg_list) {
        this.arg_list = arg_list;
    }

    public Map<String, Integer> getArg_max_count() {
        return arg_max_count;
    }

    public void setArg_max_count(Map<String, Integer> arg_max_count) {
        this.arg_max_count = arg_max_count;
    }

    public Map<String, Integer> getArg_min_count() {
        return arg_min_count;
    }

    public void setArg_min_count(Map<String, Integer> arg_min_count) {
        this.arg_min_count = arg_min_count;
    }

    public Map<String, List<String>> getKeys_by_type() {
        return keys_by_type;
    }

    public void setKeys_by_type(Map<String, List<String>> keys_by_type) {
        this.keys_by_type = keys_by_type;
    }
}
