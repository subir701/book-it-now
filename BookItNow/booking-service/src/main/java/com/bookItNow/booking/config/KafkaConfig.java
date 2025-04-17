package com.bookItNow.booking.config;

import com.bookItNow.common.event.CancelReservationEvent;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnProperty(name=" messaging.kafka.enabled", havingValue = "true")
public class KafkaConfig {

    @Bean
    public ProducerFactory<String, CancelReservationEvent> producerFactory() {
        Map<String,Object> configProps = new HashMap<String,Object>();

        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return new DefaultKafkaProducerFactory<String, CancelReservationEvent>(configProps);
    }

    @Bean
    public KafkaTemplate<String, CancelReservationEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
