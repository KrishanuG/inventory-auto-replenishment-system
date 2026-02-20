package com.krishanu.inventory.inventory_service.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@Configuration
public class KafkaErrorHandlerConfig {

    @Bean
    public DefaultErrorHandler errorHandler() {

        FixedBackOff fixedBackOff = new FixedBackOff(2000L, 3); //3 retries in 2 sec gap

        return new DefaultErrorHandler(
                (record, exception) -> {
                    log.error("Retry exhausted for record with key: {}, value: {}, partition: {}, offset: {}. Error: {}",
                            record.key(),
                            record.value(),
                            record.partition(),
                            record.offset(),
                            exception.getMessage(),
                            exception
                    );
                },
                fixedBackOff
        );
    }
}
