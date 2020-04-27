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
  public RedisConnectionFactory goodRedisConnectionFactory() {

    RedisStandaloneConfiguration redisStandaloneConfiguration =
            new RedisStandaloneConfiguration(host, port);

    redisStandaloneConfiguration.setPassword(password);

    LettuceConnectionFactory lettuceConnectionFactory =
            new LettuceConnectionFactory(redisStandaloneConfiguration);

    /*개발의 편의성을 위해 레디스의 논리적으로 database 를 분할하였습니다.
      실제 서비스 시에는 properties 에서 호스트를 변경해야만 합니다.
    */
    lettuceConnectionFactory.setDatabase(1);

    return lettuceConnectionFactory;
  }

  @Bean(name = "goodRedisTemplate")
  public RedisTemplate<String, Object> goodRedisTemplate() {

    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(goodRedisConnectionFactory());
    redisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
    redisTemplate.setKeySerializer(new StringRedisSerializer());

    return redisTemplate;
  }
}
