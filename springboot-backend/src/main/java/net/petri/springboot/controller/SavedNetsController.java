package net.petri.springboot.controller;

import net.petri.springboot.model.FM.SavedNetsFM;
import net.petri.springboot.model.VM.SavedNetsVM;
import net.petri.springboot.service.SavedNetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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

    @GetMapping("/fetchAll")
    public List<SavedNetsVM> fetchSavedNetsPaginated(@RequestParam int page, @RequestParam int size) { return savedNetsService.getAllPaginated(page, size); }

    @GetMapping("/{id}")
    public SavedNetsVM find(@PathVariable Long id) {
        return savedNetsService.find(id);
    }

    @GetMapping("/find")
    public Long findBySaveNameAndUserId(@RequestParam String saveName, Authentication authentication) {
        return savedNetsService.findBySaveNameAndUserId(saveName, authentication);
    }

    @PostMapping()
    public SavedNetsVM create(@RequestBody SavedNetsFM newEntity, Authentication authentication) {
        return savedNetsService.create(newEntity, authentication);
    }

    @PutMapping()
    public SavedNetsVM update(@RequestBody SavedNetsFM newEntity, @RequestParam Long id, Authentication authentication) {
        return savedNetsService.update(newEntity, id, authentication);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, Authentication authentication) { savedNetsService.delete(id, authentication); }

    @GetMapping("/user")
    public List<SavedNetsVM> findByUserEmail(@RequestParam String email, Authentication authentication) { return savedNetsService.findByUserEmail(email, authentication); }

    @GetMapping("/user/fetchAll")
    public List<SavedNetsVM> findByUserEmailPaginated(@RequestParam String email, @RequestParam int page, @RequestParam int size, Authentication authentication) { return savedNetsService.findByUserEmailPaginated(email, page, size, authentication); }

    @GetMapping("/public")
    public List<SavedNetsVM> findByPublic() { return savedNetsService.findByPublic(true); }

    @GetMapping("/public/fetchAll")
    public List<SavedNetsVM> findByPublicPaginated(@RequestParam int page, @RequestParam int size) { return savedNetsService.getPublicPaginated(true, page, size); }

    @GetMapping("/public/search")
    public List<SavedNetsVM> searchPublic(@RequestParam int page, @RequestParam int size, @RequestParam String search) { return savedNetsService.search(page, size, search); }

    @GetMapping("/public/searchAll")
    public List<SavedNetsVM> searchAll(@RequestParam int page, @RequestParam int size, @RequestParam String search) { return savedNetsService.searchAll(page, size, search); }

    @PutMapping("/public")
    public SavedNetsVM setPublic(@RequestBody SavedNetsFM newEntity, @RequestParam Long id, Authentication authentication) {
        return savedNetsService.setPublic(newEntity, id, authentication);
    }

    @GetMapping("/ref")
    public String createRefLink(@RequestParam Long id, Authentication authentication) {
        return savedNetsService.createRefLink(id, authentication);
    }

    @GetMapping("/ref/{ref}")
    public SavedNetsVM getByRefLink(@PathVariable String ref) {
        return savedNetsService.findByRefLink(ref);
    }
}
