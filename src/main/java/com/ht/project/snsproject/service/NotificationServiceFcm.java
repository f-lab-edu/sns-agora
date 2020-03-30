package com.ht.project.snsproject.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import com.ht.project.snsproject.Exception.FcmInitializingException;
import com.ht.project.snsproject.Exception.NoSuchUserIdException;
import com.ht.project.snsproject.mapper.NotificationMapper;
import com.ht.project.snsproject.model.Notification.NotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Android Device ID test data 가 필요하므로,
 * 현재 메소드 호출 코드를 삽입하지 않음.
 * 이후 단위 테스트 구현시, 코드 삽입 예정.
 *
 * Message 객체: Firebase Cloud Messaging Service 에서 보낼 메시지 객체 입니다.
 * 메세지 객체에는 기본 알림, 안드로이드, 웹, Apple Device 등 기기 종류에 따른 전송 설정 필드가 존재합니다.
 * - AndroidConfig -
 *   ttl : 메시지의 수명기간을 설정하며,
 *         이 매개변수는 타깃 기기가 오프라인 상태일 때 FCM 저장소에 얼마나 보관될 지를 결정합니다.
 *         milliSeconds 단위로 표현됩니다.
 *   priority : 메시지의 우선순위로 NORMAL, HIGH 중 선택해야 합니다.
 *   notification : 안드로이드와 관련된 알림 객체입니다.
 *                  제목, 본문, 아이콘, 색상, 사운드 등을 설정할 수 있습니다.
 *   token : 디바이스 id 를 설정하여 메시지의 타깃을 설정합니다.
 *
 */

@Slf4j
@PropertySource("application-fcm.properties")
@Service
public class NotificationServiceFcm implements NotificationService{

    @Value("${fcm.database.name}")
    String databaseName;

    @Value("${fcm.service.account}")
    String serviceAccountPath;

    @Autowired
    NotificationMapper notificationMapper;

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceFcm.class);

    /*
    - Initialize 메소드
      오브젝트가 생성 되고, DI 작업을 마친 다음 실행 되는 메소드입니다.
      생성자에서 초기화 작업을 해도 되지만
      빈에서 주입받는 모든 의존관계 변수나 프로퍼티의 DI 작업이 끝난 시점 이후에 초기화를 해야 될 때
      사용합니다.
    - 초기화 방법
      1. @PostConstruct 애노테이션을 이용한 초기화
         해당 애노테이션을 적용한 메소드의 경우 모든 빈이 초기화된 직후에 한 번만 실행됩니다.
      2. InitializingBean 인터페이스를 구현한 초기화
         해당 인터페이스를 구현하고, afterPropertiesSet() 메소드를 Override 하면 초기화가 가능합니다.
      3. @Bean(initMethod = "init") : @Bean 애노테이션 속성에 initMethod 를 이용해서 초기화할 수 있습니다.
      4. XML 으로 하는 init-method : @Bean 애노테이션 설정과 동일합니다.
      이외의 생성자 주입, ApplicationListener, CommandLineRunner 구현(Spring Boot) 등이 있습니다.
     */
    @PostConstruct
    public void initialize() {
        try {
            FileInputStream serviceAccount = new FileInputStream(serviceAccountPath);

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl(databaseName)
                    .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                logger.info("Firebase application has been initialized");
            }
        } catch (IOException ioe) {
            throw new FcmInitializingException("FCM 초기화에 실패하였습니다.", ioe);
        }
    }

    public void sendPush(NotificationRequest notificationRequest){

        String deviceId = notificationMapper.getDeviceId(notificationRequest.getTargetId());

        if(deviceId.isEmpty()){
            throw new NoSuchUserIdException("해당 서비스에 등록된 사용자가 아닙니다.");
        }

        try {
            Message message = Message.builder()
                    .setAndroidConfig(AndroidConfig.builder()
                            .setTtl(3600 * 1000)
                            .setPriority(AndroidConfig.Priority.NORMAL)
                            .setNotification(AndroidNotification.builder()
                                    .setTitle(notificationRequest.getTitle())
                                    .setBody(notificationRequest.getMessage())
                                    .build())
                            .build())
                    .setToken(deviceId)
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            logger.info("Successfully sent message: " + response);
        }catch (FirebaseMessagingException fme){
            logger.error(fme.getErrorCode());
        }
    }
}
