package net.petri.springboot.components;

import net.petri.springboot.model.FM.SavedNetsFM;
import net.petri.springboot.repository.SavedNetsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SavedNetsValidator extends Validator {
    private final SavedNetsRepository savedNetsRepository;

    @Autowired
    public SavedNetsValidator(SavedNetsRepository savedNetsRepository) {
        this.savedNetsRepository = savedNetsRepository;
    }

    public boolean validateSaveName(SavedNetsFM model) {
        if (savedNetsRepository.findBySaveName(model.getSaveName()).isEmpty()) {
            return true;
        }
        return false;
    }
}
