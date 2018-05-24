package com.jiangdaxian.redis;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

public class RedisLock {
	private static final long TIMEOUT = 10000L;
	private static final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;
	
	private RedisTemplate redisTemplate;

	public RedisLock(RedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	/**
	 * redis lock
	 * 
	 * @param key
	 */
	public void lock(Object key) {
		String keyStr = key.toString();
		boolean isLock = lockLogic(keyStr);
		long last = System.currentTimeMillis();
		while (!isLock) {
			try {
				TimeUnit.MILLISECONDS.sleep(100);
			} catch (InterruptedException e) {

			}

			if (System.currentTimeMillis() - last > TIMEOUT) {
				// 锁超时
				throw new RuntimeException("multi retry lock timeout!");
			}
			// 重新获取锁
			isLock = lockLogic(keyStr);
		}
	}

	public void unlock(Object key) {
		String keyStr = key.toString();
		remove(keyStr);
	}

	/**
	 * 获取锁lock
	 * 
	 * @author vakinge
	 * @param lockId
	 * @param timeout
	 *            毫秒
	 * @return 获得lock ＝＝ true
	 */
	@SuppressWarnings("unchecked")
	private boolean lockLogic(Object key) {
		String keyStr = key.toString();
		Boolean result = false;
		try {
			ValueOperations valueOperations = redisTemplate.opsForValue();
			result = valueOperations.setIfAbsent(keyStr, "true");
			if (result) {
				redisTemplate.expire(keyStr, TIMEOUT + 2000, TIME_UNIT);
			}
		} finally {

		}
		return result;
	}

	/**
	 * redis lock
	 * 
	 * @param key
	 */
	public void lockByIncr(Object key) {
		String keyStr = key.toString();
		boolean isLock = lockLogicByIncr(keyStr);
		long last = System.currentTimeMillis();
		while (!isLock) {
			try {
				TimeUnit.MILLISECONDS.sleep(100);
			} catch (InterruptedException e) {

			}

			if (System.currentTimeMillis() - last > TIMEOUT) {
				// 锁超时
				throw new RuntimeException("multi retry lock timeout!");
			}
			// 重新获取锁
			isLock = lockLogicByIncr(keyStr);
		}
	}

	/**
	 * 获取锁lock
	 * 
	 * @author vakinge
	 * @param lockId
	 * @param timeout
	 *            毫秒
	 * @return 获得lock ＝＝ true
	 */
	@SuppressWarnings("unchecked")
	private boolean lockLogicByIncr(Object key) {
		String keyStr = key.toString();
		boolean result = false;
		try {
			ValueOperations valueOperations = redisTemplate.opsForValue();
			Long l = valueOperations.increment(keyStr, 1);
			if (l != null && l.equals(1L)) {
				result = true;
				redisTemplate.expire(keyStr, TIMEOUT + 2000, TIME_UNIT);
			}
		} finally {

		}
		return result;
	}
	
	private void remove(Object key){
		String keyStr = key.toString();
		redisTemplate.delete(keyStr);
	}
}
