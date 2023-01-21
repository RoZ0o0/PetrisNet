package net.petri.springboot.controller;

import net.petri.springboot.entity.User;
import net.petri.springboot.model.FM.SavedNetsFM;
import net.petri.springboot.model.VM.SavedNetsVM;
import net.petri.springboot.model.VM.UserVM;
import net.petri.springboot.service.SavedNetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping({"/api/saved_nets"})
public class SavedNetsController {
    private final SavedNetsService savedNetsService;

    @Autowired
    public SavedNetsController(SavedNetsService savedNetsService) {
        this.savedNetsService = savedNetsService;
    }

    @GetMapping()
    public List<SavedNetsVM> fetchSavedNets() {
        return savedNetsService.getAll();
    }

    @GetMapping("/{id}")
    public SavedNetsVM find(@PathVariable Long id) {
        return savedNetsService.find(id);
    }

    @PostMapping()
    public SavedNetsVM create(@RequestBody SavedNetsFM newEntity) {
        return savedNetsService.create(newEntity);
    }

    @GetMapping("/user")
    public List<SavedNetsVM> findByUserEmail(@RequestParam String email) { return savedNetsService.findByUserEmail(email); }

    @GetMapping("/public")
    public List<SavedNetsVM> findByPublic() { return savedNetsService.findByPublic(true); }
}
