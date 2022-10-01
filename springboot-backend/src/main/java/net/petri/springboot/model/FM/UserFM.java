package net.petri.springboot.model.FM;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserFM {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String role;
}
