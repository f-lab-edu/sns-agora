package com.ht.project.snsproject.model.notification;

import lombok.Value;

@Value
public class NotificationRequest {

  String targetId;

  String title;

  String message;

}
