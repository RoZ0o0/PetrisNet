package net.petri.springboot.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import net.petri.springboot.components.ExampleNetsValidator;
import net.petri.springboot.entity.ExampleNets;
import net.petri.springboot.entity.SavedNets;
import net.petri.springboot.mapper.ExampleNetsMapper;
import net.petri.springboot.model.FM.ExampleNetsFM;
import net.petri.springboot.model.VM.ExampleNetsVM;
import net.petri.springboot.model.VM.SavedNetsVM;
import net.petri.springboot.repository.ExampleNetsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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

    public List<ExampleNetsVM> getAllPaginated(int page, int size) {
        Pageable getPage = PageRequest.of(page, size);
        Page<ExampleNets> entity = exampleNetsRepository.findAll(getPage);

        List<ExampleNetsVM> entities = exampleNetsMapper.mapToListPage(entity);

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
        Optional<ExampleNets> findByName = exampleNetsRepository.findByNetName(newEntity.getNetName());
        ExampleNets entity;
        if (optionalExampleNets.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        entity = optionalExampleNets.get();

        if (findByName.isPresent() && findByName.get().getId() != id) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

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

    public List<ExampleNetsVM> search(int page, int size, String search) {
        Pageable getPage = PageRequest.of(page, size);
        Page<ExampleNets> entity;

        if (Objects.equals(search, "")) {
            entity = exampleNetsRepository.findAll(getPage);
        } else {
            entity = exampleNetsRepository.findByNetNameContaining(search, getPage);
        }

        List<ExampleNetsVM> entities = exampleNetsMapper.mapToListPage(entity);

        return entities;
    }

    public void delete(Long id) {
        Optional<ExampleNets> exampleNets = exampleNetsRepository.findById(id);
        ExampleNets entity;
        if (exampleNets.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        entity = exampleNets.get();
        exampleNetsRepository.delete(entity);
    }
}