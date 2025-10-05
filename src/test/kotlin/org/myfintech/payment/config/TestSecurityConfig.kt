package org.myfintech.payment.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.web.SecurityFilterChain

@TestConfiguration
class TestSecurityConfig {
    @Bean
    @Throws(Exception::class)
    fun testSecurityFilterChain(http: HttpSecurity): SecurityFilterChain? {
        http
            .csrf(Customizer { csrf: CsrfConfigurer<HttpSecurity?>? -> csrf!!.disable() })
            .authorizeHttpRequests(Customizer { auth: AuthorizationManagerRequestMatcherRegistry? ->
                auth.anyRequest().permitAll()
            })
        return http.build()
    }
}
