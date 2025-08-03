package com.studyParty.websocket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class RedisUtil {
    
    @Autowired
    private ReactiveRedisOperations<String, String> reactiveRedisOperations;
    
    // 离线消息存储的Redis键前缀
    private static final String OFFLINE_MESSAGE_KEY_PREFIX = "offline_message:";
    
    // 用户在线状态的Redis键前缀
    private static final String USER_ONLINE_KEY_PREFIX = "user_online:";
    
    // 群组成员列表的Redis键前缀
    private static final String GROUP_MEMBERS_KEY_PREFIX = "group_members:";
    
    /**
     * 保存离线消息到Redis
     *
     * @param userId  用户ID
     * @param message 消息内容
     * @return Mono<Void>
     */
    public Mono<Void> saveOfflineMessage(String userId, String message) {
        String key = OFFLINE_MESSAGE_KEY_PREFIX + userId;
        return reactiveRedisOperations.opsForList().leftPush(key, message)
                .then(reactiveRedisOperations.expire(key, Duration.ofDays(7)).then()); // 设置7天过期
    }
    
    /**
     * 获取用户的所有离线消息
     *
     * @param userId 用户ID
     * @return 消息列表
     */
    public Mono<java.util.List<String>> getOfflineMessages(String userId) {
        String key = OFFLINE_MESSAGE_KEY_PREFIX + userId;
        return reactiveRedisOperations.opsForList().range(key, 0, -1)
                .collectList();
    }
    
    /**
     * 删除用户的所有离线消息
     *
     * @param userId 用户ID
     * @return Mono<Void>
     */
    public Mono<Void> deleteOfflineMessages(String userId) {
        String key = OFFLINE_MESSAGE_KEY_PREFIX + userId;
        return reactiveRedisOperations.delete(key).then();
    }
    
    /**
     * 设置用户在线状态
     *
     * @param userId 用户ID
     * @return Mono<Void>
     */
    public Mono<Void> setUserOnline(String userId) {
        String key = USER_ONLINE_KEY_PREFIX + userId;
        return reactiveRedisOperations.opsForValue()
                .set(key, "true", Duration.ofHours(24))
                .then();
    }
    
    /**
     * 设置用户离线状态
     *
     * @param userId 用户ID
     * @return Mono<Void>
     */
    public Mono<Void> setUserOffline(String userId) {
        String key = USER_ONLINE_KEY_PREFIX + userId;
        return reactiveRedisOperations.delete(key).then();
    }
    
    /**
     * 检查用户是否在线
     *
     * @param userId 用户ID
     * @return Mono<Boolean>
     */
    public Mono<Boolean> isUserOnline(String userId) {
        String key = USER_ONLINE_KEY_PREFIX + userId;
        return reactiveRedisOperations.hasKey(key);
    }
    
    /**
     * 将用户添加到群组
     *
     * @param groupId 群组ID
     * @param userId  用户ID
     * @return Mono<Long> 添加后的成员数量
     */
    public Mono<Long> addUserToGroup(String groupId, String userId) {
        String key = GROUP_MEMBERS_KEY_PREFIX + groupId;
        return reactiveRedisOperations.opsForSet().add(key, userId);
    }
    
    /**
     * 将用户从群组中移除
     *
     * @param groupId 群组ID
     * @param userId  用户ID
     * @return Mono<Long> 移除后剩余的成员数量
     */
    public Mono<Long> removeUserFromGroup(String groupId, String userId) {
        String key = GROUP_MEMBERS_KEY_PREFIX + groupId;
        return reactiveRedisOperations.opsForSet().remove(key, userId);
    }
    
    /**
     * 获取群组所有成员
     *
     * @param groupId 群组ID
     * @return 群组成员列表
     */
    public Mono<java.util.List<String>> getGroupMembers(String groupId) {
        String key = GROUP_MEMBERS_KEY_PREFIX + groupId;
        return reactiveRedisOperations.opsForSet().members(key)
                .collectList();
    }
}