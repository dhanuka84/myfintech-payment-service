/*
 * Copyright (c) 2024 MyFintech Payment Service
 * All rights reserved.
 *
 * This software is proprietary and confidential. Unauthorized copying of this file,
 * via any medium, is strictly prohibited.
 * @author : Dhanuka Ranasinghe
 * @since : Date: 11/07/2025
 */
package org.myfintech.payment.config

import org.apache.catalina.connector.Connector
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@Configuration
class VirtualThreadConfig {
    @Value("\${use.virtual.threads:false}")
    private val useVirtualThreads = false

    @Bean(name = ["taskExecutor"]) // this is the key name Spring looks for
    fun taskExecutor(): Executor {
        if (useVirtualThreads) {
            return Executors.newVirtualThreadPerTaskExecutor()
        } else {
            val executor = ThreadPoolTaskExecutor()
            executor.setCorePoolSize(10)
            executor.setMaxPoolSize(100)
            executor.setQueueCapacity(500)
            executor.setThreadNamePrefix("payment-task-")
            executor.initialize()
            return executor
        }
    }

    @Bean
    fun tomcatVirtualThreadCustomizer(): WebServerFactoryCustomizer<ConfigurableServletWebServerFactory?> {
        return WebServerFactoryCustomizer { factory: ConfigurableServletWebServerFactory? ->
            if (useVirtualThreads
                && factory is TomcatServletWebServerFactory
            ) {
                factory.addConnectorCustomizers(
                    TomcatConnectorCustomizer { connector: Connector? ->
                        val handler = connector!!.getProtocolHandler()
                        handler.setExecutor(Executors.newVirtualThreadPerTaskExecutor())
                    })
            }
        }
    }
}
