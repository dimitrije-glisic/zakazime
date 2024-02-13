package com.dglisic.zakazime.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticConfig implements WebMvcConfigurer {

  @Override
  public void addResourceHandlers(org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry registry) {
    registry
        .addResourceHandler("images/**")
        .addResourceLocations("file:///C:/Users/dglisic/personal-projects/storage/images/");
  }

}
