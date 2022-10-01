package net.petri.springboot.service;

import java.util.List;
import java.util.Optional;
import net.petri.springboot.entity.User;
import net.petri.springboot.mapper.UserMapper;
import net.petri.springboot.model.VM.UserVM;
import net.petri.springboot.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;


import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @param userMapper
 */

@CrossOrigin
@Service

public record UserService(UserRepository userRepository,
                          UserMapper userMapper) {

    public List<UserVM> getAll() {
        List<User> entity = userRepository.findAll();

        List<UserVM> entities = userMapper.mapToList(entity);

        return entities;
    }

    public UserVM find(long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        User entity;
        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        entity = optionalUser.get();

        return userMapper.mapToVM(entity);
    }

    public UserVM findByEmail(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        User entity;
        if(optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        entity = optionalUser.get();

        return userMapper.mapToVM(entity);
    }
}