package com.ht.project.snsproject.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfig {
  /*
  서비스 레이어 단에서 오브젝트 매퍼를 지속적으로 초기화해주어 사용하면 비용이 크다.
  그러므로 일반적으로 빈(싱글톤) 으로 등록하여 사용한다.
  */
  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModules(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .registerModule(new ParameterNamesModule());

    return objectMapper;
  }
}
