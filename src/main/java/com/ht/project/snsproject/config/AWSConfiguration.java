package com.ht.project.snsproject.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * AWS S3 설정
 *
 * @PropertySource : 해당 설정파일에는 aws key 라는 중요한 정보가 들어가 있기 때문에
 * 프로퍼티를 설정하는 파일을 분리하여 설정한다.
 * 해당 프로퍼티 설정파일은 .gitignore 에 등록해 git 에 올라가지 않게끔 만들어 준다.
 *
 * BasicAWSCredentials 는 호출자가 생성자에서 AWS Access key 와 Secret key 를 전달할 수 있는
 * AWSCredentials 인터페이스의 구현체이다.
 *
 * AmazonS3 는 아마존 S3 web service 에 접근하기 위한 인터페이스이다.
 */
@Configuration
@PropertySource("application-aws.properties")
public class AWSConfiguration {

    @Value("${aws.s3.accessKey}")
    private String accessKey;

    @Value("${aws.s3.secretKey}")
    private String secretKey;

    @Bean
    public BasicAWSCredentials awsCredentials(){
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey,secretKey);
        return credentials;
    }

    @Bean
    public AmazonS3 amazonS3Client() {
        AmazonS3 client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(this.awsCredentials()))
                .withRegion(Regions.AP_NORTHEAST_2)
                .build();
        return client;
    }

}
