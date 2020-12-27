package com.ht.project.snsproject.properites.datasource;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Value
@ConstructorBinding
@ConfigurationProperties(prefix = "spring.datasource.master")
@RequiredArgsConstructor
public class MasterDatasourceProperty {

  String url;

  String username;

  String password;

  String driverName;
}
