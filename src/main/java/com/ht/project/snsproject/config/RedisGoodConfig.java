package com.ht.project.snsproject.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisGoodConfig {

  @Value("${redis.good.host}")
  private String host;

  @Value("${redis.good.password:@null}")
  private String password;

  @Value("${redis.good.port}")
  private int port;

  @Bean("goodRedis")
  public RedisConnectionFactory redisGoodConnectionFactory() {

    RedisStandaloneConfiguration redisStandaloneConfiguration =
            new RedisStandaloneConfiguration(host, port);

    redisStandaloneConfiguration.setPassword(password);

    LettuceConnectionFactory lettuceConnectionFactory =
            new LettuceConnectionFactory(redisStandaloneConfiguration);

    return lettuceConnectionFactory;
  }

  @Bean(name = "goodRedisTemplate")
  public RedisTemplate<String, Object> goodRedisTemplate() {

    RedisTemplate<String, Object> redisTemplate2 = new RedisTemplate<>();
    redisTemplate2.setConnectionFactory(redisGoodConnectionFactory());
    redisTemplate2.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
    redisTemplate2.setKeySerializer(new StringRedisSerializer());

    return redisTemplate2;
  }
}
