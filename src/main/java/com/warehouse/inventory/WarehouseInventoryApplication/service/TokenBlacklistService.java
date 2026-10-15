package com.warehouse.inventory.WarehouseInventoryApplication.service;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    /*
     * why ConcurrentHashMap instead of HashMap?
     * thread-safe(i.e., one request can add a token while another request checks whether a token exists?)
     */
    private final Set<String> blacklistedTokens =
            ConcurrentHashMap.newKeySet();

    public void blacklistToken(String token) {
        blacklistedTokens.add(token);
    }

    public boolean isBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
}
