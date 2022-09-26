package net.petri.springboot.service;

import java.util.List;
import java.util.Optional;
import net.petri.springboot.entity.User;
import net.petri.springboot.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.server.ResponseStatusException;

@CrossOrigin
@Service
public record UserService(UserRepository userRepository) {
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAll() {
        List<User> entity = this.userRepository.findAll();
        return entity;
    }

    public Optional<User> find(long id) {
        if (!this.userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else {
            Optional<User> entity = this.userRepository.findById(id);
            return entity;
        }
    }

    public UserRepository userRepository() {
        return this.userRepository;
    }
}