package net.petri.springboot.components;

import net.petri.springboot.entity.SavedNets;
import net.petri.springboot.model.FM.SavedNetsFM;
import net.petri.springboot.repository.SavedNetsRepository;
import net.petri.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class SavedNetsValidator extends Validator {
    private final UserService userService;

    @Autowired
    public SavedNetsValidator(UserService userService) {
        this.userService = userService;
    }

    public boolean validateUserSave(SavedNetsFM savedNetsFM, Authentication authentication) {
        if (!checkNetUser(savedNetsFM, authentication)) {
            return false;
        }
        if (savedNetsFM.getSaveName().isEmpty() || savedNetsFM.getSaveName().length() < 3 || savedNetsFM.getSaveName().length() > 20) {
            return false;
        }

        return true;
    }

    public boolean checkNetUser(SavedNetsFM savedNetsFM, Authentication authentication) {
        if (!Objects.equals(userService.findByEmail(authentication.getName()).getId(), savedNetsFM.getUserId()) &&
                !Objects.equals(userService.findByEmail(authentication.getName()).getRole(), "ROLE_ADMIN")) {
            return false;
        }
        return true;
    }

    public boolean validateDelete(SavedNets savedNets, Authentication authentication) {
        if (!Objects.equals(userService.findByEmail(authentication.getName()).getId(), savedNets.getUser().getId()) &&
                !Objects.equals(userService.findByEmail(authentication.getName()).getRole(), "ROLE_ADMIN")) {
            return false;
        }

        return true;
    }
}
