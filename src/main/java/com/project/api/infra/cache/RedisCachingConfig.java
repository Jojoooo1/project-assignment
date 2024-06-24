package com.project.api.infra.cache;

import static java.lang.String.format;

import com.project.api.clients.slack.SlackAlertClient;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisCommandTimeoutException;
import io.lettuce.core.SocketOptions;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RedisCachingConfig implements CachingConfigurer {

  public static final String NAME = "redisCacheManager";
  private final RedisConfig redisConfig;

  private final SlackAlertClient slack;

  @Bean
  public LettuceConnectionFactory redisConnectionFactory() {
    final SocketOptions so =
        SocketOptions.builder()
            .connectTimeout(Duration.ofMillis(this.redisConfig.getTimeoutInMs()))
            .build();

    final ClientOptions clientOptions = ClientOptions.builder().socketOptions(so).build();

    final LettuceClientConfiguration clientConfig =
        LettuceClientConfiguration.builder()
            .commandTimeout(Duration.ofMillis(this.redisConfig.getTimeoutInMs()))
            .clientOptions(clientOptions)
            .build();

    final RedisStandaloneConfiguration redisStandaloneConfiguration =
        new RedisStandaloneConfiguration();
    redisStandaloneConfiguration.setHostName(this.redisConfig.getHost());
    redisStandaloneConfiguration.setPort(this.redisConfig.getPort());
    redisStandaloneConfiguration.setPassword(this.redisConfig.getPassword());

    return new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfig);
  }

  @Bean
  public RedisTemplate<String, String> redisTemplate() {
    final RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(this.redisConnectionFactory());
    return redisTemplate;
  }

  @Bean
  public RedisCacheConfiguration cacheConfiguration() {
    return RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofSeconds(this.redisConfig.getDefaultTTL()));
  }

  @Bean(name = NAME)
  @Override
  public CacheManager cacheManager() {
    LettuceConnectionFactory cf = this.redisConnectionFactory();
    log.info("[REDIS] connection status '{}'", cf.getConnection().ping());
    return RedisCacheManager.builder(cf).cacheDefaults(this.cacheConfiguration()).build();
  }

  @Override
  public CacheErrorHandler errorHandler() {
    return new CacheErrorHandler() {
      @Override
      public void handleCacheGetError(
          final @NonNull RuntimeException exception,
          final @NonNull Cache cache,
          final @NonNull Object key) {
        this.processExceptions(exception);
      }

      @Override
      public void handleCachePutError(
          final @NonNull RuntimeException exception,
          final @NonNull Cache cache,
          final @NonNull Object key,
          final Object value) {
        this.processExceptions(exception);
      }

      @Override
      public void handleCacheEvictError(
          final @NonNull RuntimeException exception,
          final @NonNull Cache cache,
          final @NonNull Object key) {
        this.processExceptions(exception);
      }

      @Override
      public void handleCacheClearError(
          final @NonNull RuntimeException exception, final @NonNull Cache cache) {
        this.processExceptions(exception);
      }

      private void processExceptions(final @NonNull RuntimeException exception) {
        if (exception.getCause() instanceof RedisCommandTimeoutException) {
          final String errorMessage =
              format(
                  "[REDIS] Error connecting to redis with errorMessage '%s'",
                  exception.getMessage());
          RedisCachingConfig.this.slack.notify(errorMessage);
          log.error(errorMessage, exception);
        } else {
          final String errorMessage =
              format("[REDIS] exception caught with errorMessage '%s'", exception.getMessage());
          RedisCachingConfig.this.slack.notify(errorMessage);
          log.warn(errorMessage, exception);
        }
      }
    };
  }
}
