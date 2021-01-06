package com.ht.project.snsproject.properites.aws;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Value
@ConstructorBinding
@ConfigurationProperties(prefix = "aws.s3")
@RequiredArgsConstructor
public class AwsS3Property {

  String accessKey;

  String secretKey;

  String bucketName;

  int transferThread;

}
