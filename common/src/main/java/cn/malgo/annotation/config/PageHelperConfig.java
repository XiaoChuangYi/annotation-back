package cn.malgo.annotation.config;

import com.github.pagehelper.PageHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Created by 张钟 on 2017/6/24.
 */
@Configuration
public class PageHelperConfig {

    @Bean
    public PageHelper pageHelper() {
        System.out.println("MyBatisConfiguration.pageHelper()");
        PageHelper pageHelper = new PageHelper();
        Properties p = new Properties();
        p.setProperty("dialect","mysql");
        p.setProperty("offsetAsPageNum", "true");
        p.setProperty("rowBoundsWithCount", "true");
        p.setProperty("reasonable", "true");
        p.setProperty("pageSizeZero", "true");
        p.setProperty("supportMethodsArguments", "false");
        p.setProperty("returnPageInfo", "none");
        pageHelper.setProperties(p);
        return pageHelper;
    }
}
