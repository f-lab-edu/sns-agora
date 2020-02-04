package com.ht.project.snsproject.config;


import com.ht.project.snsproject.model.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;

@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 1800)
public class RedisConfig extends AbstractHttpSessionApplicationInitializer {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.password:@null}")
    private String password;

    @Value("${spring.redis.port}")
    private int port;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // 서버 환경 설정
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(host, port);
        redisStandaloneConfiguration.setPassword(password);

        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration);
        return lettuceConnectionFactory;
    }

    @Bean
    public RedisTemplate<String, User> redisTemplate() {
        /*주어진 객체들 과 레디스 저장소 내부의 이진 데이터 사이에서 자동적으로 직렬화와 역직렬화를 수행하는 객체이다.
        Default 값으로 JDKSerializationRedisSerializer 를 사용한다.*/
        RedisTemplate<String, User> redisTemplate = new RedisTemplate<>();

        //setter 를 통한 RedisConnectionFactory 객체를 주입받는다. 여기서는 lettuce를 사용한다.
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        /* template 에서 사용될 key(map 에서의 key,value 중 key 에 해당) serializer 를 StringRedisSerializer 로 설정한다.
        StringRedisSerializer 는 String 타입의 테이터를 byte[] 타입으로 혹은 그 역으로 직렬화해주는 serializer 이다.*/
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        /*template 에서 사용될 value serializer 를 Jackson2JsonRedisSerializer 로 설정한다.
        Jackson 은 text/html 형태의 문자가 아닌 객체등의 데이터를 JSON 으로 처리(데이터 바인딩)해 주는 라이브러리이다.
        즉, Jackson 을 사용해서 JSON 을 읽고 쓸 수 있는 RedisSerializer 이다.
        이 변환기는 타입이 지정된 빈들 혹은 타입이 지정되지 않은 HashMap 인스턴스들을 바인딩 하는데 사용된다.
         */
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(User.class));

        return redisTemplate;
    }
}
