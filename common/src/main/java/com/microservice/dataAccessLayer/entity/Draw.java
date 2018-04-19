package com.microservice.dataAccessLayer.entity;

/**
 * Created by cjl on 2018/4/2.
 */
public class Draw {
    private int id;
    private String drawName;
    private String typeLabel;

    //关联type表的字段typeCode
    private String typeCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDrawName() {
        return drawName;
    }

    public void setDrawName(String drawName) {
        this.drawName = drawName;
    }

    public String getTypeLabel() {
        return typeLabel;
    }

    public void setTypeLabel(String typeLabel) {
        this.typeLabel = typeLabel;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }
}
