package net.petri.springboot.controller;

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

    @GetMapping("/{id}")
    public ExampleNetsVM find(@PathVariable Long id) {
        return exampleNetsService.find(id);
    }
}