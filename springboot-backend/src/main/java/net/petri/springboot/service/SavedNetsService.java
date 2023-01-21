package net.petri.springboot.service;

import java.util.List;
import java.util.Optional;

import net.petri.springboot.components.SavedNetsValidator;
import net.petri.springboot.components.UserValidator;
import net.petri.springboot.entity.SavedNets;
import net.petri.springboot.entity.User;
import net.petri.springboot.mapper.SavedNetsMapper;
import net.petri.springboot.model.FM.SavedNetsFM;
import net.petri.springboot.model.VM.SavedNetsVM;
import net.petri.springboot.model.VM.UserVM;
import net.petri.springboot.repository.SavedNetsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;


import org.springframework.web.server.ResponseStatusException;

@CrossOrigin
@Service
public record SavedNetsService(SavedNetsRepository savedNetsRepository, SavedNetsMapper savedNetsMapper,
                               SavedNetsValidator savedNetsValidator, UserValidator userValidator) {

    public List<SavedNetsVM> getAll() {
        List<SavedNets> entity = savedNetsRepository.findAll();

        List<SavedNetsVM> entities = savedNetsMapper.mapToList(entity);

        return entities;
    }

    public SavedNetsVM find(long id) {
        Optional<SavedNets> optionalSavedNets = savedNetsRepository.findById(id);
        SavedNets entity;
        if (optionalSavedNets.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        entity = optionalSavedNets.get();

        return savedNetsMapper.mapToVM(entity);
    }

    public SavedNetsVM create(SavedNetsFM newEntity) {
        if (!savedNetsValidator.findBySaveNameAndUserId(newEntity.getSaveName(), newEntity.getUserId())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        SavedNets entity = savedNetsMapper.mapToEntity(newEntity);
        savedNetsRepository.save(entity);
        return savedNetsMapper.mapToVM(entity);
    }

    public List<SavedNetsVM> findByUserEmail(String email) {
        List<SavedNets> entity = savedNetsRepository.findByUserEmail(email);
        List<SavedNetsVM> entities = savedNetsMapper.mapToList(entity);

        return entities;
    }

    public List<SavedNetsVM> findByPublic (boolean isPublic) {
        List<SavedNets> entity = savedNetsRepository.findByIsPublic(isPublic);
        List<SavedNetsVM> entities = savedNetsMapper.mapToList(entity);

        return entities;
    }
}