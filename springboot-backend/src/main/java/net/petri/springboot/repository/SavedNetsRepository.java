package net.petri.springboot.repository;

import net.petri.springboot.entity.SavedNets;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavedNetsRepository extends JpaRepository<SavedNets, Long> {
    List<SavedNets> findAll();

    List<SavedNets> findByUserEmail(String email);

    Page<SavedNets> findByUserEmail(String email, Pageable pageable);

    Optional<SavedNets> findBySaveNameAndUserId(String saveName, Long userId);

    List<SavedNets> findByIsPublic(boolean isPublic);

    Page<SavedNets> findByIsPublic(boolean isPublic, Pageable pageable);

    Optional<SavedNets> findById(Long id);
}
