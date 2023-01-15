package net.petri.springboot.repository;

import net.petri.springboot.entity.SavedNets;
import net.petri.springboot.entity.User;
import net.petri.springboot.model.VM.UserVM;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavedNetsRepository extends JpaRepository<SavedNets, Long> {
    List<SavedNets> findAll();

    List<SavedNets> findByUserEmail(String email);

    Optional<SavedNets> findById(Long id);

    Optional<SavedNets> findBySaveName(String saveName);
}
