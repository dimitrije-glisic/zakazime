package com.dglisic.zakazime.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfiguration {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
      .httpBasic(Customizer.withDefaults())
      .authorizeHttpRequests(authorize ->
        authorize
          .requestMatchers("/index.html", "/", "/home", "/login", "/register", "/user", "/resource", "/*.js",
            "/*.css").permitAll()
          .anyRequest().authenticated()
      )
      .csrf(csrf -> csrf.ignoringRequestMatchers("/logout").csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
      .build();
  }

}
