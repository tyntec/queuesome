package com.tyntec.queuesome.queue.configuration;

import com.tyntec.queuesome.queue.repository.InMemoryQueueBackend;
import com.tyntec.queuesome.queue.repository.QueueBackendService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfiguration {

    @Bean
    QueueBackendService queueBackendService() {
        return new InMemoryQueueBackend();
    }
}
