package net.petri.springboot.components;

import net.petri.springboot.entity.User;
import net.petri.springboot.mapper.UserMapper;
import net.petri.springboot.model.FM.UserFM;
import net.petri.springboot.model.VM.UserVM;
import net.petri.springboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class  UserValidator extends Validator {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserValidator(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public boolean validateUser(UserFM user) {
        if (user.getFirstName().isEmpty() || user.getFirstName().isBlank()) {
            return false;
        }
        if (user.getLastName().isEmpty() || user.getLastName().isBlank()) {
            return false;
        }
        if (user.getFirstName().length() < 3 || user.getLastName().length() <3) {
            return false;
        }
        if (!emailCheck(user.getEmail())) {
            return false;
        }

        return true;
    }

    public boolean validateCreateUser(UserFM user) {
        if (!validateUser(user)) {
            return false;
        }
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return false;
        }
        if (user.getPassword().length() < 3 || user.getPassword().isEmpty() || user.getPassword().isBlank()){
            return false;
        }
        if (!passswordCheck(user.getPassword())) {
            return false;
        }

        return true;
    }

    public boolean validateEditProfile(Long userId, UserFM user, Authentication authentication) {
        User userByEmail;
        Optional<User> userById;

        if (!user.getFirstName().isEmpty() && user.getFirstName().length() < 3) {
            return false;
        }
        if (user.getLastName() == null && (!user.getLastName().isEmpty() && user.getLastName().length() <3)) {
            return false;
        }
        if (!user.getEmail().isEmpty() && !emailCheck(user.getEmail())){
            return false;
        }
        if (!user.getPassword().isEmpty() && !passswordCheck(user.getPassword())) {
            return false;
        }
        if ((userById = userRepository.findById(userId)).isEmpty()) {
            return false;
        }
        if ((userByEmail = userRepository.findByEmail(authentication.getName())) == null) {
            return false;
        }
        if (userById.get() != userByEmail) {
            return false;
        }
        if (!user.getEmail().isEmpty() && (userRepository.findByEmail(user.getEmail()) != null && userRepository.findByEmail(user.getEmail()).getId() != userId)) {
            return false;
        }

        return true;
    }

    public UserFM setEditUser(UserFM user, User entity) {
        if (user.getFirstName().isEmpty()) {
            user.setFirstName(entity.getFirstName());
        }
        if (user.getLastName().isEmpty()) {
            user.setLastName(entity.getLastName());
        }
        if (user.getEmail().isEmpty()) {
            user.setEmail(entity.getEmail());
        }

        return user;
    }

    public boolean validateUpdateUser(UserFM user, Long id) {
        if (!validateUser(user)) {
            return false;
        }
        if (userRepository.findByEmail(user.getEmail()) != null && userRepository.findByEmail(user.getEmail()).getId() != id) {
            return false;
        }

        return true;
    }

    public boolean emailCheck(String email) {
        String regex = "^[A-Za-z-\\.]+[A-Za-z0-9-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();

    }

    public boolean passswordCheck(String password) {
        String regex = "^(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,16}$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);

        return matcher.matches();
    }
}
