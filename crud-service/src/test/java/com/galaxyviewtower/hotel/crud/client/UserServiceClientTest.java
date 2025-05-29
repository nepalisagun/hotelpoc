package com.galaxyviewtower.hotel.crud.client;

import com.galaxyviewtower.hotel.crud.dto.UserDTO;
import com.galaxyviewtower.hotel.crud.exception.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserServiceClientTest {

    @Autowired
    private UserServiceClient userServiceClient;

    @MockBean
    private WebClient userServiceClient;

    @MockBean
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @MockBean
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @MockBean
    private WebClient.ResponseSpec responseSpec;

    private UserDTO testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserDTO(
            "1",
            "test@example.com",
            "John",
            "Doe",
            "+1234567890",
            true
        );

        when(userServiceClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(String.class), any(String.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void getUserById_Success() {
        when(responseSpec.bodyToMono(UserDTO.class)).thenReturn(Mono.just(testUser));

        StepVerifier.create(userServiceClient.getUserById("1"))
                .expectNext(testUser)
                .verifyComplete();
    }

    @Test
    void getUserById_NotFound() {
        when(responseSpec.bodyToMono(UserDTO.class))
                .thenReturn(Mono.error(new ServiceException("User not found", HttpStatus.NOT_FOUND)));

        StepVerifier.create(userServiceClient.getUserById("999"))
                .expectComplete()
                .verify();
    }

    @Test
    void getUserById_ServiceError() {
        when(responseSpec.bodyToMono(UserDTO.class))
                .thenReturn(Mono.error(new ServiceException("Service unavailable", HttpStatus.SERVICE_UNAVAILABLE)));

        StepVerifier.create(userServiceClient.getUserById("1"))
                .expectError(ServiceException.class)
                .verify();
    }

    @Test
    void validateUser_Success() {
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(true));

        StepVerifier.create(userServiceClient.validateUser("1"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void validateUser_NotFound() {
        when(responseSpec.bodyToMono(Boolean.class))
                .thenReturn(Mono.error(new ServiceException("User not found", HttpStatus.NOT_FOUND)));

        StepVerifier.create(userServiceClient.validateUser("999"))
                .expectNext(false)
                .verifyComplete();
    }
} 