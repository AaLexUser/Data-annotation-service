package org.tix.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.tix.backend.dto.auth.JwtAuthenticationResponse;
import org.tix.backend.dto.auth.SignInRequest;
import org.tix.backend.dto.auth.SignUpRequest;
import org.tix.backend.model.User;
import org.tix.backend.util.Role;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    public void updateUser(Long id, SignUpRequest request) {
        User user = userService.getById(id);
        user.setLogin(request.getUsername());
        if (!request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        userService.save(user);
    }

    public void deleteUser(Long id) {
        User user = userService.getById(id);
        if (user.getRole() == Role.ADMIN) {
            throw new RuntimeException("Cannot delete admin user");
        }
        userService.deleteUser(id);
    }

    public JwtAuthenticationResponse signIn(SignInRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));

        var user = userService
                .userDetailsService()
                .loadUserByUsername(request.getUsername());

        return jwtAuthenticationResponseBuilder(userService.getByLogin(user.getUsername()));
    }

    public JwtAuthenticationResponse signUp(SignUpRequest request) {
        boolean isFirstUser = userService.countUsers() == 0;

        var user = User.builder()
                .login(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(isFirstUser ? Role.ADMIN : Role.ASSESSOR)
                .build();

        userService.create(user);

        return jwtAuthenticationResponseBuilder(user);
    }

    private JwtAuthenticationResponse jwtAuthenticationResponseBuilder(User user) {
        JwtAuthenticationResponse response = new JwtAuthenticationResponse();
        response.setToken(jwtService.generateToken(user));
        response.setUsername(user.getUsername());
        response.setRole(userService.getByLogin(user.getUsername()).getRole());
        response.setUserId(userService.getByLogin(user.getUsername()).getId());
        return response;
    }
}
