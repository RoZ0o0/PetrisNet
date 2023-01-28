package net.petri.springboot.service;

import java.util.List;
import java.util.Optional;

import net.petri.springboot.components.ExampleNetsValidator;
import net.petri.springboot.entity.ExampleNets;
import net.petri.springboot.entity.SavedNets;
import net.petri.springboot.mapper.ExampleNetsMapper;
import net.petri.springboot.model.FM.ExampleNetsFM;
import net.petri.springboot.model.VM.ExampleNetsVM;
import net.petri.springboot.model.VM.UserVM;
import net.petri.springboot.repository.ExampleNetsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;


import org.springframework.web.server.ResponseStatusException;

import static java.lang.Long.valueOf;

@CrossOrigin
@Service
public record ExampleNetsService(ExampleNetsRepository exampleNetsRepository, ExampleNetsMapper exampleNetsMapper,
                                 ExampleNetsValidator exampleNetsValidator) {

    public List<ExampleNetsVM> getAll() {
        List<ExampleNets> entity = exampleNetsRepository.findAll();

        List<ExampleNetsVM> entities = exampleNetsMapper.mapToList(entity);

        return entities;
    }

    public ExampleNetsVM find(long id) {
        Optional<ExampleNets> optionalExampleNets = exampleNetsRepository.findById(id);
        ExampleNets entity;
        if (optionalExampleNets.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        entity = optionalExampleNets.get();

        return exampleNetsMapper.mapToVM(entity);
    }

    public ExampleNetsVM create(ExampleNetsFM newEntity) {
        if (!exampleNetsValidator.validateNetName(newEntity)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        ExampleNets entity = exampleNetsMapper.mapToEntity(newEntity);
        exampleNetsRepository.save(entity);
        return exampleNetsMapper.mapToVM(entity);
    }

    public ExampleNetsVM update(ExampleNetsFM newEntity, Long id) {
        if (!exampleNetsValidator.validateNetName(newEntity)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        Optional<ExampleNets> optionalExampleNets = exampleNetsRepository.findById(id);
        ExampleNets entity;
        if (optionalExampleNets.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        entity = optionalExampleNets.get();

        exampleNetsMapper.mapToEntity(entity, newEntity);
        exampleNetsRepository.save(entity);

        return exampleNetsMapper.mapToVM(entity);
    }

    public boolean findByNetName(String netName) {
        Optional<ExampleNets> optionalExampleNets = exampleNetsRepository.findByNetName(netName);
        if (optionalExampleNets.isEmpty()) {
            return false;
        }

        return true;
    }
}