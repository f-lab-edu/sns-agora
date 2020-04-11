package com.ht.project.snsproject.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;



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

  @Autowired
  RedisConfig redisConfig;

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
            .fromConnectionFactory(redisConfig.redisConnectionFactory());
    RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                    .fromSerializer(new GenericJackson2JsonRedisSerializer()))
            .disableKeyPrefix()
            .entryTtl(Duration.ofHours(2L));//2시간 경과시 만료
    builder.cacheDefaults(configuration);

    return builder.build();
  }
}
