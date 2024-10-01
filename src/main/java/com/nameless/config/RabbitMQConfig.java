package com.nameless.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {


    public static final String RESULT_QUEUE_NAME = "resultQueue"; // Result Queue Name
    public static final String RESULT_EXCHANGE_NAME = "resultExchange"; // Result Exchange Name
    public static final String RESULT_ROUTING_KEY = "resultRoutingKey"; // Result Routing Key

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }


    @Bean
    public Queue resultQueue() {
        return new Queue(RESULT_QUEUE_NAME, true); // Durable result queue
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter()); // Set JSON message converter
        return rabbitTemplate;
    }


    // Define the Result Exchange
    @Bean
    public DirectExchange resultExchange() {
        return new DirectExchange(RESULT_EXCHANGE_NAME);
    }


    // Bind the Result Queue to the Result Exchange with a Routing Key
    @Bean
    public Binding resultBinding(Queue resultQueue, DirectExchange resultExchange) {
        return BindingBuilder.bind(resultQueue).to(resultExchange).with(RESULT_ROUTING_KEY);
    }
}