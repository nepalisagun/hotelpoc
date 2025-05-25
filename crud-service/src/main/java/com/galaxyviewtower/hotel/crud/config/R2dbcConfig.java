package com.galaxyviewtower.hotel.crud.config;

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
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@EnableR2dbcRepositories
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
  @Override
  public ConnectionFactory connectionFactory() {
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
}
