package cn.malgo.annotation.manage.result;

import java.io.Serializable;

/**
 * Created by ZhangZhong on 2017/5/6.
 */
public class JsonResult implements Serializable {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1475348231900998033L;

    /**系统调用结果*/
    private boolean           is_succ          = true;

    /**业务操作结果*/
    private boolean           succ             = true;

    /**是否登录超时*/
    private boolean           timeout          = false;

    /**页面是否过期*/
    private boolean           overdue          = false;

    /** 错误码 **/
    private String            code;

    /**错误信息*/
    private String            msg;

    public JsonResult() {
    }

    public JsonResult(boolean isSucc, boolean succ) {
        this.is_succ = isSucc;
        this.succ = succ;
        this.msg = "系统异常！";
    }

    public JsonResult(boolean isSucc, boolean succ, String message) {
        this.is_succ = isSucc;
        this.succ = succ;
        this.msg = message;
    }

    public JsonResult(boolean isSucc, boolean succ, boolean timeout, String message) {
        this.is_succ = isSucc;
        this.succ = succ;
        this.timeout = timeout;
        this.msg = message;
    }

    public static JsonResult success() {
        return new JsonResult(true, true, "操作成功");
    }

    public static JsonResult success(String message) {
        return new JsonResult(true, true, message);
    }

    public static JsonResult error(String msg) {
        return new JsonResult(true, false, msg);
    }

    public static JsonResult error(String msg, String code) {
        JsonResult result = new JsonResult(true, false, msg);
        result.setCode(code);
        return result;
    }

    public boolean isIs_succ() {
        return is_succ;
    }

    public void setIs_succ(boolean is_succ) {
        this.is_succ = is_succ;
    }

    public boolean isSucc() {
        return succ;
    }

    public void setSucc(boolean succ) {
        this.succ = succ;
    }

    public String getMsg() {
        if (msg == null) {
            return "";
        }
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * @return Returns the timeout.
     */
    public boolean isTimeout() {
        return timeout;
    }

    /**
     * @param timeout
     *            The timeout to set.
     */
    public void setTimeout(boolean timeout) {
        this.timeout = timeout;
    }

    /**
     * @return Returns the overdue.
     */
    public boolean isOverdue() {
        return overdue;
    }

    /**
     * @param overdue
     *            The overdue to set.
     */
    public void setOverdue(boolean overdue) {
        this.overdue = overdue;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
