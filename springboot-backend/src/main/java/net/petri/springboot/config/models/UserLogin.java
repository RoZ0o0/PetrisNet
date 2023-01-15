package net.petri.springboot.config.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLogin {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;

    public UserLogin(long id, String firstName, String lastName, String email, String role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
    }
}
