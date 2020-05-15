package com.ht.project.snsproject.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
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
  @Bean
  public RedisConnectionFactory sessionRedisConnectionFactory() {

    RedisStandaloneConfiguration redisStandaloneConfiguration =
            new RedisStandaloneConfiguration(host, port);
    redisStandaloneConfiguration.setPassword(password);

    LettuceConnectionFactory lettuceConnectionFactory =
            new LettuceConnectionFactory(redisStandaloneConfiguration);

    /*개발의 편의성을 위해 레디스의 논리적으로 database 를 분할하였습니다.
      실제 서비스 시에는 properties 에서 호스트를 변경해야만 합니다.
    */
    lettuceConnectionFactory.setDatabase(2);

    return lettuceConnectionFactory;
  }

  @Bean("sessionRedisTemplate")
  public RedisTemplate<String, Object> sessionRedisTemplate() {

    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(sessionRedisConnectionFactory());
    redisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
    redisTemplate.setKeySerializer(new StringRedisSerializer());

    return redisTemplate;
  }

  /*
  기존에 Spring Session DefaultRedisSerializer 가
  JdkSerializationRedisSerializer 로 설정 되어 있어서 Java Serialize 를 해야만 했으나,
  springSessionDefaultRedisSerializer 에 Jackson Serializer 를 주입 하면서
  추가로 Java Serialize 를 하지 않도록 설정했습니다.

  * Java Serialize 를 피해야 하는 이유
  - java Serialize 의 경우 가장 근본적인 문제는 공격 범위가 넓고 이는 지속적으로 더 넓어져서 방어가 어렵다는 접입니다.
  - ObjectInputStream 의 readObject 를 호출하면 객체 그래프가 역직렬화되기 때문에
    클래스패스 안의 거의 모든 타입의 객체를 만들어낼 수 있습니다.
    바이트 스트림을 역직렬화 하는 과정에서 메소드는 내부의 모든 메소드를 수행할 수 있으므로 전체가 공격 대상이 됩니다.
    신뢰할 수 없는 스트림을 역직렬화 하게 되면
    원격 코드 실행(Remote Code Execution), 서비스 거부(Dos) 등의 공격 대상이 될 수 있습니다.
    직렬화의 위험을 피하는 방법은 직렬화를 하지 않는 것입니다.
    하지만 직렬화를 해야한다면 JSON 이나 프로토콜 버퍼와 같은 대안을 사용하는 것을 추천합니다.
    이 또한 모든 공격을 막아줄 수는 없다는 것을 인식해야 합니다.
   */

  @Bean
  RedisSerializer<Object> springSessionDefaultRedisSerializer() {

    return new GenericJackson2JsonRedisSerializer();
  }


}
