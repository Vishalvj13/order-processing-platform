package com.vishal.ordering.inventory.config;

import com.vishal.ordering.common.event.Topics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {

    @Bean
    public NewTopic orderCreatedTopic() {
        return TopicBuilder.name(Topics.ORDER_CREATED).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic inventoryReservedTopic() {
        return TopicBuilder.name(Topics.INVENTORY_RESERVED).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic inventoryFailedTopic() {
        return TopicBuilder.name(Topics.INVENTORY_FAILED).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic releaseInventoryTopic() {
        return TopicBuilder.name(Topics.RELEASE_INVENTORY).partitions(3).replicas(1).build();
    }
}
