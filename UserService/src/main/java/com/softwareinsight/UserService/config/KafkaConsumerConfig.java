package com.softwareinsight.UserService.config;

import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import com.softwareinsight.UserService.dto.UserEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.ConsumerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka Consumer Configuration
 * Design Pattern: Configuration Pattern
 */
@Configuration
@EnableKafka
public class KafkaConsumerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    /**
     * Consumer Factory for UserEvent
     */
    @Bean
    public ConsumerFactory<String, UserEvent> consumerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // Trust all packages for deserialization
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, UserEvent.class.getName());

        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                new JsonDeserializer<>(UserEvent.class, false)
        );
    }

    /**
     * Kafka Listener Container Factory
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UserEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}