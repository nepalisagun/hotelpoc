package com.galaxyviewtower.hotel.crud.config;

import io.r2dbc.h2.H2ConnectionConfiguration;
import io.r2dbc.h2.H2ConnectionFactory;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import java.net.URI;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@EnableR2dbcRepositories(basePackages = "com.galaxyviewtower.hotel.crud.repository")
@EnableR2dbcAuditing
public class R2dbcConfig extends AbstractR2dbcConfiguration {

  @Value("${spring.r2dbc.url}")
  private String url;

  @Value("${spring.r2dbc.username}")
  private String username;

  @Value("${spring.r2dbc.password}")
  private String password;

  @Value("${spring.r2dbc.pool.initial-size:5}")
  private int initialSize;

  @Value("${spring.r2dbc.pool.max-size:20}")
  private int maxSize;

  @Value("${spring.r2dbc.pool.max-idle-time:30m}")
  private Duration maxIdleTime;

  @Value("${spring.r2dbc.pool.max-acquire-time:30s}")
  private Duration maxAcquireTime;

  @Bean
  @Primary
  @Override
  public ConnectionFactory connectionFactory() {
    if (url.contains("h2")) {
      return h2ConnectionFactory();
    }
    return postgresqlConnectionFactory();
  }

  @Bean
  @Profile("!dev")
  public ConnectionFactory postgresqlConnectionFactory() {
    URI uri = URI.create(url.replace("r2dbc:", ""));

    PostgresqlConnectionConfiguration configuration =
        PostgresqlConnectionConfiguration.builder()
            .host(uri.getHost())
            .port(uri.getPort())
            .database(uri.getPath().substring(1)) // Remove leading slash
            .username(username)
            .password(password)
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    ConnectionPoolConfiguration poolConfig =
        ConnectionPoolConfiguration.builder()
            .connectionFactory(new PostgresqlConnectionFactory(configuration))
            .initialSize(initialSize)
            .maxSize(maxSize)
            .maxIdleTime(maxIdleTime)
            .maxAcquireTime(maxAcquireTime)
            .validationQuery("SELECT 1")
            .build();

    return new ConnectionPool(poolConfig);
  }

  @Bean
  @Profile("dev")
  public ConnectionFactory h2ConnectionFactory() {
    H2ConnectionConfiguration configuration =
        H2ConnectionConfiguration.builder()
            .inMemory("hoteldb")
            .username("sa")
            .password("")
            .build();

    ConnectionPoolConfiguration poolConfig =
        ConnectionPoolConfiguration.builder()
            .connectionFactory(new H2ConnectionFactory(configuration))
            .initialSize(initialSize)
            .maxSize(maxSize)
            .maxIdleTime(maxIdleTime)
            .maxAcquireTime(maxAcquireTime)
            .validationQuery("SELECT 1")
            .build();

    return new ConnectionPool(poolConfig);
  }

  @Bean
  public R2dbcEntityTemplate r2dbcEntityTemplate(ConnectionFactory connectionFactory) {
    return new R2dbcEntityTemplate(connectionFactory);
  }
}
