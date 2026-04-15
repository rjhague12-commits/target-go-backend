package com.targetgo.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String address;
    private String phone;
}
