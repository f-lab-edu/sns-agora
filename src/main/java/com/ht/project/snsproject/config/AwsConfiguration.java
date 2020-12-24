package com.ht.project.snsproject.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.ht.project.snsproject.properites.aws.AwsS3Property;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

/*
 * AWS S3 설정.
 *
 *@PropertySource : 해당 설정파일에는 aws key 라는 중요한 정보가 들어가 있기 때문에
 *                  프로퍼티를 설정하는 파일을 분리하여 설정한다.
 *                  해당 프로퍼티 설정파일은 .gitignore 에 등록해 git 에 올라가지 않게끔 만들어 준다.
 *
 *BasicAWSCredentials 는 호출자가 생성자에서 AWS Access key 와 Secret key 를 전달할 수 있는
 *AWSCredentials 인터페이스의 구현체이다.
 *AmazonS3 는 아마존 S3 web service 에 접근하기 위한 인터페이스이다.
 */

@Configuration
@RequiredArgsConstructor
public class AwsConfiguration {

  private final AwsS3Property awsS3Property;

  @Bean
  public BasicAWSCredentials awsCredentials() {

    return new BasicAWSCredentials(awsS3Property.getAccessKey(),
            awsS3Property.getSecretKey());
  }

  /**
   * 아마존 웹 서비스에 접근하기 위한 객체.
   * Spring Bean 으로 등록.
   * awsCredentials() 는 AWS 서비스에 액세스하는 데 사용되는 AWS 자격 증명
   * (AWS 액세스 키 ID 및 비밀 액세스 키)에 대한 액세스를 제공.
   * Regions 는 ENUM 으로 AP_NORTHEAST_2 가 가리키는 것은 아시아-태평양 서울 리전을 의미.
   * @return AmazonS3
   */
  @Bean
  public AmazonS3 amazonS3Client() {

    return AmazonS3ClientBuilder
            .standard()
            .withCredentials(new AWSStaticCredentialsProvider(this.awsCredentials()))
            .withRegion(Regions.AP_NORTHEAST_2)
            .build();
  }

  @Bean
  public TransferManager transferManager() {

    return TransferManagerBuilder.standard()
            .withS3Client(amazonS3Client())
            .withExecutorFactory(() -> createExecutorService(awsS3Property.getTransferThread())).build();
  }

  private ThreadPoolExecutor createExecutorService(int threadNumber) {
    ThreadFactory threadFactory = new ThreadFactory() {
      private int threadCount = 1;

      public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName("amazon-s3-transfer-manager-worker-" + threadCount++);
        return thread;
      }
    };
    return (ThreadPoolExecutor) Executors.newFixedThreadPool(threadNumber, threadFactory);
  }
}
