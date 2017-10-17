package cn.malgo.annotation.task;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author ZhangZhong
 * @date 2017/6/16
 */
@Configuration
@ConfigurationProperties(prefix = "spring.task.pool") // 该注解的locations已经被启用，现在只要是在环境中，都会优先加载
public class TaskThreadPoolConfig {

    /** 核心线程数 **/
    private int corePoolSize;

    /** 最大线程数 **/
    private int maxPoolSize;

    /** 存活时间 **/
    private int keepAliveSeconds;

    /**  队列容量 **/
    private int queueCapacity;


    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public int getKeepAliveSeconds() {
        return keepAliveSeconds;
    }

    public void setKeepAliveSeconds(int keepAliveSeconds) {
        this.keepAliveSeconds = keepAliveSeconds;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }
}