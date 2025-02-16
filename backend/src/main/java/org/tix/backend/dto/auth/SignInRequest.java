package org.tix.backend.dto.auth;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class SignInRequest {
    private String username;
    private String password;
}
