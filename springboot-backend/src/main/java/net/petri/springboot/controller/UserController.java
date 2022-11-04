package net.petri.springboot.controller;

import java.util.List;
import net.petri.springboot.entity.User;
import net.petri.springboot.model.FM.UserFM;
import net.petri.springboot.model.VM.UserVM;
import net.petri.springboot.repository.UserRepository;
import net.petri.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping({"/api/users"})
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public List<UserVM> fetchUsers() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserVM find(@PathVariable Long id) {
        return userService.find(id);
    }

    @GetMapping("/email")
    public UserVM find(@RequestParam String email) {
        return userService.findByEmail(email);
    }

    @PostMapping("/login")
    public UserVM getUserByEmail(@RequestBody UserFM user) {
        return userService.login(user);
    }
}
