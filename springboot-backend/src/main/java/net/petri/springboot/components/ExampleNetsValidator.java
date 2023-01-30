package net.petri.springboot.components;

import net.petri.springboot.entity.ExampleNets;
import net.petri.springboot.model.FM.ExampleNetsFM;
import net.petri.springboot.repository.ExampleNetsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ExampleNetsValidator extends Validator{

    @Autowired
    public ExampleNetsValidator() {
    }

    public boolean validateNetName(ExampleNetsFM exampleNetsFM) {
        if (exampleNetsFM.getNetName().isEmpty() || exampleNetsFM.getNetName().length() < 3 || exampleNetsFM.getNetName().length() > 16) {
            return false;
        }

        return true;
    }
}
