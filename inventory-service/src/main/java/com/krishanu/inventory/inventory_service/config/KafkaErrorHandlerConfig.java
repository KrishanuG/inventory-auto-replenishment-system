package com.krishanu.inventory.inventory_service.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KafkaErrorHandlerConfig {

    @Value("${app.kafka.topics.stock-event-dlt}")
    private String deadLetterTopic;

    private final KafkaTemplate<String, String> kafkaTemplate;


    @Bean
    public DefaultErrorHandler errorHandler() {

        FixedBackOff fixedBackOff = new FixedBackOff(2000L, 3); //3 retries in 2 sec gap
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, exception) -> {
                    log.error("Retry exhausted for record with key: {}, value: {}, partition: {}, offset: {}. Error: {}",
                            record.key(),
                            record.value(),
                            record.partition(),
                            record.offset(),
                            exception.getMessage(),
                            exception);

                    return new TopicPartition(deadLetterTopic, record.partition());
                }
        );
        return new DefaultErrorHandler(recoverer, fixedBackOff);
    }
}
