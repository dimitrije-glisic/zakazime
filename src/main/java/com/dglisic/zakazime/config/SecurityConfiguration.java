package com.dglisic.zakazime.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;

@Configuration
public class SecurityConfiguration {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    CookieCsrfTokenRepository tokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
    XorCsrfTokenRequestAttributeHandler delegate = new XorCsrfTokenRequestAttributeHandler();

    return http
      .httpBasic(basic -> basic.securityContextRepository(new HttpSessionSecurityContextRepository()))
      .authorizeHttpRequests(authorize ->
        authorize
          .requestMatchers("/", "/home", "/login", "/register", "/user", "/users/**", "/resource", "/error").permitAll()
          .anyRequest().authenticated()
      )
//      .csrf(csrf -> csrf
//        .csrfTokenRepository(tokenRepository)
//        .csrfTokenRequestHandler(delegate::handle))
      .csrf(AbstractHttpConfigurer::disable)
      .build();
  }

}
