package net.petri.springboot.repository;

import net.petri.springboot.entity.SavedNets;
import net.petri.springboot.model.VM.UserVM;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavedNetsRepository extends JpaRepository<SavedNets, Long> {
    List<SavedNets> findAll();

    List<SavedNets> findByUserEmail(String email);

    Optional<SavedNets> findBySaveNameAndUserId(String saveName, Long userId);

    List<SavedNets> findByIsPublic(boolean isPublic);

    Optional<SavedNets> findById(Long id);
}
