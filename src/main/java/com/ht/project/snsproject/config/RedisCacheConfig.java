package com.ht.project.snsproject.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;


/**
 * {@EnableCaching}
 * 스프링 프레임워크에서 어노테이션 중심의 캐시 관리 기능을 지원합니다.
 * 스프링 3.1 이후로 추가되었습니다.
 * CacheManager 설정이 없으면 기본적으로 ConcurrentMapCache 를 사용한다.
 * 3가지 속성 mode, proxy-target-class, order 을 설정할 수 있다.
 * mode : 기본값인 "proxy" 모드는 스프링의 AOP 프레임워크로 어노테이션이 붙은 빈을 프록시한다.
 *        (프록시의 개념에 맞게 프록시를 통해서 메서드 호출에만 적용한다.)
 *        다른 모드인 "aspectj"는 영향받은 클래스를 스프링의 AspectJ 캐싱 관점으로 위빙한다.
 *        (대상 클래스의 바이트코드를 메서드 호출에 적용되도록 수정한다.)
 *        AspectJ 위빙을 사용하려면 로딩 타임 위빙(또는 컴파일 타임 위빙)
 *        활성화와 마찬가지로 클래스패스에 spring-aspects.jar 가 있어야 한다.
 * order: @Cacheable 나 @CacheEvict 어노테이션이 붙은 빈에 적용된 캐시 어드바이스의 순서를 정의한다.
 *        순서를 지정하지 않으면 AOP 하위시스템이 어드바이스의 순서를 결정한다.
 * proxy-target-class: proxy 모드에만 적용된다.
 *                     @Cacheable 나 @CacheEvict 어노테이션이 붙은 클래스에
 *                     어떤 종류의 캐싱 프록시를 생성할지를 제어한다.
 *                     proxy-target-class 속성을 true 로 설정하면 클래스에 기반을 둔 프록시를 생성한다.
 *                     proxy-target-class 가 false 이거나 proxy-target-class 속성을 생략하면
 *                     표준 JDK 인터페이스에 기반을 둔 프록시를 생성한다.
 */

@EnableCaching
@Configuration
public class RedisCacheConfig extends CachingConfigurerSupport {


  @Value("${redis.cache.host}")
  private String host;

  @Value("${redis.cache.password:@null}")
  private String password;

  @Value("${redis.cache.port}")
  private int port;


  @Bean("cacheRedis")
  public RedisConnectionFactory redisCacheConnectionFactory() {

    RedisStandaloneConfiguration redisStandaloneConfiguration =
            new RedisStandaloneConfiguration(host, port);
    redisStandaloneConfiguration.setPassword(password);

    LettuceConnectionFactory lettuceConnectionFactory =
            new LettuceConnectionFactory(redisStandaloneConfiguration);

    return lettuceConnectionFactory;
  }

  @Bean
  @Override
  public CacheManager cacheManager() {

    /*RedisCacheConfiguration javadoc 내용 기반
      disableKeyPrefix 적용시,
      캐시 관련 설정에 있어서 전용 redis 인스턴스를 사용해야한다고 명시되어 있는 것을 보아
      redisTemplate 으로 직접 설정해야한다고 이해하여 서비스에서도
      redisTemplate 으로 설정하였습니다.

      ttl 설정: 2시간으로 선정한 이유는 트래픽을 고려할 때 07:00~09:00(출근시간)
      11:00~13:00(점심시간), 18:00~20:00(퇴근 시간) 22:00~00:00(취침시간)에 가장 많이 접속할 것으로 판단하여
      캐시를 2시간으로 만료하기로 결정하였습니다.
      메모리 와 비용은 조금 더 생각해보겠습니다.
     */
    RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.RedisCacheManagerBuilder
            .fromConnectionFactory(redisCacheConnectionFactory());
    RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                    .fromSerializer(new GenericJackson2JsonRedisSerializer()))
            .disableKeyPrefix()
            .entryTtl(Duration.ofHours(2L));//2시간 경과시 만료
    builder.cacheDefaults(configuration);

    return builder.build();
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
  @Bean("cacheRedisTemplate")
  public RedisTemplate<String, Object> cacheRedisTemplate() {

    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(redisCacheConnectionFactory());
    redisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));

    return redisTemplate;
  }

  @Bean("cacheStrRedisTemplate")
  public StringRedisTemplate cacheStrRedisTemplate(){

    StringRedisTemplate strRedisTemplate = new StringRedisTemplate();
    strRedisTemplate.setConnectionFactory(redisCacheConnectionFactory());

    return strRedisTemplate;
  }
}
