package com.gindho.authorization.service;
import com.gindho.authorization.model.*;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.TimeUnit;
@Service @Slf4j
public class PermissionCacheService {
    private Cache<String, List<Permission>> cache;
    @PostConstruct
    public void init() {
        this.cache = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();
    }
    public List<Permission> get(String userId) { return cache.getIfPresent(userId); }
    public void put(String userId, List<Permission> permissions) { cache.put(userId, permissions); }
    public void invalidate(String userId) { cache.invalidate(userId); }
    public void invalidateAll() { cache.invalidateAll(); }
}