package com.guoanjia.guoanjiasso.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guoanjia.guoanjiasso.dao.User;
import com.guoanjia.guoanjiasso.dao.jpa.UserRepository;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author syliu
 * @Title:
 * @Package UserService
 * @create 2018/1/30 0030
 */
@Service
public class UserService {
	private static final ObjectMapper MAPPER = new ObjectMapper();
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private StringRedisTemplate redisTemplate;
	@Value("${token.cacheTime}")
	private Long cacheTime;
	@Value("${token.tokenKey}")
	private String tokenKey;
	public Boolean check(String param, Integer type) {
		if (type < 1 || type > 4) {
			return null;
		}
		switch (type) {
			case 1:
				return userRepository.findByUsername(param) == null;
			case 2:
				return userRepository.findByPhone(param) == null;
			case 3:
				return userRepository.findByEmail(param) == null;
			case 4:
				return userRepository.findByOpenid(param) == null;
		}
		return true;
	}
	public Boolean saveUser(User user) {
		user.setId(null);
		user.setCreated(new Date());
		user.setUpdated(user.getCreated());
		// 密码通过MD5进行加密处理
		try {
			userRepository.saveAndFlush(user);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public String doLogin(String username, String password) throws Exception {
		User user =userRepository.findByUsername(username);
		if (null == user) {
			return null;
		}
		if (!StringUtils.equals(DigestUtils.md5Hex(password), user.getPassword())) {
			return null;
		}
		 //登录成功
		 //生存token
		String token = DigestUtils.md5Hex(System.currentTimeMillis() + username);
		redisTemplate.opsForValue().set(tokenKey+token,MAPPER.writeValueAsString(user),cacheTime, TimeUnit.SECONDS);
		return token;
	}

	public User queryUserByToken(String token) {
		String key=tokenKey+token;
		String jsonData = redisTemplate.opsForValue().get(key);
		if (org.springframework.util.StringUtils.isEmpty(jsonData)) {
			return null;
		}
		try {
			redisTemplate.expire(key,cacheTime,TimeUnit.SECONDS);
			// 刷新用户的生存时间(非常重要)
			return MAPPER.readValue(jsonData, User.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
