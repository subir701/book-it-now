package com.bookItNow.event.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Bean
    public RedissonClient redissonClient(){
        Config config = new Config();
        // Use "redis://" for non-SSL, "rediss://" for SSL
        config.useSingleServer()
                .setAddress("redis://"+redisHost+":"+redisPort)
                .setConnectionPoolSize(50)
                .setConnectionMinimumIdleSize(10);
        return Redisson.create(config);
    }
}
