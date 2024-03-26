package com.dglisic.zakazime.common.config.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;

@Configuration
//replace with profile instead of conditional
@ConditionalOnMissingBean(SecurityConfigurationDev.class)
@EnableMethodSecurity
public class SecurityConfiguration {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    CookieCsrfTokenRepository tokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
    XorCsrfTokenRequestAttributeHandler delegate = new XorCsrfTokenRequestAttributeHandler();

    return http
        .httpBasic(basic -> basic.securityContextRepository(new HttpSessionSecurityContextRepository()))
        .authorizeHttpRequests(authorize ->
            authorize
                .requestMatchers("/", "/home", "/login", "/register", "/user", "/error", "/dummy-post").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").authenticated()
                .requestMatchers(HttpMethod.GET).permitAll()
                .requestMatchers(HttpMethod.POST, "/business").permitAll()
                .requestMatchers(HttpMethod.POST, "/appointments").permitAll()
                .requestMatchers(HttpMethod.POST).authenticated()
                .requestMatchers(HttpMethod.PUT).authenticated()
                .requestMatchers(HttpMethod.DELETE).authenticated()
                .anyRequest().authenticated()
        )
        .csrf(csrf -> csrf
            .ignoringRequestMatchers("/register", "/login", "/logout")
            .csrfTokenRepository(tokenRepository)
            .csrfTokenRequestHandler(delegate::handle))
        .build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return NoOpPasswordEncoder.getInstance();
  }

}
