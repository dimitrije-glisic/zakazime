package com.dglisic.zakazime.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;

@Configuration
@ConditionalOnMissingBean(SecurityConfigurationDev.class)
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
                .requestMatchers(request -> request.getServletPath().startsWith("/admin/**")).hasRole("ADMIN")
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
