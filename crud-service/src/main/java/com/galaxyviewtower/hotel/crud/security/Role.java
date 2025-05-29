package com.galaxyviewtower.hotel.crud.security;

public enum Role {
    ROLE_CUSTOMER,
    ROLE_STAFF,
    ROLE_ADMIN;

    public String getAuthority() {
        return name();
    }
} 