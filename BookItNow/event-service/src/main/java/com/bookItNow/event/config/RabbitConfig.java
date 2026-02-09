package com.bookItNow.event.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name="messaging.rabbit.enabled", havingValue = "true")
public class RabbitConfig {

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.queue.name}")
    private String queueName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    @Bean
    public DirectExchange seatExchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public Queue seatStatusUpdateQueue() {
        return new Queue(queueName,true);
    }

    @Bean
    public Binding seatStatusUpdateBinding() {
        return BindingBuilder.bind(seatStatusUpdateQueue())
                .to(seatExchange())
                .with(routingKey);
    }
}
