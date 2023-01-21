package net.petri.springboot.components;

import net.petri.springboot.entity.SavedNets;
import net.petri.springboot.model.FM.SavedNetsFM;
import net.petri.springboot.model.VM.SavedNetsVM;
import net.petri.springboot.repository.SavedNetsRepository;
import net.petri.springboot.service.SavedNetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SavedNetsValidator extends Validator {
    private final SavedNetsRepository savedNetsRepository;

    @Autowired
    public SavedNetsValidator(SavedNetsRepository savedNetsRepository) {
        this.savedNetsRepository = savedNetsRepository;
    }


    public boolean findBySaveNameAndUserId(String saveName, Long userId) {
        Optional<SavedNets> entity = savedNetsRepository.findBySaveNameAndUserId(saveName, userId);
        if (!entity.isEmpty()) {
            return false;
        }
        return true;
    }
}
