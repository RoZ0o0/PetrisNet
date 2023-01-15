package net.petri.springboot.controller;

import net.petri.springboot.config.LoginCredentials;
import net.petri.springboot.config.UserManager;

import net.petri.springboot.config.models.Role;
import net.petri.springboot.config.models.UserLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@CrossOrigin
@RestController
@RequestMapping(path = "/api/auth")
public class LoginController {
    private final UserManager userManager;

    @Autowired
    public LoginController(UserManager userManager) {
        this.userManager = userManager;
    }

    @PostMapping("/login")
    public void login(@RequestBody LoginCredentials credentials) {
    }

    @GetMapping(path = "/user")
    public UserLogin getUser() {
        return userManager.getLoginUser();
    }

    @GetMapping(path = "/user/role")
    public Role getUserRole() {
        return new Role(userManager.getRole());
    }
}
