package net.petri.springboot.service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import net.petri.springboot.components.UserValidator;
import net.petri.springboot.config.DetailsUser;
import net.petri.springboot.entity.User;
import net.petri.springboot.mapper.UserMapper;
import net.petri.springboot.model.FM.UserFM;
import net.petri.springboot.model.VM.UserVM;
import net.petri.springboot.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;


import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @param userMapper
 */

@CrossOrigin
@Service

public record UserService(UserRepository userRepository, UserMapper userMapper,
                          UserValidator userValidator, BCryptPasswordEncoder bCryptPasswordEncoder) {

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
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return userMapper.mapToVM(user);
    }

    public UserVM create(UserFM newEntity) {
        if (!userValidator.validateCreateUser(newEntity)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        newEntity.setPassword(bCryptPasswordEncoder.encode(newEntity.getPassword()));

        newEntity.setRole("ROLE_USER");

        User entity = userMapper.mapToEntity(newEntity);
        userRepository.save(entity);
        return userMapper.mapToVM(entity);
    }


    public UserVM update(Long id, UserFM newEntity) {
        if (!userValidator().validateUpdateUser(newEntity, id)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        Optional<User> optionalUser = userRepository.findById(id);
        User entity;
        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        entity = optionalUser.get();

        newEntity.setPassword(entity.getPassword());

        userMapper.mapToEntity(entity, newEntity);
        userRepository.save(entity);

        return userMapper.mapToVM(entity);
    }

    public UserVM editProfile(Long userId, UserFM newEntity,Authentication authentication) {
        if (!userValidator.validateEditProfile(userId, newEntity, authentication)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        Optional<User> optionalUser = userRepository.findById(userId);
        User entity;
        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        entity = optionalUser.get();

        newEntity.setPassword(bCryptPasswordEncoder.encode(newEntity.getPassword()));

        userMapper.mapToEntity(entity, newEntity);
        userRepository.save(entity);

        return userMapper.mapToVM(entity);
    }

    public void delete(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        User entity;
        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        entity = optionalUser.get();
        userRepository.delete(entity);
    }
}