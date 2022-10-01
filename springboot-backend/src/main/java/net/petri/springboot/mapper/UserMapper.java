package net.petri.springboot.mapper;

import net.petri.springboot.entity.User;
import net.petri.springboot.model.FM.UserFM;
import net.petri.springboot.model.VM.UserVM;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public record UserMapper() {
    public UserVM mapToVM(User src) {
        UserVM dest = new UserVM();
        dest.setId(src.getId());
        dest.setFirstName(src.getFirstName());
        dest.setLastName(src.getLastName());
        dest.setEmail(src.getEmail());
        dest.setRole(src.getRole());

        return dest;
    }

    void mapToEntity(User dest, UserFM src) {
        dest.setFirstName(src.getFirstName());
        dest.setLastName(src.getLastName());
        dest.setEmail(src.getEmail());
        dest.setPassword(src.getPassword());
        dest.setRole(src.getRole());
    }

    User mapToEntity(UserFM src) {
        User dest = new User();
        dest.setFirstName(src.getFirstName());
        dest.setLastName(src.getLastName());
        dest.setEmail(src.getEmail());
        dest.setPassword(src.getPassword());
        dest.setRole(src.getRole());

        return dest;
    }

    public List<UserVM> mapToList(List<User> srcList) {
        List<UserVM> destList = new ArrayList<>();
        for (User srcEntity : srcList) {
            destList.add(mapToVM(srcEntity));
        }

        return destList;
    }
}
