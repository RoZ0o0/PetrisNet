package net.petri.springboot.config;

import net.petri.springboot.config.models.UserLogin;
import net.petri.springboot.entity.User;
import net.petri.springboot.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public record UserManager(UserRepository userRepository) {

    public UserLogin getLoginUser() {
        User user = userRepository.findByEmail(getEmail());
        return new UserLogin(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getRole());
    }

    public long getId() {
        return getLoginUser().getId();
    }

    public User getUser() {
        return userRepository.findByEmail(getEmail());
    }

    public String getRole() {
        Authentication auth = getAuth();
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        return authorities.toArray()[0].toString();
    }

    private Authentication getAuth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private String getEmail() {
        return getAuth().getPrincipal().toString();
    }
}
