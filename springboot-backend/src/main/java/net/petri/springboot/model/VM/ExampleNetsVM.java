package net.petri.springboot.model.VM;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExampleNetsVM {
    private Long id;
    private String netName;
    private String netExport;

    public ExampleNetsVM() {
    }

    public ExampleNetsVM(Long id, String netName, String netExport) {
        this.id = id;
        this.netName = netName;
        this.netExport = netExport;
    }
}
