package org.tix.backend.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.tix.backend.model.User;
import org.tix.backend.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User create(User user) {
//        if (userRepository.existsByUsername((user.getUsername())) {
//            throw new RuntimeException("Пользователь с таким именем уже существует");
//        }
//
//        if (userRepository.existsByEmail((user.getEmail())) {
//            throw new RuntimeException("Пользователь с таким email уже существует");
//        }

        return save(user);
    }

    public User getByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

    }

    public UserDetailsService userDetailsService() {
        return this::getByLogin;
    }
}
