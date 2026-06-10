package com.campus.trade.service;

import org.springframework.stereotype.Service;

/**
 * 占位缓存服务：项目仅使用 MySQL，不依赖 Redis。
 * 查询始终走数据库，避免未安装 Redis 时出现「系统繁忙」。
 */
@Service
public class CacheService {

    public <T> T get(String key, Class<T> type) {
        return null;
    }

    public void set(String key, Object value, long minutes) {
        // no-op
    }

    public void evict(String key) {
        // no-op
    }

    public void evictByPrefix(String prefix) {
        // no-op
    }
}
