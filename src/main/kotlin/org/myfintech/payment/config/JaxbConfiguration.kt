/*
 * Copyright (c) 2024 MyFintech Payment Service
 * All rights reserved.
 *
 * This software is proprietary and confidential. Unauthorized copying of this file,
 * via any medium, is strictly prohibited.
 */
package org.myfintech.payment.config

import org.myfintech.payment.util.jaxb.JAXBContextPool
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration for JAXB Context pooling.
 */
@Configuration
@EnableConfigurationProperties(JaxbPoolProperties::class)
class JaxbConfiguration {
    @Bean
    fun jaxbContextPool(): JAXBContextPool {
        return JAXBContextPool()
    }
}

/**
 * Configuration properties for JAXB pool.
 */
@ConfigurationProperties(prefix = "myfintech.jaxb.pool")
internal class JaxbPoolProperties {
    // Getters and setters
    var maxTotal: Int = 20
    var maxIdle: Int = 10
    var minIdle: Int = 2
    var maxWaitMillis: Long = 5000
    var isTestOnBorrow: Boolean = true
    var isTestOnReturn: Boolean = true
}