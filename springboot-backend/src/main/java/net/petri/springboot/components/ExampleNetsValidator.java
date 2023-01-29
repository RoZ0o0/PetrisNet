package net.petri.springboot.components;

import net.petri.springboot.model.FM.ExampleNetsFM;
import net.petri.springboot.repository.ExampleNetsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExampleNetsValidator extends Validator{
    private final ExampleNetsRepository exampleNetsRepository;

    @Autowired
    public ExampleNetsValidator(ExampleNetsRepository exampleNetsRepository) {
        this.exampleNetsRepository = exampleNetsRepository;
    }

    public boolean validateNetName(ExampleNetsFM exampleNetsFM) {
        if (exampleNetsFM.getNetName().isEmpty() || exampleNetsFM.getNetName().length() < 3 || exampleNetsFM.getNetName().length() > 16) {
            return false;
        }

        if (!exampleNetsRepository.findByNetName(exampleNetsFM.getNetName()).isEmpty()){
            return false;
        }

        return true;
    }
}
