package com.qritiooo.translationagency.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.List;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfiguration {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCacheNames(List.of(
                CacheNames.CLIENTS_ALL,
                CacheNames.DOCUMENTS_ALL,
                CacheNames.LANGUAGES_ALL,
                CacheNames.ORDERS_ALL,
                CacheNames.ORDERS_BY_TITLE,
                CacheNames.ORDERS_SEARCH_JPQL,
                CacheNames.ORDERS_SEARCH_NATIVE,
                CacheNames.TRANSLATORS_ALL
        ));
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1_000)
                .expireAfterWrite(Duration.ofMinutes(10))
                .recordStats());
        return cacheManager;
    }
}
