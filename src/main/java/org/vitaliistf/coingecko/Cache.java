package org.vitaliistf.coingecko;

import java.time.LocalDateTime;
import java.util.Optional;

public class Cache {

    private String marketsCache;
    private LocalDateTime marketsCacheLastRefresh;

    private static Cache INSTANCE;

    private Cache() {
        marketsCacheLastRefresh = LocalDateTime.MIN;
    }

    public static Cache getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new Cache();
        }
        return INSTANCE;
    }

    public Optional<String> getMarketsCache() {
        if(marketsCacheLastRefresh.plusMinutes(1).isBefore(LocalDateTime.now())) {
            return Optional.empty();
        }
        return Optional.of(marketsCache);
    }

    public void setMarketsCache(String marketsCache) {
        marketsCacheLastRefresh = LocalDateTime.now();
        this.marketsCache = marketsCache;
    }
}
