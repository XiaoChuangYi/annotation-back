package cn.malgo.annotation.test;

import cn.malgo.annotation.core.service.tag.TagService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by cjl on 2017/11/30.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TagTest {
    @Autowired
    private TagService tagService;

    @Test
    public  void  test(){
//        List<String> aa=new LinkedList<>();
//        aa.add("弃用");
//        aa.add("启用");
//        tagService.insertTags(aa);
    }
}
