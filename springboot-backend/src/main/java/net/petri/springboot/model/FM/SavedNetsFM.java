package net.petri.springboot.model.FM;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SavedNetsFM {
    private Long userId;
    private String saveName;
    private String netExport;
}
