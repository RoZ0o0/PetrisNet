package net.petri.springboot.components;

import net.petri.springboot.model.FM.ExampleNetsFM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExampleNetsValidator extends Validator{

    @Autowired
    public ExampleNetsValidator() {}

    public boolean validateNetName(ExampleNetsFM exampleNetsFM) {
        if (exampleNetsFM.getNetName().isEmpty() || exampleNetsFM.getNetName().length() < 3 || exampleNetsFM.getNetName().length() > 16) {
            return false;
        }

        return true;
    }
}
