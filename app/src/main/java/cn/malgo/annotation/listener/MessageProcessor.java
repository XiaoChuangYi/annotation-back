package cn.malgo.annotation.listener;


import cn.malgo.annotation.core.model.event.CommonEvent;

/**
 * Created by 张钟 on 2017/9/13.
 */
public abstract class MessageProcessor<T> {

    protected abstract void doProcess(CommonEvent<T> commonEvent);

    protected abstract void beforeProcess(CommonEvent<T> commonEvent);

    public void process(CommonEvent<T> commonEvent) {
        this.beforeProcess(commonEvent);
        this.doProcess(commonEvent);
        this.afterProcess(commonEvent);
    }

    protected abstract void afterProcess(CommonEvent<T> commonEvent);

}
