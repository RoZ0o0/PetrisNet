package net.petri.springboot.config.models.simulation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tokens {

    private String name;

    private String object_name;

    private String label_name;

    private String circle;

    private int token_amount;
}
