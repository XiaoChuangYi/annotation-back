package cn.malgo.annotation.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.malgo.annotation.web.result.ResultVO;

/**
 *
 * @author ZhangZhong
 * @date 2017/5/6
 */
@Controller
public class IndexController {

    @RequestMapping(value = "/index.htm")
    public String getIndex() {
        return "index";
    }

}
