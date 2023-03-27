package net.petri.springboot.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role;

    @Column(name= "verification_code")
    private String verificationCode;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "reset_token")
    private String resetToken;

    @OneToMany
    @JoinTable(name = "saved_user_nets")
    private List<SavedNets> savedNetsList;

}
