package cn.malgo.annotation.core.tool.event;

import cn.malgo.annotation.core.tool.enums.EventEnum;
import org.springframework.context.ApplicationEvent;

/**
 * Created by 张钟 on 2017/9/13.
 */
public class CommonEvent<T> extends ApplicationEvent {

    private static final long serialVersionUID = -1388275394985648520L;

    private EventEnum eventEnum;

    private T message;


    /**
     * Create visual new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public CommonEvent(Object source,EventEnum eventEnum,T message) {
        super(source);
        this.eventEnum = eventEnum;
        this.message = message;
    }

    public EventEnum getEventEnum() {
        return eventEnum;
    }

    public void setEventEnum(EventEnum eventEnum) {
        this.eventEnum = eventEnum;
    }

    public T getMessage() {
        return message;
    }

    public void setMessage(T message) {
        this.message = message;
    }
}
