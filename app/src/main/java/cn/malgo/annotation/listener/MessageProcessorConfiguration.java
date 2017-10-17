package cn.malgo.annotation.listener;

import java.util.HashMap;
import java.util.Map;

import cn.malgo.annotation.core.model.enums.EventEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



/**
 * Created by 张钟 on 2017/9/13.
 */

@Configuration
public class MessageProcessorConfiguration {

    @Autowired
    private TradeProcessor TradeProcessor;

    @Bean
    public Map<EventEnum,MessageProcessor> getMessageProcessorMap(){
        Map<EventEnum,MessageProcessor> map = new HashMap<>();
        map.put(EventEnum.TRADE, TradeProcessor);
        return map;
    }
}
