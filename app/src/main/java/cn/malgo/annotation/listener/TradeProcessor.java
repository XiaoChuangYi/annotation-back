package cn.malgo.annotation.listener;

import org.springframework.stereotype.Component;

import cn.malgo.annotation.core.model.event.CommonEvent;

/**
 * Created by 张钟 on 2017/9/13.
 */
@Component
public class TradeProcessor extends MessageProcessor<Object> {

    @Override
    protected void doProcess(CommonEvent<Object> commonEvent) {

    }

    @Override
    protected void beforeProcess(CommonEvent<Object> commonEvent) {

    }

    @Override
    protected void afterProcess(CommonEvent<Object> commonEvent) {

    }
}
