package org.tix.backend.dto;

import lombok.Data;

@Data
public class SignUpRequest {
    private String username;
    private String password;
}
