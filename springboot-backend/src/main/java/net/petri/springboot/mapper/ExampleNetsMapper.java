package net.petri.springboot.mapper;

import net.petri.springboot.entity.ExampleNets;
import net.petri.springboot.model.FM.ExampleNetsFM;
import net.petri.springboot.model.VM.ExampleNetsVM;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public record ExampleNetsMapper() {
    public ExampleNetsVM mapToVM(ExampleNets src) {
        ExampleNetsVM dest = new ExampleNetsVM();
        dest.setId(src.getId());
        dest.setNetName(src.getNetName());
        dest.setNetExport(src.getNetExport());

        return dest;
    }

    public void mapToEntity(ExampleNets dest, ExampleNetsFM src) {
        dest.setNetName(src.getNetName());
        dest.setNetExport(src.getNetExport());
    }

    public ExampleNets mapToEntity(ExampleNetsFM src) {
        ExampleNets dest = new ExampleNets();

        dest.setNetName(src.getNetName());
        dest.setNetExport(src.getNetExport());

        return dest;
    }

    public List<ExampleNetsVM> mapToList(List<ExampleNets> srcList) {
        List<ExampleNetsVM> destList = new ArrayList<>();
        for (ExampleNets srcEntity : srcList) {
            destList.add(mapToVM(srcEntity));
        }

        return destList;
    }

    public List<ExampleNetsVM> mapToListPage(Page<ExampleNets> srcPage) {
        List<ExampleNetsVM> destList = new ArrayList<>();
        for (ExampleNets srcEntity : srcPage) {
            destList.add(mapToVM(srcEntity));
        }

        return destList;
    }
}
