package net.petri.springboot.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;
import net.petri.springboot.model.FM.UserFM;
import net.petri.springboot.model.VM.UserVM;
import net.petri.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

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

    @GetMapping("/fetchAll")
    public List<UserVM> fetchUsersPaginated(@RequestParam int page, @RequestParam int size) { return userService.getAllPaginated(page, size); }

    @GetMapping("/{id}")
    public UserVM find(@PathVariable Long id) {
        return userService.find(id);
    }

    @GetMapping("/email")
    public UserVM findByEmail(@RequestParam String email) { return userService.findByEmail(email); }

    @PostMapping("/register")
    public UserVM create(@RequestBody UserFM newEntity) throws MessagingException, UnsupportedEncodingException {
        return userService.create(newEntity);
    }

    @PutMapping("/{id}")
    public UserVM update(@PathVariable Long id, @RequestBody UserFM newEntity) {
        return userService.update(id, newEntity);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }

    @PutMapping("/profile")
    public UserVM editProfile(@RequestParam Long userId, @RequestBody UserFM newEntity, Authentication authentication) {
        return userService.editProfile(userId, newEntity, authentication);
    }
    
    @GetMapping("/search")
    public List<UserVM> search (@RequestParam int page, @RequestParam int size, @RequestParam String search) { return userService.search(page, size, search); }

    @PostMapping("/verify")
    public boolean verify(@RequestParam String code) { return userService.verify(code); }

    @PostMapping("/reset")
    public boolean reset(@RequestParam String code, @RequestParam String password) { return userService.reset(code, password); }

    @PostMapping("/resetMail")
    public void resetMail(@RequestParam String email) throws MessagingException, UnsupportedEncodingException {
        userService.resetPassword(email);
    }
}
