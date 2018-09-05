package cn.malgo.annotation.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONObject;

import cn.malgo.annotation.config.Consts;
import cn.malgo.annotation.service.AuthService;
import cn.malgo.annotation.utils.PostUtil;
import cn.malgo.common.auth.RedisConfigService;
import lombok.extern.slf4j.Slf4j;
/**
 * TODO 登录用户中心
 * @author huangjq
   2018年9月5日
 */
@Slf4j
@Service(value = "authService")
public class AuthServiceImpl implements AuthService {
	
	@Value("${cn.malgo.auth.path}")
	private String path;
	@Value("${cn.malgo.auth.nickname}")
	private String nickname;
	@Value("${cn.malgo.auth.password}")
	private String password;

	private RedisConfigService redisConfigService;

	public AuthServiceImpl(RedisConfigService redisConfigService) {
		super();
		this.redisConfigService = redisConfigService;
	}

	/**
	 * 系统登录用户中心
	 * 
	 * @return
	 */
	public boolean login() {
		try {
			String json = getJson(nickname, password);
			String str = PostUtil.doPost(path, json);
			if (StringUtils.isEmpty(str)) {
				return false;
			}
			JSONObject o = JSONObject.parseObject(str);
			if (null == o)
				return false;
			JSONObject data = o.getJSONObject("data");
			if (null == data)
				return false;
			String ticket = data.getString("ticket");
			if (StringUtils.isBlank(ticket))
				return false;
			redisConfigService.set(Consts.SYSTEM_TICKET, ticket);
			return true;
		} catch (Exception e) {
			log.error("AuthServiceImpl error", e);
			return false;
		}
	}

	private String getJson(String nickname, String password) {
		return "{\"nickname\":" + nickname + ",\"password\":" + password + "}";
	}
}
