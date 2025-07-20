/*
 * Copyright (c) 2024 MyFintech Payment Service
 * All rights reserved.
 *
 * This software is proprietary and confidential. Unauthorized copying of this file,
 * via any medium, is strictly prohibited.
 */
package org.myfintech.payment.config;

import org.myfintech.payment.util.jaxb.JAXBContextPool;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Configuration for JAXB Context pooling.
 */
@Configuration
@EnableConfigurationProperties(JaxbPoolProperties.class)
public class JaxbConfiguration {
    
    @Bean
    public JAXBContextPool jaxbContextPool() {
        return new JAXBContextPool();
    }
}

/**
 * Configuration properties for JAXB pool.
 */
@ConfigurationProperties(prefix = "myfintech.jaxb.pool")
class JaxbPoolProperties {
    private int maxTotal = 20;
    private int maxIdle = 10;
    private int minIdle = 2;
    private long maxWaitMillis = 5000;
    private boolean testOnBorrow = true;
    private boolean testOnReturn = true;
    
    // Getters and setters
    public int getMaxTotal() { return maxTotal; }
    public void setMaxTotal(int maxTotal) { this.maxTotal = maxTotal; }
    
    public int getMaxIdle() { return maxIdle; }
    public void setMaxIdle(int maxIdle) { this.maxIdle = maxIdle; }
    
    public int getMinIdle() { return minIdle; }
    public void setMinIdle(int minIdle) { this.minIdle = minIdle; }
    
    public long getMaxWaitMillis() { return maxWaitMillis; }
    public void setMaxWaitMillis(long maxWaitMillis) { this.maxWaitMillis = maxWaitMillis; }
    
    public boolean isTestOnBorrow() { return testOnBorrow; }
    public void setTestOnBorrow(boolean testOnBorrow) { this.testOnBorrow = testOnBorrow; }
    
    public boolean isTestOnReturn() { return testOnReturn; }
    public void setTestOnReturn(boolean testOnReturn) { this.testOnReturn = testOnReturn; }
}