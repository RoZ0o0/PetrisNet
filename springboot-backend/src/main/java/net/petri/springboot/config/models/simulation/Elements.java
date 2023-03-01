package net.petri.springboot.config.models.simulation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Elements {

    private String name;

    private String type;

    private int x;

    private int y;

    private int tokens;

    private String id;

    private int r;

    private int width;

    private int height;
}
