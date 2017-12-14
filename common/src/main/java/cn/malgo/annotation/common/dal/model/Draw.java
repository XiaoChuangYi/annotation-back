package cn.malgo.annotation.common.dal.model;

import javax.persistence.*;

public class Draw {
    @Id
    private Integer id;

    @Column(name = "draw_name")
    private String drawName;

    private String typeCode;


    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

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
     * @return draw_name
     */
    public String getDrawName() {
        return drawName;
    }

    /**
     * @param drawName
     */
    public void setDrawName(String drawName) {
        this.drawName = drawName;
    }
}