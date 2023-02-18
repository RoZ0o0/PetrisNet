package net.petri.springboot.config.models.simulation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SimulationNet {

    private List<Elements> elements;

    private List<Connections> connections;

    private List<Tokens> tokens;

    private List<ConnectionWeight> connectionWeights;

    private List<String> changes;

}
