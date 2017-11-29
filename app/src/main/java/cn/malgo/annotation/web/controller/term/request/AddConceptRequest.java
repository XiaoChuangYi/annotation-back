package cn.malgo.annotation.web.controller.term.request;

import cn.malgo.annotation.common.util.AssertUtil;

/**
 * Created by cjl on 2017/11/29.
 */
public class AddConceptRequest {

    private  Integer id;
    private  String originName;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }


    public  static  void check(AddConceptRequest request){
        AssertUtil.notNull(request,"新增单条术语请求对象为空");
        AssertUtil.notBlank(request.getOriginName(),"originName为空");
        AssertUtil.notNull(request.getId(),"id为空");

    }
}
