package net.petri.springboot.repository;

import net.petri.springboot.entity.SavedNets;
import net.petri.springboot.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.List;
import java.util.Optional;

public interface SavedNetsRepository extends JpaRepository<SavedNets, Long>, JpaSpecificationExecutor<SavedNets> {
    List<SavedNets> findAll();

    List<SavedNets> findByUserEmail(String email);

    Page<SavedNets> findByUserEmail(String email, Pageable pageable);

    Optional<SavedNets> findBySaveNameAndUserId(String saveName, Long userId);

    Optional<SavedNets> findByRefLink(String refLink);

    List<SavedNets> findByIsPublic(boolean isPublic);

    Page<SavedNets> findByIsPublic(boolean isPublic, Pageable pageable);

    static Specification<SavedNets> search(String search) {
        return (r, q, b) -> {
            Join<SavedNets, User> joinUser = r.join("user");
            Predicate predicate = b.or(
                    b.like(joinUser.get("firstName"), "%" + search + "%"),
                    b.like(joinUser.get("lastName"), "%" + search + "%"),
                    b.like(joinUser.get("email"), "%" + search + "%"),
                    b.like(r.get("saveName"), "%" + search + "%")
            );

            return predicate;
        };
    }

    Page<SavedNets> findByIsPublicAndSaveNameContaining(boolean isPublic, String saveName, Pageable pageable);

    Optional<SavedNets> findById(Long id);
}
