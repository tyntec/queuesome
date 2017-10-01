package com.tyntec.queuesome.queue.configuration;

import ai.api.AIConfiguration;
import ai.api.AIDataService;
import com.tyntec.queuesome.queue.repository.DummyPassphraseProvider;
import com.tyntec.queuesome.queue.repository.InMemoryQueueBackend;
import com.tyntec.queuesome.queue.repository.PassphraseProvider;
import com.tyntec.queuesome.queue.repository.QueueBackendService;
import com.tyntec.queuesome.queue.service.Notifier;
import com.tyntec.queuesome.queue.service.RestcommNotifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class QueueConfiguration {

    @Autowired
    Docket api;

    @Value("${ai.token}")
    String token;

    @Bean
    PassphraseProvider passphraseProvider() {
        return new DummyPassphraseProvider();
    }

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

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.tyntec.queuesome.queue.service"))
                .paths(PathSelectors.any())
                .build();
    }
}
