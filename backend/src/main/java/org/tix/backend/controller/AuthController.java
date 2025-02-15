package org.tix.backend.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tix.backend.dto.JwtAuthenticationResponse;
import org.tix.backend.dto.SignInRequest;
import org.tix.backend.dto.SignUpRequest;
import org.tix.backend.service.AuthService;

@RestController
@RequestMapping("/v1/user")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;

    }

    @PostMapping("/login")
    public JwtAuthenticationResponse signIn(@RequestBody SignInRequest request) {
        return authService.signIn(request);
    }

    @PostMapping("/create")
    public JwtAuthenticationResponse signUp(@RequestBody SignUpRequest request) {
        return authService.signUp(request);

    }
}
