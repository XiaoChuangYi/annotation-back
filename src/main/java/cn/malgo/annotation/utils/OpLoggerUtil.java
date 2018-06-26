package cn.malgo.annotation.utils;

import com.alibaba.fastjson.JSON;
import cn.malgo.annotation.utils.entity.LoggerEntity;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Created by cjl on 2018/6/6. */
@Slf4j
public class OpLoggerUtil {

  private static final String APPEND_NAME = "OpLog";

  private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  public static void info(int userId, int roleId, String action, String result, int id) {
    Logger logger = LoggerFactory.getLogger(APPEND_NAME);
    logger.info(
        JSON.toJSONString(
            new LoggerEntity(userId, roleId, action, result, sdf.format(new Date()), id)));
  }
}
