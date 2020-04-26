package com.ht.project.snsproject.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.RedisSessionRepository;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;

/**
 * Redis 설정.
 *
 * {@EnableRedisHttpSession}
 * Spring 의 SessionRepositoryFilter 를 Redis 가 지원하도록 만든다.
 * 해당 어노테이션을 활용하기 위해서는 @Configuration 클래스 내부에 하나 이상의 RedisConnectionFactory 가 제공되어야 한다.
 * maxInactiveIntervalInSeconds 속성은 세션의 만료 시간을 설정한다.
 * flushMode 속성 : Redis Session 의 FlushMode 를 설정할 수 있다.
 *                 기본값은 ON_SAVE 이다.호출 될 때만 Redis 에 업데이트한다.
 *                 웹 환경에서 이는 HTTP 응답이 커밋되기 직전에 발생한다.
 *                 값을 IMMEDIATE 로 설정하면 세션에 대한 모든 업데이트가 즉시 Redis 인스턴스에 작성된다.
 * redisNamespace 속성 : 세션을 저장하는 데에 key 값의 prefix 로 볼 수 있다. default 는 spring:session 이다.
 * cleanupCron 속성 : 만료 된 세션 정리 작업에 대한 cron 표현식으로 기본적으로 1 분마다 실행된다.
 * saveMode 속성 : redis session 의 Save Mode 를 설정한다. 기본은 ON_SET_ATTRIBUTE 이다.
 */
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 1800)
public class RedisSessionConfig extends AbstractHttpSessionApplicationInitializer {

  @Value("${redis.session.host}")
  private String host;

  @Value("${redis.session.password:@null}")
  private String password;

  @Value("${redis.session.port}")
  private int port;

  /**
   * Redis 에 접속하기 위한 Connection 들을 생성하는 팩토리 Object.
   * Thread-safe 하다.
   * 해당 프로젝트에서는 LettuceConnectionFactory 를 사용한다.
   * Lettuce 는 Netty (비동기 이벤트 기반 고성능 네트워크 프레임워크) 기반의 Redis 클라이언트로써
   * 비동기로 요청을 처리하기 때문에 고성능을 자랑합니다.
   * @return RedisConnectionFactory
   */

  /*
  현재는 임시로 primay 빈으로 등록하여 사용하고 있으나,
  해당 빈을 세션에 주입해야 하므로 명확하게는 Qualifier 설정을 하여 주입하는 것이
  맞다고 생각합니다.
  하지만 스프링 부트 내부에서 동작하는 빈을 설정하는 방법을 현재는 잘 모르는 상황이라
  조금 더 찾아 봐야 할 것 같습니다.
   */
  @Primary
  @Bean("sessionRedis")
  public RedisConnectionFactory redisSessionConnectionFactory() {

    RedisStandaloneConfiguration redisStandaloneConfiguration =
            new RedisStandaloneConfiguration(host, port);
    redisStandaloneConfiguration.setPassword(password);

    LettuceConnectionFactory lettuceConnectionFactory =
            new LettuceConnectionFactory(redisStandaloneConfiguration);

    return lettuceConnectionFactory;
  }
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
