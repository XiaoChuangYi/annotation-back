package cn.malgo.annotation.common.dal.model;

import javax.persistence.*;

@Table(name="tag")
public class Tag {
    @Id
    private Integer id;

    @Column(name = "tag_name")
    private String tagName;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return tag_name
     */
    public String getTagName() {
        return tagName;
    }

    /**
     * @param tagName
     */
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}