package com.dglisic.zakazime.common.config;

import jooq.tables.daos.BusinessAccountMapDao;
import jooq.tables.daos.BusinessDao;
import jooq.tables.daos.BusinessTypeDao;
import jooq.tables.daos.EmployeeDao;
import jooq.tables.daos.PredefinedCategoryDao;
import jooq.tables.daos.RoleDao;
import jooq.tables.daos.ServiceDao;
import jooq.tables.daos.UserDefinedCategoryDao;
import jooq.tables.daos.WorkingHoursDao;
import org.jooq.SQLDialect;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JooqConfiguration {

  @Bean
  public DefaultConfiguration configuration(final DataSourceConnectionProvider connectionProvider) {
    final DefaultConfiguration jooqConfiguration = new DefaultConfiguration();
    jooqConfiguration.setSQLDialect(SQLDialect.POSTGRES);
    jooqConfiguration.set(connectionProvider);

    return jooqConfiguration;
  }

  @Bean
  public DefaultDSLContext dsl(final DefaultConfiguration configuration) {
    return new DefaultDSLContext(configuration);
  }

  // ===================================================================================================================
  // DAO beans for each table
  // ===================================================================================================================

  @Bean
  public BusinessAccountMapDao businessAccountMapDao(final DefaultConfiguration configuration) {
    return new BusinessAccountMapDao(configuration);
  }

  @Bean
  public BusinessDao businessDao(final DefaultConfiguration configuration) {
    return new BusinessDao(configuration);
  }

  @Bean
  public BusinessTypeDao businessTypeDao(final DefaultConfiguration configuration) {
    return new BusinessTypeDao(configuration);
  }

  @Bean
  public RoleDao roleDao(final DefaultConfiguration configuration) {
    return new RoleDao(configuration);
  }

  @Bean
  public PredefinedCategoryDao predefinedCategoryDao(final DefaultConfiguration configuration) {
    return new PredefinedCategoryDao(configuration);
  }

  @Bean
  public UserDefinedCategoryDao userDefinedCategoryDao(final DefaultConfiguration configuration) {
    return new UserDefinedCategoryDao(configuration);
  }

  @Bean
  public ServiceDao serviceDao(final DefaultConfiguration configuration) {
    return new ServiceDao(configuration);
  }

  @Bean
  public EmployeeDao employeeDao(final DefaultConfiguration configuration) {
    return new EmployeeDao(configuration);
  }

  @Bean
  public WorkingHoursDao workingHoursDao(final DefaultConfiguration configuration) {
    return new WorkingHoursDao(configuration);
  }

}
