package com.worktrack.serviceImpl;

import com.worktrack.service.FcmPushNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FcmPushNotificationServiceImpl implements FcmPushNotificationService {

    @Override
    public boolean sendPushNotification(String deviceToken, String title, String body) {
        log.info("Sending FCM push notification to token {}: title='{}', body='{}'", deviceToken, title, body);
        return true;
    }

    @Override
    public boolean sendTopicNotification(String topic, String title, String body) {
        log.info("Sending FCM topic push notification to topic {}: title='{}', body='{}'", topic, title, body);
        return true;
    }
}
