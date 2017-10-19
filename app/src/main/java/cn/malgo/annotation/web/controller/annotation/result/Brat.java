/**
  * Copyright 2017 bejson.com 
  */
package cn.malgo.annotation.web.controller.annotation.result;

import java.util.Date;
import java.util.List;

/**
 * Auto-generated: 2017-10-19 10:40:46
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Brat {

    private List<String>        modifications;
    private List<String>        normalizations;
    private double              ctime;
    private List<String>        triggers;
    private String              text;
    private List<String>        source_files;
    private double              mtime;
    private List<String>        messages;
    private List<List<Integer>> sentence_offsets;
    private List<String>        relations;
    private List<List<String>>  entities;
    private List<String>        comments;
    private List<Date>          token_offsets;
    private String              action;
    private List<String>        attributes;
    private List<String>        equivs;
    private List<String>        events;
    private int                 protocol;

    public void setModifications(List<String> modifications) {
        this.modifications = modifications;
    }

    public List<String> getModifications() {
        return modifications;
    }

    public void setNormalizations(List<String> normalizations) {
        this.normalizations = normalizations;
    }

    public List<String> getNormalizations() {
        return normalizations;
    }

    public void setCtime(double ctime) {
        this.ctime = ctime;
    }

    public double getCtime() {
        return ctime;
    }

    public void setTriggers(List<String> triggers) {
        this.triggers = triggers;
    }

    public List<String> getTriggers() {
        return triggers;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setSource_files(List<String> source_files) {
        this.source_files = source_files;
    }

    public List<String> getSource_files() {
        return source_files;
    }

    public void setMtime(double mtime) {
        this.mtime = mtime;
    }

    public double getMtime() {
        return mtime;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setSentence_offsets(List<List<Integer>> sentence_offsets) {
        this.sentence_offsets = sentence_offsets;
    }

    public List<List<Integer>>

            getSentence_offsets() {
        return sentence_offsets;
    }

    public void setRelations(List<String> relations) {
        this.relations = relations;
    }

    public List<String> getRelations() {
        return relations;
    }

    public void setEntities(List<List<String>> entities) {
        this.entities = entities;
    }

    public List<List<String>> getEntities() {
        return entities;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setToken_offsets(List<Date> token_offsets) {
        this.token_offsets = token_offsets;
    }

    public List<Date> getToken_offsets() {
        return token_offsets;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public void setEquivs(List<String> equivs) {
        this.equivs = equivs;
    }

    public List<String> getEquivs() {
        return equivs;
    }

    public void setEvents(List<String> events) {
        this.events = events;
    }

    public List<String> getEvents() {
        return events;
    }

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    public int getProtocol() {
        return protocol;
    }

}