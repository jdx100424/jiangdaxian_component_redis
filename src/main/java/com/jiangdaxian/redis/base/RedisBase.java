package com.jiangdaxian.redis.base;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;

public class RedisBase {
	private static final long TIMEOUT = 10000L;
	private static final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;
	
	private RedisTemplate redisTemplate;

	public RedisBase(RedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}
	
	public void set(String name,Object itemInfo) {
		redisTemplate.opsForValue().set(name, itemInfo);
	}
	
	public Object get(String name) {
		return redisTemplate.opsForValue().get(name);
	}
	public <T> T get(String name,Class<T> cla) throws Exception {
		if(cla ==null) {
			throw new Exception("class is not allow null");
		}
		Object result = redisTemplate.opsForValue().get(name);
		T t = (T) result;
		return t;
	}
}
