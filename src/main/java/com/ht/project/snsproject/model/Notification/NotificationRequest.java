package com.ht.project.snsproject.model.Notification;

import lombok.Value;

@Value
public class NotificationRequest {

    String targetId;

    String title;

    String message;

}
