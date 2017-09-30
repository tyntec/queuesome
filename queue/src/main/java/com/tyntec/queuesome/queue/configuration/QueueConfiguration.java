package com.tyntec.queuesome.queue.configuration;

import ai.api.AIConfiguration;
import ai.api.AIDataService;
import com.tyntec.queuesome.queue.repository.InMemoryQueueBackend;
import com.tyntec.queuesome.queue.repository.QueueBackendService;
import com.tyntec.queuesome.queue.service.Notifier;
import com.tyntec.queuesome.queue.service.RestcommNotifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfiguration {

    @Value("${ai.token}")
    String token;

    @Bean
    QueueBackendService queueBackendService(Notifier notifier) {
        return new InMemoryQueueBackend(notifier);
    }

    @Bean
    Notifier notifier() {
        return new RestcommNotifier();
    }

    @Bean
    AIConfiguration aiConfiguration() {
        return new AIConfiguration(token);
    }

    @Bean
    AIDataService aiDataService(AIConfiguration aiConfiguration) {
        return new AIDataService(aiConfiguration);
    }
}
