/*
 * Copyright (c) 2024 MyFintech Payment Service
 * All rights reserved.
 *
 * This software is proprietary and confidential. Unauthorized copying of this file,
 * via any medium, is strictly prohibited.
 * @author : Dhanuka Ranasinghe
 * @since : Date: 11/07/2025
 */
package org.myfintech.payment.config;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.coyote.ProtocolHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class VirtualThreadConfig {

    @Value("${use.virtual.threads:false}")
    private boolean useVirtualThreads;

    @Bean(name = "taskExecutor") // this is the key name Spring looks for
    public Executor taskExecutor() {
        if (useVirtualThreads) {
            return Executors.newVirtualThreadPerTaskExecutor();
        } else {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(10);
            executor.setMaxPoolSize(100);
            executor.setQueueCapacity(500);
            executor.setThreadNamePrefix("payment-task-");
            executor.initialize();
            return executor;
        }
    }

    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory>
            tomcatVirtualThreadCustomizer() {
        return factory -> {
            if (useVirtualThreads
                    && factory instanceof TomcatServletWebServerFactory tomcatFactory) {
                tomcatFactory.addConnectorCustomizers(
                        connector -> {
                            ProtocolHandler handler = connector.getProtocolHandler();
                            handler.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
                        });
            }
        };
    }
}
