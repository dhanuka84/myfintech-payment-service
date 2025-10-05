package org.myfintech.payment

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.web.config.EnableSpringDataWebSupport
import org.springframework.scheduling.annotation.EnableAsync

/**
 * Main entry point for the MyFintech Payment Service.
 *
 * @author : Dhanuka Ranasinghe
 * @since : Date: 05/07/2025
 */
@EnableAsync
@SpringBootApplication
@EnableSpringDataWebSupport
class PaymentApplication

/**
 * Top-level main function to bootstrap the Spring Boot application.
 */
fun main(args: Array<String>) {
    runApplication<PaymentApplication>(*args)
}