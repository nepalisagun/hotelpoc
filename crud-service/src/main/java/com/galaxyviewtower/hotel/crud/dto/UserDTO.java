package com.galaxyviewtower.hotel.crud.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private boolean isActive;
} 