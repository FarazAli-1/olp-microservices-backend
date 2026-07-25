package com.centegy.notification_service.service;


import com.centegy.common.dto.NotificationEventDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationReceiver {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    // fixedDelay means wait this many milliseconds after the method finishes before running it again
    @Scheduled(fixedDelay = 100)
    public void checkQueueForMessages() {
        try {
            ListOperations<String, String> ops = stringRedisTemplate.opsForList();

            // the thread will pause here for up to 30 seconds waiting for a message
            String jsonPayload = ops.rightPop("notificationQueue", Duration.ofSeconds(30));

            if (jsonPayload != null) {

                NotificationEventDto event = objectMapper.readValue(jsonPayload, NotificationEventDto.class);

                log.info("New {} Alert for User: {}", event.getNotificationType(), event.getRecipientEmail());
                log.info("Message: {}", event.getMessage());
            }
        } catch (Exception e) {
            log.error("Failed to pull from Redis: {}", e.getMessage());
        }
    }
}
