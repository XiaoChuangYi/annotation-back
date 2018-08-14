package cn.malgo.annotation.utils;

import cn.malgo.annotation.utils.entity.LoggerEntity;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OpLoggerUtil {
  private static final String APPEND_NAME = "OpLog";
  private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private static final Logger logger = LoggerFactory.getLogger(APPEND_NAME);

  public static void info(long userId, String action, String result, long id) {
    logger.info(
        JSON.toJSONString(new LoggerEntity(userId, action, result, sdf.format(new Date()), id)));
  }
}
