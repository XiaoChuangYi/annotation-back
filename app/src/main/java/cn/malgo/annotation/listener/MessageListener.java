package cn.malgo.annotation.listener;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import cn.malgo.annotation.core.tool.enums.EventEnum;
import cn.malgo.annotation.core.tool.event.CommonEvent;

/**
 * Created by 张钟 on 2017/9/13.
 */
@Component
//这里注意我们直接把监听类注册成组件
public class MessageListener implements ApplicationListener<CommonEvent> {

    @Autowired
    private Map<EventEnum, MessageProcessor> processorMap;

    @Async
    @Override
    public void onApplicationEvent(CommonEvent event) {
        processorMap.get(event.getEventEnum()).process(event);
    }
}
