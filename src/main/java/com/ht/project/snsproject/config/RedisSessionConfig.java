package com.ht.project.snsproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;

@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 1800)
public class RedisSessionConfig extends AbstractHttpSessionApplicationInitializer {

  /*
  기존에 Spring Session DefaultRedisSerializer 가
  JdkSerializationRedisSerializer 로 설정 되어 있어서 Java Serialize 를 해야만 했으나,
  springSessionDefaultRedisSerializer 에 Jackson Serializer 를 주입 하면서
  추가로 Java Serialize 를 하지 않도록 설정했습니다.
   */
  @Bean
  RedisSerializer<Object> springSessionDefaultRedisSerializer() {

    return new GenericJackson2JsonRedisSerializer();
  }
}
