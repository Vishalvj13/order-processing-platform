package com.vishal.ordering.notification.config;

import com.vishal.ordering.common.event.Topics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {

    @Bean
    public NewTopic orderNotificationTopic() {
        return TopicBuilder.name(Topics.ORDER_NOTIFICATION).partitions(3).replicas(1).build();
    }
}
