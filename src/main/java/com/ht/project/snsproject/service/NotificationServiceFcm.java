package com.ht.project.snsproject.service;

import com.google.firebase.messaging.*;
import com.ht.project.snsproject.Exception.NoSuchUserIdException;
import com.ht.project.snsproject.mapper.NotificationMapper;
import com.ht.project.snsproject.model.Notification.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

/**
 * Android Device ID test data 가 필요하므로,
 * 현재 메소드 호출 코드를 삽입하지 않음.
 * 이후 단위 테스트 구현시, 코드 삽입 예정.
 */

@Service
public class NotificationServiceFcm implements NotificationService{

    @Autowired
    NotificationMapper notificationMapper;

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceFcm.class);

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
