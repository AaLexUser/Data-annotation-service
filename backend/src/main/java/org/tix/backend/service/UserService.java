package org.tix.backend.service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.tix.backend.dto.AssessorDTO;
import org.tix.backend.dto.mapper.AssessorMapper;
import org.tix.backend.model.Batch;
import org.tix.backend.model.User;
import org.tix.backend.repository.BatchRepository;
import org.tix.backend.repository.UserRepository;
import org.tix.backend.util.Role;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BatchRepository batchRepository;
    private final AssessorMapper assessorMapper;

    public UserService(UserRepository userRepository, BatchRepository batchRepository, AssessorMapper assessorMapper) {
        this.userRepository = userRepository;
        this.batchRepository = batchRepository;
        this.assessorMapper = assessorMapper;
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User create(User user) {
            if (userRepository.existsByLogin((user.getUsername()))) {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }

        return save(user);
    }

    public User getByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
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

    public void assignBatchToAssessors(Long batchId, List<Long> assessorIds) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Batch not found"));
        
        List<User> assessors = userRepository.findAllById(assessorIds);
        if (assessors.size() != assessorIds.size()) {
            throw new RuntimeException("Some assessors were not found");
        }
        
        // Verify all users are assessors
        if (!assessors.stream().allMatch(user -> user.getRole() == Role.ASSESSOR)) {
            throw new RuntimeException("All users must be assessors");
        }
        
        List<User> currentAssessors = batch.getAvailableUsers();
        currentAssessors.addAll(assessors);
        batch.setAvailableUsers(currentAssessors);
        
        batchRepository.save(batch);
    }

    public void grantAssessorToBatch(Long batchId, Long userId) {
        Batch batch = batchRepository.findById(batchId).orElseThrow();
        List<User> tmp = batch.getAvailableUsers();
        tmp.add(userRepository.findById(userId).orElseThrow());
        batch.setAvailableUsers(tmp);
        batchRepository.save(batch);
    }

    public void refuseBatchToAssessor(Long batchId, Long userId) {
        Batch batch = batchRepository.findById(batchId).orElseThrow();
        List<User> tmp = batch.getAvailableUsers();
        tmp.remove(userRepository.findById(userId).orElseThrow());
        batch.setAvailableUsers(tmp);
        batchRepository.save(batch);
    }
}
