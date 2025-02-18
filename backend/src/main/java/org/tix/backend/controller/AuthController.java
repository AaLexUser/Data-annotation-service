package org.tix.backend.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tix.backend.dto.auth.JwtAuthenticationResponse;
import org.tix.backend.dto.auth.SignInRequest;
import org.tix.backend.dto.auth.SignUpRequest;
import org.tix.backend.service.AuthService;

@RestController
@RequestMapping("/api/v1/user")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;

    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse>  signIn(@RequestBody SignInRequest request, HttpServletResponse response) {
        JwtAuthenticationResponse jwtResponse = authService.signIn(request);
        setCookieForResponse(jwtResponse.getToken(), response);
        return new ResponseEntity<>(jwtResponse, HttpStatus.OK);

    }

    @PostMapping("/create")
    public ResponseEntity<JwtAuthenticationResponse> signUp(@RequestBody SignUpRequest request, HttpServletResponse response) {

        JwtAuthenticationResponse jwtResponse = authService.signUp(request);
        setCookieForResponse(jwtResponse.getToken(), response);
        return new ResponseEntity<>(jwtResponse, HttpStatus.OK);

    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(authService.getAllUsers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody SignUpRequest request) {
        authService.updateUser(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        authService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    private void setCookieForResponse(String jwt,HttpServletResponse response){
        Cookie cookie = new Cookie("access_token", jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(3600);
        response.addCookie(cookie);
    }
}
