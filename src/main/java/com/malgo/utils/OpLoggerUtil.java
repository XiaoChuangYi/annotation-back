package com.malgo.utils;

import com.alibaba.fastjson.JSON;
import com.malgo.utils.entity.LoggerEntity;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by cjl on 2018/6/6.
 */
@Slf4j
public class OpLoggerUtil {

  private final static String APPEND_NAME = "OpLog";

  private final static SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  public static void info(int userId, int roleId, String action, String result) {
    Logger logger = LoggerFactory.getLogger(APPEND_NAME);
    logger.info(JSON.toJSONString(new LoggerEntity(userId, roleId, action, result, sdf.format(new Date()))));
  }
}