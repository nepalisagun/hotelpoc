package com.galaxyviewtower.hotel.crud.config;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.reactor.bulkhead.operator.BulkheadOperator;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.util.function.Function;

@Configuration
public class WebClientConfig {

    @Value("${services.user-service.url}")
    private String userServiceUrl;

    @Value("${services.booking-service.url}")
    private String bookingServiceUrl;

    @Value("${services.payment-service.url}")
    private String paymentServiceUrl;

    private final MeterRegistry meterRegistry;

    public WebClientConfig(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Bean
    public WebClient userServiceWebClient() {
        return createWebClient(userServiceUrl, "userService");
    }

    @Bean
    public WebClient bookingServiceWebClient() {
        return createWebClient(bookingServiceUrl, "bookingService");
    }

    @Bean
    public WebClient paymentServiceWebClient() {
        return createWebClient(paymentServiceUrl, "paymentService");
    }

    private WebClient createWebClient(String baseUrl, String serviceName) {
        ConnectionProvider provider = ConnectionProvider.builder("fixed")
            .maxConnections(500)
            .maxIdleTime(Duration.ofSeconds(60))
            .maxLifeTime(Duration.ofMinutes(5))
            .pendingAcquireTimeout(Duration.ofSeconds(60))
            .evictInBackground(Duration.ofSeconds(120))
            .build();

        HttpClient httpClient = HttpClient.create(provider)
            .responseTimeout(Duration.ofSeconds(5))
            .keepAlive(true);

        return WebClient.builder()
            .baseUrl(baseUrl)
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .filter(logRequest())
            .filter(logResponse())
            .filter(timeoutFilter())
            .build();
    }

    @Bean
    public CircuitBreaker userServiceCircuitBreaker() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
            .failureRateThreshold(50)
            .waitDurationInOpenState(Duration.ofSeconds(60))
            .permittedNumberOfCallsInHalfOpenState(10)
            .slidingWindowSize(100)
            .minimumNumberOfCalls(10)
            .recordExceptions(Exception.class)
            .build();

        return CircuitBreaker.of("userService", config, meterRegistry);
    }

    @Bean
    public Retry userServiceRetry() {
        RetryConfig config = RetryConfig.custom()
            .maxAttempts(3)
            .waitDuration(Duration.ofSeconds(1))
            .retryExceptions(IllegalStateException.class)
            .ignoreExceptions(IllegalArgumentException.class)
            .build();

        return Retry.of("userService", config, meterRegistry);
    }

    @Bean
    public TimeLimiter userServiceTimeLimiter() {
        TimeLimiterConfig config = TimeLimiterConfig.custom()
            .timeoutDuration(Duration.ofSeconds(5))
            .cancelRunningFuture(true)
            .build();

        return TimeLimiter.of("userService", config, meterRegistry);
    }

    @Bean
    public Bulkhead userServiceBulkhead() {
        BulkheadConfig config = BulkheadConfig.custom()
            .maxConcurrentCalls(20)
            .maxWaitDuration(Duration.ofMillis(500))
            .build();

        return Bulkhead.of("userService", config, meterRegistry);
    }

    @Bean
    public RateLimiter userServiceRateLimiter() {
        RateLimiterConfig config = RateLimiterConfig.custom()
            .limitForPeriod(100)
            .limitRefreshPeriod(Duration.ofSeconds(1))
            .timeoutDuration(Duration.ofMillis(500))
            .build();

        return RateLimiter.of("userService", config, meterRegistry);
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            System.out.println("Request: " + clientRequest.method() + " " + clientRequest.url());
            return Mono.just(clientRequest);
        });
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            System.out.println("Response status: " + clientResponse.statusCode());
            return Mono.just(clientResponse);
        });
    }

    private ExchangeFilterFunction timeoutFilter() {
        return (request, next) -> next.exchange(request)
            .timeout(Duration.ofSeconds(5))
            .onErrorResume(throwable -> {
                if (throwable instanceof java.util.concurrent.TimeoutException) {
                    return Mono.error(new RuntimeException("Request timed out"));
                }
                return Mono.error(throwable);
            });
    }

    public <T> Function<Mono<T>, Mono<T>> applyResiliencePatterns(String serviceName) {
        return mono -> mono
            .transform(CircuitBreakerOperator.of(userServiceCircuitBreaker()))
            .transform(RetryOperator.of(userServiceRetry()))
            .transform(BulkheadOperator.of(userServiceBulkhead()))
            .transform(RateLimiterOperator.of(userServiceRateLimiter()))
            .timeout(Duration.ofSeconds(5))
            .onErrorResume(throwable -> {
                if (throwable instanceof java.util.concurrent.TimeoutException) {
                    return Mono.error(new RuntimeException(serviceName + " service timed out"));
                }
                return Mono.error(throwable);
            });
    }

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        .slidingWindowSize(10)
                        .failureRateThreshold(50)
                        .waitDurationInOpenState(Duration.ofSeconds(10))
                        .permittedNumberOfCallsInHalfOpenState(5)
                        .build())
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(2))
                        .build())
                .build());
    }

    private ExchangeFilterFunction errorHandler() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().is5xxServerError()) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono.error(new ServiceException(
                                "Service error: " + errorBody,
                                clientResponse.statusCode())));
            }
            if (clientResponse.statusCode().is4xxClientError()) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono.error(new ServiceException(
                                "Client error: " + errorBody,
                                clientResponse.statusCode())));
            }
            return Mono.just(clientResponse);
        });
    }

    private ExchangeFilterFunction jwtPropagationFilter() {
        return (request, next) -> {
            // Get JWT from the current request context
            String jwt = getJwtFromContext();
            if (jwt != null) {
                ClientRequest filtered = ClientRequest.from(request)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                        .build();
                return next.exchange(filtered);
            }
            return next.exchange(request);
        };
    }

    private String getJwtFromContext() {
        // TODO: Implement JWT extraction from the current request context
        // This will be implemented when we add security
        return null;
    }
} 