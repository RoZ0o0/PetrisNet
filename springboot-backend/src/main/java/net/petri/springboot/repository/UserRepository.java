package net.petri.springboot.repository;

import net.petri.springboot.entity.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.persistence.criteria.Predicate;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    List<User> findAll();

    Optional<User> findById(Long id);

    User findByEmail(String email);

    User findByVerificationCode(String verificationCode);

    User findByResetToken(String resetToken);

    static Specification<User> search(String search) {
        return (r, q, b) -> {
            Predicate predicate = b.or(
                    b.like(r.get("firstName"), "%" + search + "%"),
                    b.like(r.get("lastName"), "%" + search + "%"),
                    b.like(r.get("email"), "%" + search + "%")
            );

            return predicate;
        };
    }
}
