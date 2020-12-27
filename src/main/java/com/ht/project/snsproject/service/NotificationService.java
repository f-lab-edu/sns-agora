package com.ht.project.snsproject.service;

import com.ht.project.snsproject.model.notification.NotificationRequest;

public interface NotificationService {

  void sendPush(NotificationRequest notificationRequest);
}
