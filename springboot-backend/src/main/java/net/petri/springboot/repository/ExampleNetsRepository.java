package net.petri.springboot.repository;


import net.petri.springboot.entity.ExampleNets;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExampleNetsRepository extends JpaRepository<ExampleNets, Long> {
    List<ExampleNets> findAll();

    Optional<ExampleNets> findById(Long id);
}