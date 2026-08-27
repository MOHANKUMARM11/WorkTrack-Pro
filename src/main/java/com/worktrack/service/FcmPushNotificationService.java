package com.worktrack.service;

public interface FcmPushNotificationService {

    boolean sendPushNotification(String deviceToken, String title, String body);

    boolean sendTopicNotification(String topic, String title, String body);
}
