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
public class Connections {

    private String source;

    private String target;

    private int weight;

    private List<Vertices> vertices;
}
