package com.microservice.result;

/**
 * Created by cjl on 2018/4/16.
 */
public class ResultVO<T> {
    /**系统调用结果*/
    private boolean sys_success=true;

    /**业务操作结果*/
    private boolean service_success=true;

    /**是否登录超时*/
    private boolean timeout=false;

    /**结果码*/
    private int code;

    /**返回信息*/
    private String msg;

    /**结果*/
    private T data;

    /**建立常用构造函数*/
    public ResultVO(){
        this.sys_success=true;
        this.service_success=true;
        this.msg="操作成功！";
    }
    public ResultVO(boolean sysSuccess, boolean serviceSuccess, String message) {
        this.sys_success = sysSuccess;
        this.service_success = serviceSuccess;
        this.msg = message;
    }

    public ResultVO(boolean sysSuccess, boolean serviceSuccess, boolean timeout, String message) {
        this.sys_success= sysSuccess;
        this.service_success = serviceSuccess;
        this.timeout = timeout;
        this.msg = message;
    }

    public static ResultVO success() {
        return new ResultVO(true, true, "操作成功!");
    }

    public static ResultVO success(Object data) {
        ResultVO result = new ResultVO(true, true, "操作成功!");
        result.setData(data);
        return result;
    }

    public static ResultVO error(String message) {
        ResultVO result = new ResultVO(true, false, message);
        return result;
    }

    public static ResultVO error(String message, int code) {
        ResultVO result = new ResultVO(true, false, message);
        result.setCode(code);
        return result;
    }

    public boolean isSys_success() {
        return sys_success;
    }

    public void setSys_success(boolean sys_success) {
        this.sys_success = sys_success;
    }

    public boolean isService_success() {
        return service_success;
    }

    public void setService_success(boolean service_success) {
        this.service_success = service_success;
    }

    public boolean isTimeout() {
        return timeout;
    }

    public void setTimeout(boolean timeout) {
        this.timeout = timeout;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
