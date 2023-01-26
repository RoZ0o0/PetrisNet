package net.petri.springboot.service;

import java.util.List;
import java.util.Optional;

import net.petri.springboot.components.SavedNetsValidator;
import net.petri.springboot.components.UserValidator;
import net.petri.springboot.entity.SavedNets;
import net.petri.springboot.mapper.SavedNetsMapper;
import net.petri.springboot.model.FM.SavedNetsFM;
import net.petri.springboot.model.VM.SavedNetsVM;
import net.petri.springboot.model.VM.UserVM;
import net.petri.springboot.repository.SavedNetsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;


import org.springframework.web.server.ResponseStatusException;

import static java.lang.Long.valueOf;

@CrossOrigin
@Service
public record SavedNetsService(SavedNetsRepository savedNetsRepository, SavedNetsMapper savedNetsMapper,
                               SavedNetsValidator savedNetsValidator, UserValidator userValidator,
                               UserService userService) {

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

    public SavedNetsVM create(SavedNetsFM newEntity, Authentication authentication) {
        if (!savedNetsValidator.validateUserSave(newEntity, authentication)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if(findBySaveNameAndUserId(newEntity.getSaveName(), authentication) != valueOf(0)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        SavedNets entity = savedNetsMapper.mapToEntity(newEntity);
        savedNetsRepository.save(entity);
        return savedNetsMapper.mapToVM(entity);
    }

    public SavedNetsVM update(SavedNetsFM newEntity, Long id, Authentication authentication) {
        if (!savedNetsValidator.validateUserSave(newEntity, authentication)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if(findBySaveNameAndUserId(newEntity.getSaveName(), authentication) == valueOf(0)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        Optional<SavedNets> optionalSavedNets = savedNetsRepository.findById(id);
        SavedNets entity;
        if (optionalSavedNets.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        entity = optionalSavedNets.get();

        newEntity.setSaveName(entity.getSaveName());
        newEntity.setPublic(entity.isPublic());
        newEntity.setUserId(entity.getUser().getId());

        savedNetsMapper.mapToEntity(entity, newEntity);
        savedNetsRepository.save(entity);

        return savedNetsMapper.mapToVM(entity);
    }

    public List<SavedNetsVM> findByUserEmail(String email) {
        List<SavedNets> entity = savedNetsRepository.findByUserEmail(email);
        List<SavedNetsVM> entities = savedNetsMapper.mapToList(entity);

        return entities;
    }

    public Long findBySaveNameAndUserId(String saveName, Authentication authentication) {
        UserVM user = userService.findByEmail(authentication.getName());
        Optional<SavedNets> optionalSavedNets = savedNetsRepository.findBySaveNameAndUserId(saveName, user.getId());
        SavedNets entity;
        if (optionalSavedNets.isEmpty()) {
            return valueOf(0);
        }

        entity = optionalSavedNets.get();

        return entity.getId();
    }

    public List<SavedNetsVM> findByPublic (boolean isPublic) {
        List<SavedNets> entity = savedNetsRepository.findByIsPublic(isPublic);
        List<SavedNetsVM> entities = savedNetsMapper.mapToList(entity);

        return entities;
    }
}