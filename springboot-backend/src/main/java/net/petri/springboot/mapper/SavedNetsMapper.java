package net.petri.springboot.mapper;

import net.petri.springboot.entity.SavedNets;
import net.petri.springboot.model.FM.SavedNetsFM;
import net.petri.springboot.model.VM.SavedNetsVM;
import net.petri.springboot.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public record SavedNetsMapper(UserRepository userRepository) {
    public SavedNetsVM mapToVM(SavedNets src) {
        SavedNetsVM dest = new SavedNetsVM();
        dest.setId(src.getId());
        dest.setUserId(src.getUser().getId());
        dest.setSaveName(src.getSaveName());
        dest.setNetExport(src.getNetExport());
        dest.setPublic(src.isPublic());

        return dest;
    }

    public void mapToEntity(SavedNets dest, SavedNetsFM src) {
        if (src.getUserId() != null) {
            dest.setUser(userRepository.findById(src.getUserId()).orElse(null));
        }
        dest.setSaveName(src.getSaveName());
        dest.setNetExport(src.getNetExport());
        dest.setPublic(src.isPublic());
        dest.setRefLink(src.getRefLink());
    }

    public SavedNets mapToEntity(SavedNetsFM src) {
        SavedNets dest = new SavedNets();

        if (src.getUserId() != null) {
            dest.setUser(userRepository.findById(src.getUserId()).orElse(null));
        }
        dest.setSaveName(src.getSaveName());
        dest.setNetExport(src.getNetExport());
        dest.setPublic(src.isPublic());
        dest.setRefLink(src.getRefLink());

        return dest;
    }

    public List<SavedNetsVM> mapToList(List<SavedNets> srcList) {
        List<SavedNetsVM> destList = new ArrayList<>();
        for (SavedNets srcEntity : srcList) {
            destList.add(mapToVM(srcEntity));
        }

        return destList;
    }

    public List<SavedNetsVM> mapToListPage(Page<SavedNets> srcPage) {
        List<SavedNetsVM> destList = new ArrayList<>();
        for (SavedNets srcEntity : srcPage) {
            destList.add(mapToVM(srcEntity));
        }

        return destList;
    }
}
