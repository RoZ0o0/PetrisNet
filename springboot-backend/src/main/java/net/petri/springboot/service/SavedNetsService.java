package net.petri.springboot.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import net.petri.springboot.components.SavedNetsValidator;
import net.petri.springboot.components.UserValidator;
import net.petri.springboot.entity.SavedNets;
import net.petri.springboot.mapper.SavedNetsMapper;
import net.petri.springboot.model.FM.SavedNetsFM;
import net.petri.springboot.model.VM.SavedNetsVM;
import net.petri.springboot.model.VM.UserVM;
import net.petri.springboot.repository.SavedNetsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public List<SavedNetsVM> getAllPaginated(int page, int size) {
        Pageable getPage = PageRequest.of(page, size);
        Page<SavedNets> entity = savedNetsRepository.findAll(getPage);

        List<SavedNetsVM> entities = savedNetsMapper.mapToListPage(entity);

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

        Optional<SavedNets> optionalSavedNets = savedNetsRepository.findById(id);
        Optional<SavedNets> optionalSaveNameAndUser = savedNetsRepository.findBySaveNameAndUserId(newEntity.getSaveName(), newEntity.getUserId());
        SavedNets entity;
        if (optionalSavedNets.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        entity = optionalSavedNets.get();

        if (optionalSaveNameAndUser.isPresent() && optionalSaveNameAndUser.get().getId() != id) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if (!Objects.equals(userService.findByEmail(authentication.getName()).getRole(), "ROLE_ADMIN")) {
            newEntity.setPublic(entity.isPublic());
            newEntity.setUserId(entity.getUser().getId());
        }

        newEntity.setRefLink(entity.getRefLink());

        savedNetsMapper.mapToEntity(entity, newEntity);
        savedNetsRepository.save(entity);

        return savedNetsMapper.mapToVM(entity);
    }

    public void delete(Long id, Authentication authentication) {
        Optional<SavedNets> savedNetsOptional = savedNetsRepository.findById(id);
        SavedNets entity;
        if (savedNetsOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        entity = savedNetsOptional.get();
        if (!savedNetsValidator.validateDelete(entity, authentication)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        savedNetsRepository.delete(entity);
    }

    public List<SavedNetsVM> findByUserEmail(String email, Authentication authentication) {
        if (!Objects.equals(email, authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        List<SavedNets> entity = savedNetsRepository.findByUserEmail(email);
        List<SavedNetsVM> entities = savedNetsMapper.mapToList(entity);

        return entities;
    }

    public List<SavedNetsVM> findByUserEmailPaginated(String email, int page, int size, Authentication authentication) {
        if (!Objects.equals(email, authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Pageable getPage = PageRequest.of(page, size);
        Page<SavedNets> entity = savedNetsRepository.findByUserEmail(email, getPage);
        List<SavedNetsVM> entities = savedNetsMapper.mapToListPage(entity);

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

    public List<SavedNetsVM> getPublicPaginated(boolean isPublic, int page, int size) {
        Pageable getPage = PageRequest.of(page, size);
        Page<SavedNets> entity = savedNetsRepository.findByIsPublic(isPublic, getPage);

        List<SavedNetsVM> entities = savedNetsMapper.mapToListPage(entity);

        return entities;
    }

    public SavedNetsVM setPublic(SavedNetsFM newEntity, Long id, Authentication authentication) {
        if (!savedNetsValidator.checkNetUser(newEntity, authentication)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        Optional<SavedNets> optionalSavedNets = savedNetsRepository.findById(id);
        SavedNets entity;
        if (optionalSavedNets.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        entity = optionalSavedNets.get();

        newEntity.setPublic(true);
        newEntity.setNetExport(entity.getNetExport());
        newEntity.setSaveName(entity.getSaveName());
        newEntity.setUserId(entity.getUser().getId());
        newEntity.setRefLink(entity.getRefLink());

        savedNetsMapper.mapToEntity(entity, newEntity);
        savedNetsRepository.save(entity);

        return savedNetsMapper.mapToVM(entity);
    }

    public String createRefLink(Long id, Authentication authentication) {
        SavedNetsFM newEntity = new SavedNetsFM();
        Optional<SavedNets> optionalSavedNets = savedNetsRepository.findById(id);
        SavedNets entity;
        if (optionalSavedNets.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        entity = optionalSavedNets.get();

        if (Objects.equals(entity.getRefLink(), null)) {
            newEntity.setPublic(entity.isPublic());
            newEntity.setNetExport(entity.getNetExport());
            newEntity.setSaveName(entity.getSaveName());
            newEntity.setUserId(entity.getUser().getId());

            if (!savedNetsValidator.checkNetUser(newEntity, authentication)) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
            }

            int leftLimit = 48;
            int rightLimit = 122;
            int targetStringLength = 10;
            Random random = new Random();

            String generatedString = random.ints(leftLimit, rightLimit + 1)
                    .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                    .limit(targetStringLength)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();

            newEntity.setRefLink(generatedString);

            savedNetsMapper.mapToEntity(entity, newEntity);
            savedNetsRepository.save(entity);
        }
        return entity.getRefLink();
    }

    public SavedNetsVM findByRefLink(String ref) {
        Optional<SavedNets> optionalSavedNets = savedNetsRepository.findByRefLink(ref);
        SavedNets entity;
        if (optionalSavedNets.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        entity = optionalSavedNets.get();

        return savedNetsMapper.mapToVM(entity);
    }
}