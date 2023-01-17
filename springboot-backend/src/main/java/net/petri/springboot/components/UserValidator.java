package net.petri.springboot.components;

import net.petri.springboot.entity.User;
import net.petri.springboot.model.FM.UserFM;
import net.petri.springboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class  UserValidator extends Validator {
    private final UserRepository userRepository;

    @Autowired
    public UserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return false;
        }
        if (!emailCheck(user.getEmail())) {
            return false;
        }
        if (!passswordCheck(user.getPassword())) {
            return false;
        }
        if (user.getPassword().length() < 3 || user.getPassword().isEmpty() || user.getPassword().isBlank()){
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
