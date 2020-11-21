package com.ht.project.snsproject.properites.redis;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Value
@ConstructorBinding
@ConfigurationProperties(prefix = "redis.session")
@RequiredArgsConstructor
public class SessionRedisProperty {

  String host;

  String password;

  int port;
}
