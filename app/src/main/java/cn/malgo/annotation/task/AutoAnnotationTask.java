package cn.malgo.annotation.task;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import cn.malgo.annotation.common.util.ip.IpUtil;
import cn.malgo.annotation.common.util.log.LogUtil;

/**
 *
 * @author 张钟
 * @date 2017/9/26
 */
@Component
public class AutoAnnotationTask {

    private Logger logger = Logger.getLogger(AutoAnnotationTask.class);

    @Value("${spring.task.server.host}")
    private String taskServerIp;

    @Scheduled(cron = "0 5 0 * * ?")
    public void executeFileDownLoadTask() {

        LogUtil.info(logger, "开始执行发放积分的定时任务");

        //检查是否允许执行定时任务
        if (!canExecute()) {
            return;
        }

        int pageNum = 1;
        int pageSize = 10;

        int successCount = 0;
        int failCount = 0;

        do {
            List<Future<Boolean>> futureList = new ArrayList<>();
        } while (false);

        LogUtil.info(logger,
            MessageFormat.format("结束执行发放积分的定时任务,成功:{0},失败:{1}", successCount, failCount));

    }

    /**
     * 是否允许运行
     * @return
     */
    private boolean canExecute() {
        String ip = IpUtil.getIp();
        return taskServerIp.equals(ip);
    }

}
