package cn.malgo.annotation.service;

public interface LocalRedisService {

  void setTicket(String ticket);

  String getTicket();
}
