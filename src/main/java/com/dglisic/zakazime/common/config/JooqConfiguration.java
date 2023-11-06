package com.dglisic.zakazime.common.config;

import org.jooq.SQLDialect;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JooqConfiguration {

  @Bean
  public DefaultConfiguration configuration(DataSourceConnectionProvider connectionProvider) {
    DefaultConfiguration jooqConfiguration = new DefaultConfiguration();
    jooqConfiguration.setSQLDialect(SQLDialect.POSTGRES);
    jooqConfiguration.set(connectionProvider);

    return jooqConfiguration;
  }

  @Bean
  public DefaultDSLContext dsl(DefaultConfiguration configuration) {
    return new DefaultDSLContext(configuration);
  }

}
