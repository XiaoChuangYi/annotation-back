package cn.malgo.annotation.manage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by 张钟 on 2017/8/2.
 */
@Controller
public class IndexController {

    @RequestMapping(value = { "/index.htm" })
    public String hello() {
        return "index";
    }

}
