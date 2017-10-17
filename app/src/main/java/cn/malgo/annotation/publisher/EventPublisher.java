package cn.malgo.annotation.publisher;

import cn.malgo.annotation.core.model.enums.EventEnum;
import cn.malgo.annotation.core.model.event.CommonEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;


/**
 * Created by 张钟 on 2017/9/13.
 */
@Component
public class EventPublisher<T> {

    @Autowired
    private ApplicationContext context;

    public void publish(EventEnum eventEnum, T message){
        //方法中调用
        context.publishEvent(new CommonEvent(this, eventEnum,message));
    }

}
