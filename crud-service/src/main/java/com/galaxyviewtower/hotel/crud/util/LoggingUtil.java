package com.galaxyviewtower.hotel.crud.util;

import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class LoggingUtil {

    private static final String REQUEST_ID = "requestId";
    private static final String USER_ID = "userId";
    private static final String SERVICE = "service";
    private static final String ENVIRONMENT = "environment";

    /**
     * Log a message with context
     *
     * @param logger  The logger to use
     * @param message The message to log
     * @param context The context map containing key-value pairs
     */
    public static void logWithContext(Logger logger, String message, Map<String, String> context) {
        try {
            context.forEach(MDC::put);
            logger.info(message);
        } finally {
            MDC.clear();
        }
    }

    /**
     * Log an error with context
     *
     * @param logger  The logger to use
     * @param message The message to log
     * @param error   The error to log
     * @param context The context map containing key-value pairs
     */
    public static void logErrorWithContext(Logger logger, String message, Throwable error, Map<String, String> context) {
        try {
            context.forEach(MDC::put);
            logger.error(message, error);
        } finally {
            MDC.clear();
        }
    }

    /**
     * Log a message with request context
     *
     * @param logger    The logger to use
     * @param message   The message to log
     * @param requestId The request ID
     * @param userId    The user ID
     */
    public static void logWithRequestContext(Logger logger, String message, String requestId, String userId) {
        try {
            MDC.put(REQUEST_ID, requestId);
            MDC.put(USER_ID, userId);
            logger.info(message);
        } finally {
            MDC.clear();
        }
    }

    /**
     * Log an error with request context
     *
     * @param logger    The logger to use
     * @param message   The message to log
     * @param error     The error to log
     * @param requestId The request ID
     * @param userId    The user ID
     */
    public static void logErrorWithRequestContext(Logger logger, String message, Throwable error, String requestId, String userId) {
        try {
            MDC.put(REQUEST_ID, requestId);
            MDC.put(USER_ID, userId);
            logger.error(message, error);
        } finally {
            MDC.clear();
        }
    }

    /**
     * Generate a unique request ID
     *
     * @return A unique request ID
     */
    public static String generateRequestId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Set the service name in the MDC
     *
     * @param serviceName The service name
     */
    public static void setServiceName(String serviceName) {
        MDC.put(SERVICE, serviceName);
    }

    /**
     * Set the environment in the MDC
     *
     * @param environment The environment name
     */
    public static void setEnvironment(String environment) {
        MDC.put(ENVIRONMENT, environment);
    }

    /**
     * Clear all MDC values
     */
    public static void clearContext() {
        MDC.clear();
    }
} 