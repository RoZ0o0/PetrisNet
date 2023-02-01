package net.petri.springboot.repository;

import net.petri.springboot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>{

    List<User> findAll();

    Optional<User> findById(Long id);

    User findByEmail(String email);

}
