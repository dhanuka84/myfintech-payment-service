package org.myfintech.payment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Profile({"!test & !non-security"})
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(authz -> authz
				.requestMatchers("/api-docs/**", "/v3/api-docs/**", "/error", "/v3/api-docs/swagger-config",
						"v3/api-docs/swagger-config/**", "/v3/api-docs/swagger-config/**", "/swagger-ui/**",
						"/swagger-ui.html", "/webjars/**", "/swagger-ui/index.html", "/configuration/ui",
						"/swagger-resources/**", "/swagger-resources/configuration/ui",
						"/swagger-resources/configuration/**")
				.permitAll() // Allow public access
				.anyRequest().authenticated()).oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

		return http.build();
	}

}
