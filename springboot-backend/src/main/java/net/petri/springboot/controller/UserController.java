package net.petri.springboot.controller;

import java.util.List;
import net.petri.springboot.entity.User;
import net.petri.springboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping({"/api"})
public class UserController {
    @Autowired
    private UserRepository userRepository;

    public UserController() {
    }

    @GetMapping({"/users"})
    public List<User> fetchUsers() {
        return this.userRepository.findAll();
    }
}
