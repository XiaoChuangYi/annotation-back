package cn.malgo.annotation.web.controller.atomicterm.request;

import cn.malgo.annotation.common.util.AssertUtil;
import cn.malgo.annotation.web.controller.term.request.AddConceptRequest;

/**
 * Created by cjl on 2017/12/7.
 */
public class addAtomicConceptRequest {
    private  String id;
    private  String originName;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }


    public  static  void check(addAtomicConceptRequest request){
        AssertUtil.notNull(request,"新增单条术语请求对象为空");
        AssertUtil.notBlank(request.getOriginName(),"originName为空");
        AssertUtil.notNull(request.getId(),"id为空");

    }
}
