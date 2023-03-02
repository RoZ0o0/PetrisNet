package net.petri.springboot.controller;

import net.petri.springboot.model.FM.ExampleNetsFM;
import net.petri.springboot.model.VM.ExampleNetsVM;
import net.petri.springboot.service.ExampleNetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping({"/api/example_nets"})
public class ExampleNetsController {
    private final ExampleNetsService exampleNetsService;

    @Autowired
    public ExampleNetsController(ExampleNetsService exampleNetsService) {
        this.exampleNetsService = exampleNetsService;
    }

    @GetMapping()
    public List<ExampleNetsVM> fetchExampleNets() {
        return exampleNetsService.getAll();
    }

    @GetMapping("/fetchAll")
    public List<ExampleNetsVM> fetchExampleNetsPaginated(@RequestParam int page, @RequestParam int size) { return exampleNetsService.getAllPaginated(page, size); }

    @GetMapping("/{id}")
    public ExampleNetsVM find(@PathVariable Long id) {
        return exampleNetsService.find(id);
    }

    @PostMapping()
    public ExampleNetsVM create(@RequestBody ExampleNetsFM newEntity) {
        return exampleNetsService.create(newEntity);
    }

    @PutMapping()
    public ExampleNetsVM update(@RequestBody ExampleNetsFM newEntity, @RequestParam Long id) {
        return exampleNetsService.update(newEntity, id);
    }

    @GetMapping("/find")
    public boolean find(@RequestParam String netName) {
        return exampleNetsService.findByNetName(netName);
    }

    @GetMapping("/search")
    public List<ExampleNetsVM> search(@RequestParam int page, @RequestParam int size, @RequestParam String search) { return exampleNetsService.search(page, size, search); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { exampleNetsService.delete(id); }
}