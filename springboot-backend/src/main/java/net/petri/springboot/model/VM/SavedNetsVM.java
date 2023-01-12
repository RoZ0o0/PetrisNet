package net.petri.springboot.model.VM;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SavedNetsVM {
    private Long id;
    private Long idUser;
    private String saveName;
    private String netExport;

    public SavedNetsVM() {
    }

    public SavedNetsVM(Long id, Long idUser, String saveName, String netExport) {
        this.id = id;
        this.idUser = idUser;
        this.saveName = saveName;
        this.netExport = netExport;
    }
}
