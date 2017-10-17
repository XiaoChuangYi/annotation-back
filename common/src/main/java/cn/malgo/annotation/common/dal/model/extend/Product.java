package cn.malgo.annotation.common.dal.model.extend;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by 张钟 on 2017/9/22.
 */
public class Product {

    /**
     * 商品ID
     */
    private String     id;

    /**
     * 商品名称
     */
    private String     pdName;

    /**
     * 商品名称
     */
    private String     pdType;

    /**
     * 原价
     */
    private BigDecimal pdOriginalPrice;

    /**
     * 现价
     */
    private BigDecimal pdCurrentPrice;

    /**
     * 库存总量
     */
    private Integer    total;

    /**
     * 剩余库存
     */
    private Integer    residue;

    /**
     * 商品图片列表
     */
    private String     pdImage;

    /**
     * 商品状态
     */
    private String     state;

    /**
     * 商品描述
     */
    private String     pdDesc;

    /**
     * 备注
     */
    private String     memo;

    /**
     * 商品创建时间
     */
    private Date       gmtCreated;

    /**
     * 商品修改时间
     */
    private Date       gmtModified;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPdName() {
        return pdName;
    }

    public void setPdName(String pdName) {
        this.pdName = pdName;
    }

    public String getPdType() {
        return pdType;
    }

    public void setPdType(String pdType) {
        this.pdType = pdType;
    }

    public BigDecimal getPdOriginalPrice() {
        return pdOriginalPrice;
    }

    public void setPdOriginalPrice(BigDecimal pdOriginalPrice) {
        this.pdOriginalPrice = pdOriginalPrice;
    }

    public BigDecimal getPdCurrentPrice() {
        return pdCurrentPrice;
    }

    public void setPdCurrentPrice(BigDecimal pdCurrentPrice) {
        this.pdCurrentPrice = pdCurrentPrice;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getResidue() {
        return residue;
    }

    public void setResidue(Integer residue) {
        this.residue = residue;
    }

    public String getPdImage() {
        return pdImage;
    }

    public void setPdImage(String pdImage) {
        this.pdImage = pdImage;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPdDesc() {
        return pdDesc;
    }

    public void setPdDesc(String pdDesc) {
        this.pdDesc = pdDesc;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Date getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(Date gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }
}
