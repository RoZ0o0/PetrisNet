package net.petri.springboot.repository;


import net.petri.springboot.entity.ExampleNets;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExampleNetsRepository extends JpaRepository<ExampleNets, Long> {
    List<ExampleNets> findAll();

    Optional<ExampleNets> findById(Long id);

    Optional<ExampleNets> findByNetName(String netName);

    Page<ExampleNets> findByNetNameContaining(String search, Pageable pageable);
}
