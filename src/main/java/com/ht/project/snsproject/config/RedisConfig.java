package com.ht.project.snsproject.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.session.data.redis.config.annotation.SpringSessionRedisOperations;
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
public class RedisConfig {

  @Value("${spring.redis.host}")
  private String host;

  @Value("${spring.redis.password:@null}")
  private String password;

  @Value("${spring.redis.port}")
  private int port;

  /**
   * Redis 에 접속하기 위한 Connection 들을 생성하는 팩토리 Object.
   * Thread-safe 하다.
   * 해당 프로젝트에서는 LettuceConnectionFactory 를 사용한다.
   * Lettuce 는 Netty (비동기 이벤트 기반 고성능 네트워크 프레임워크) 기반의 Redis 클라이언트로써
   * 비동기로 요청을 처리하기 때문에 고성능을 자랑합니다.
   * @return RedisConnectionFactory
   */
  @Bean
  public RedisConnectionFactory redisConnectionFactory() {

    RedisStandaloneConfiguration redisStandaloneConfiguration =
            new RedisStandaloneConfiguration(host, port);
    redisStandaloneConfiguration.setPassword(password);

    LettuceConnectionFactory lettuceConnectionFactory =
            new LettuceConnectionFactory(redisStandaloneConfiguration);

    return lettuceConnectionFactory;
  }

  /**
   * RedisTemplate : RedisConnection 은 row level 의 메서드를 제공하는 반면
   *                 RedisTemplate 은 커넥션 위에서 값을 조작하는 메서드 제공한다.
   *                 주어진 객체들과 레디스 저장소 내부의 이진 데이터 사이에서 자동적으로 직렬화와 역직렬화를 수행한다.
   *                 RedisCallback interface 를 구현하여 Redis 접근을 지원하는 메소드를 실행한다.
   *                 RedisTemplate 은 RedisCallback 구현체나 호출한 코드들이 명시적으로 RedisConnection 을 찾거나,
   *                 닫는 것을 신경쓰지 않고 혹은 Connection 생명주기 예외를 다루지 않도록 Redis Connection 처리를 제공한다.
   *                 일단 구성이 되면 이 클래스는 Thread-safe 하다.
   *                 Default 값으로 JDKSerializationRedisSerializer 를 사용한다.
   * StringRedisTemplate: 대부분 레디스 key-value 는 문자열 위주이기 때문에 문자열에 특화된 템플릿을 제공한다.
   *                      RedisTemplate 을 상속받은 클래스이다.
   *                      StringRedisSerializer 로 직렬화한다.
   * setConnectionFactory() : setter 를 통한 RedisConnectionFactory 객체를 주입받는다.
   *                          여기서는 lettuce 를 사용한다.
   * setKeySerializer() : template 에서 사용될 key(map 에서의 key,value 중 key 에 해당)
   *                      serializer 를 StringRedisSerializer 로 설정한다.
   *                      StringRedisSerializer 는 String 타입의 테이터를 byte[] 타입으로
   *                      혹은 그 역으로 직렬화해주는 serializer 이다.
   * setValueSerializer() : template 에서 사용될 value serializer 를 Jackson2JsonRedisSerializer 로 설정한다.
   *                        Jackson 은 text/html 형태의 문자가 아닌 객체등의 데이터를
   *                        JSON 으로 처리(데이터 바인딩)해 주는 라이브러리이다.
   *                        즉, Jackson 을 사용해서 JSON 을 읽고 쓸 수 있는 RedisSerializer 이다.
   *                        이 변환기는 타입이 지정된 빈들 혹은 타입이 지정되지 않은 HashMap 인스턴스들을
   *                        바인딩 하는데 사용된다.
   * @return RedisTemplate
   */
  @Bean
  public RedisTemplate<String, Object> redisTemplate() {

    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(redisConnectionFactory());
    redisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));

    return redisTemplate;
  }

/*
  spring session document 확인해서 추가 설정.
  java serialize 는 피해야한다. 호환성, 성능 면에서 떨어진다.
 */
  @Bean
  public StringRedisTemplate strRedisTemplate(){

    StringRedisTemplate strRedisTemplate = new StringRedisTemplate();
    strRedisTemplate.setConnectionFactory(redisConnectionFactory());

    return strRedisTemplate;
  }
}
