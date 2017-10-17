package cn.malgo.annotation.common.dal.model.extend;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by 张钟 on 2017/9/26.
 */
public class OrderDetail {

    /**
     * 交易记录ID
     */
    private String     id;

    /**
     * 商品ID
     */
    private String     pdId;

    /**
     * 用户ID
     */
    private String     userId;

    /**
     * 购买数量
     */
    private Integer    tdCount;

    /**
     * 单价
     */
    private BigDecimal unitPrice;

    /**
     * 单价
     */
    private BigDecimal totalPrice;

    /**
     * 订单状态
     */
    private String     state;

    /**
     * 订单状态名称
     */
    private String     stateName;

    private String     receiverDetailLocation;

    private String     memo;

    /**
     * 订单创建时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date       gmtCreated;

    /**
     * 订单修改时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date       gmtModified;

    /**
     * 订单支付时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date       paidTime;

    /**
     * 收货人县区
     */
    private String     receiverCounty;

    /**
     * 收货人省份
     */
    private String     receiverProvince;

    /**
     * 收货人城市
     */
    private String     receiverCity;

    /**
     * 收货人电话
     */
    private String     receiverPhone;

    /**
     * 收货人姓名
     */
    private String     receiverName;

    /**
     * 商品名称
     */
    private String     pdName;

    /**
     * 商品类型
     */
    private String     pdType;

    /**
     * 商品图片列表
     */
    private String     pdImage;

    /**
     * 用户图片列表
     */
    private JSONArray  imageArray;

    /**
     * 商品描述
     */
    private String     pdDesc;

    /**
     * 购买人账户
     */
    private String     accountNo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPdId() {
        return pdId;
    }

    public void setPdId(String pdId) {
        this.pdId = pdId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getTdCount() {
        return tdCount;
    }

    public void setTdCount(Integer tdCount) {
        this.tdCount = tdCount;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getReceiverDetailLocation() {
        return receiverDetailLocation;
    }

    public void setReceiverDetailLocation(String receiverDetailLocation) {
        this.receiverDetailLocation = receiverDetailLocation;
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

    public String getReceiverCounty() {
        return receiverCounty;
    }

    public void setReceiverCounty(String receiverCounty) {
        this.receiverCounty = receiverCounty;
    }

    public String getReceiverProvince() {
        return receiverProvince;
    }

    public void setReceiverProvince(String receiverProvince) {
        this.receiverProvince = receiverProvince;
    }

    public String getReceiverCity() {
        return receiverCity;
    }

    public void setReceiverCity(String receiverCity) {
        this.receiverCity = receiverCity;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
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

    public String getPdImage() {
        return pdImage;
    }

    public void setPdImage(String pdImage) {
        this.pdImage = pdImage;
    }

    public String getPdDesc() {
        return pdDesc;
    }

    public void setPdDesc(String pdDesc) {
        this.pdDesc = pdDesc;
    }

    public JSONArray getImageArray() {
        return JSONArray.parseArray(this.pdImage);
    }

    public void setImageArray(JSONArray imageArray) {
        this.imageArray = imageArray;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public Date getPaidTime() {
        return paidTime;
    }

    public void setPaidTime(Date paidTime) {
        this.paidTime = paidTime;
    }
}
