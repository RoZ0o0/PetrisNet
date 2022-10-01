package net.petri.springboot.model.VM;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserVM {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;

    public UserVM() {
    }

    public UserVM(Long id, String firstName, String lastName, String email, String role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
    }
}
