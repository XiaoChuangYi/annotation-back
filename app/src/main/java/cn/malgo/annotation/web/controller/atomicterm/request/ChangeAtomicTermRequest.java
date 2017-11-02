package cn.malgo.annotation.web.controller.atomicterm.request;

import cn.malgo.annotation.common.util.AssertUtil;

/**
 * @author 张钟
 * @date 2017/11/2
 */
public class ChangeAtomicTermRequest {

    /** 原子术语ID **/
    private String atomicTermId;

    /** 原子术语类型 **/
    private String type;


    public static void check(ChangeAtomicTermRequest request){
        AssertUtil.notNull(request,"修改原子术语请求对象为空");
        AssertUtil.notBlank(request.getAtomicTermId(),"原子术语ID为空");
        AssertUtil.notBlank(request.getType(),"原子术语内容为空");
    }


    public String getAtomicTermId() {
        return atomicTermId;
    }

    public void setAtomicTermId(String atomicTermId) {
        this.atomicTermId = atomicTermId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
