package org.tix.backend.service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.tix.backend.dto.AssessorDTO;
import org.tix.backend.dto.mapper.AssessorMapper;
import org.tix.backend.model.User;
import org.tix.backend.repository.UserRepository;
import org.tix.backend.util.Role;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final AssessorMapper assessorMapper;

    public UserService(UserRepository userRepository, AssessorMapper assessorMapper) {
        this.userRepository = userRepository;
        this.assessorMapper = assessorMapper;
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

    public Long countUsers() {
        return userRepository.count();
    }

    public List<AssessorDTO> getAllAssessors() {
        return userRepository.findAllByRole(Role.ASSESSOR).orElseThrow().stream().map(assessorMapper::toAssessorDTO).collect(Collectors.toList());
    }
}
