package com.bookItNow.booking.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name="messaging.rabbit.enabled", havingValue = "true")
public class RabbitConfig {
    // --- Queue ---
    @Value("${rabbitmq.payment.confirmed.queue}")
    private String PAYMENT_CONFIRMED_QUEUE;

    // --- Exchange ---
    @Value("${rabbitmq.seat.exchange}")
    private String SEAT_EXCHANGE;

    // --- Routing Key ---
    @Value("${rabbitmq.seat.status.update.routing.key}")
    private String SEAT_STATUS_UPDATE_ROUTING_KEY;

    // 1. Declare the queue to listen to ConfirmedPaymentEvent
    @Bean
    public Queue paymentConfirmedQueue() {
        return new Queue(PAYMENT_CONFIRMED_QUEUE, true); // durable queue
    }

    // 2. Declare the topic exchange for sending seat status updates
    @Bean
    public DirectExchange seatExchange() {
        return new DirectExchange(SEAT_EXCHANGE);
    }

    // 3. Bindings (in case you want to bind something to this exchange)
    @Bean
    public Binding bindingPaymentConfirmedQueue() {
        // This is optional if you're not publishing to this queue directly
        return BindingBuilder.bind(paymentConfirmedQueue()).to(seatExchange()).with(SEAT_STATUS_UPDATE_ROUTING_KEY);
        // Not used directly here but included for consistency
    }
}
