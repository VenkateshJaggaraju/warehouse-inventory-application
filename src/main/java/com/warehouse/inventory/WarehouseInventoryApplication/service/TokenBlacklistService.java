package com.warehouse.inventory.WarehouseInventoryApplication.service;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    // token -> expiry. ConcurrentHashMap: thread-safe across concurrent requests.
    private final Map<String, Date> blacklistedTokens = new ConcurrentHashMap<>();

    public void blacklistToken(String token, Date expiresAt) {
        purgeExpired();
        blacklistedTokens.put(token, expiresAt);
    }

    public boolean isBlacklisted(String token) {
        return blacklistedTokens.containsKey(token);
    }

    // An expired token is rejected anyway, so keep the map from growing forever.
    private void purgeExpired() {
        Date now = new Date();
        blacklistedTokens.values().removeIf(exp -> exp.before(now));
    }
}
