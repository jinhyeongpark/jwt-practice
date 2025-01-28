package com.example.testJwt.jwt;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
public class TokenBlacklist {
    private Map<String, Long> blacklist = new HashMap<>();

    public void add(String token, long expirationTime) {
        blacklist.put(token, expirationTime);
    }

    public boolean contains(String token) {
        return blacklist.containsKey(token);
    }

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void removeExpiredTokens() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<String, Long>> iterator = blacklist.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Long> entry = iterator.next();
            if (entry.getValue() <= now) {
                iterator.remove();
            }
        }
    }
}


