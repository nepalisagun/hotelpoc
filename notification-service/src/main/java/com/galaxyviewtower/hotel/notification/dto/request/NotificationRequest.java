package com.galaxyviewtower.hotel.notification.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "Notification request")
public class NotificationRequest {
    @NotBlank(message = "Template name is required")
    @Schema(description = "Name of the email template to use")
    private String templateName;

    @NotBlank(message = "Recipient email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "Recipient email address")
    private String recipientEmail;

    @Schema(description = "Additional template variables")
    private Map<String, Object> templateVariables;

    @Schema(description = "Notification type (EMAIL, SMS, etc.)")
    private String type = "EMAIL";
} 