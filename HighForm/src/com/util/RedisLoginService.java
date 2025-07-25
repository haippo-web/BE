package com.util;

import java.util.Map;

import redis.clients.jedis.Jedis;

public class RedisLoginService {
    private static final String REDIS_HOST = "localhost";
    private static final int REDIS_PORT = 6379;


    public void saveLoginUserToRedis(Long userId, String userName,String role ) {
        try (Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT)) {

            String key = "login:user:";
            jedis.hset(key, "id", String.valueOf(userId));
            jedis.hset(key, "name", userName);
            jedis.hset(key, "role", role);
            // 만료시간 1시간 설정
            jedis.expire(key, 60 * 60);

            System.out.println("Redis에 로그인 유저 정보 저장 완료: " + key);
        }
    }
    
    // 유저 정보 조회
    public Map<String, String> getLoginUserFromRedis() {
        try (Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT)) {
            // jedis.auth(REDIS_PASSWORD);

            String key = "login:user:";
            Map<String, String> userInfo = jedis.hgetAll(key);

            if (userInfo == null || userInfo.isEmpty()) {
                System.out.println("Redis에 해당 유저 정보가 없습니다.");
                return null;
            }

            System.out.println("Redis에서 유저 정보 조회: " + userInfo);
            return userInfo;
        }
    }
    
    // 유저 데이터 삭제

    public void deleteLoginUserFromRedis() {
        try (Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT)) {
            String key = "login:user:";
            jedis.del(key); 
//            jedis.flushAll(); // 모든 DB의 모든 키 삭제
        }
    }
    
}
