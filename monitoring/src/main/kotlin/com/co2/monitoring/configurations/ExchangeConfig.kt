package com.co2.monitoring.configurations

import org.springframework.amqp.core.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ExchangeConfig(private val sensorQueue: Queue) {

    @Bean
    fun sensorExchange(): Exchange {
        return ExchangeBuilder.directExchange(exchange)
            .durable(true)
            .build()
    }

    @Bean
    fun sensorQueueBinding(): Binding {
        return BindingBuilder
            .bind(sensorQueue)
            .to(sensorExchange())
            .with(queue)
            .noargs()
    }
}
