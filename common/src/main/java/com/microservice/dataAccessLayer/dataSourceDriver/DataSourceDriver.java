package com.microservice.dataAccessLayer.dataSourceDriver;

import com.github.pagehelper.PageHelper;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by cjl on 2018/3/30.
 */
@Configuration
@MapperScan(basePackages = "com.microservice.dataAccessLayer.mapper")
public class DataSourceDriver {

     /**
      * 创建数据源
      */
     @Primary
     @Bean("annotationDataSource")
     @ConfigurationProperties(prefix = "spring.datasource.annotation")
     public DataSource annotationDataSource(){
         return DataSourceBuilder.create().build();
     }

    @Bean
    public PageHelper pageHelper() {
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
     /**
      * 根据数据源创建sqlSessionFactory
      */
     @Bean
 public SqlSessionFactory sqlSessionFactory(@Qualifier("annotationDataSource") DataSource annotationDataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactory=new SqlSessionFactoryBean();
        Interceptor[] plugins=new Interceptor[]{pageHelper()};
        sqlSessionFactory.setDataSource(annotationDataSource);
        sqlSessionFactory.setPlugins(plugins);
        return sqlSessionFactory.getObject();
     }

    /**
     * 配置事务管理器
     */
    @Bean
    public DataSourceTransactionManager transactionManager(DataSource annotationDataSource){
        return  new DataSourceTransactionManager(annotationDataSource);
    }

}
