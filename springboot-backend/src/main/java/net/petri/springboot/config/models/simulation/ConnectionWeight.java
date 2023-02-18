package net.petri.springboot.config.models.simulation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionWeight {

    private String name;

    private String element;

    private int weight;
}
