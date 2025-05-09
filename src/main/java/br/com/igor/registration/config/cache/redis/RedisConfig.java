package br.com.igor.registration.config.cache.redis;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import java.time.Duration;
import java.util.Map;

@Configuration
public class RedisConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10)) // tempo de vida padrão do cache
                .enableTimeToIdle() // reinicia tempo de vida a cada acesso do cache
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())); // Configura o serializador de chave

        // cache com tempo de vida (TTL) personalizado por nome (chave)
        Map<String, RedisCacheConfiguration> lifeEachCache = Map.of(
                                                        "allUsers", config.entryTtl(Duration.ofMinutes(5)),
                                                        "userById", config.entryTtl(Duration.ofMinutes(15)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .transactionAware() // permite rollback
                .withInitialCacheConfigurations(lifeEachCache)
                .build();
    }
}
