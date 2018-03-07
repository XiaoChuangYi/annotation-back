package cn.malgo.annotation.common.dal.model;

import javax.persistence.*;

@Table(name="draw")
public class Draw {
    @Id
    private Integer id;

    @Column(name = "draw_name")
    private String drawName;


    @Column(name="type_label")
    private String typeLabel;

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

    public String getTypeLabel() {
        return typeLabel;
    }

    public void setTypeLabel(String typeLabel) {
        this.typeLabel = typeLabel;
    }
}