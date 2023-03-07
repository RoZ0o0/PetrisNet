package net.petri.springboot.controller;

import net.petri.springboot.config.models.simulation.SimulationNet;
import net.petri.springboot.service.SimulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@CrossOrigin
@RestController
@RequestMapping({"/api/simulation"})
public class SimulationController {
    private final SimulationService simulationService;


    @Autowired
    public SimulationController(SimulationService simulationService) { this.simulationService = simulationService; }

    @PostMapping()
    public SimulationNet simulation(@RequestBody SimulationNet net, @RequestParam String transitionKey) { return simulationService.simulation(net, transitionKey); }

    @PostMapping("/check")
    public SimulationNet check(@RequestBody SimulationNet net) { return simulationService.checkNetRun(net); }

    @PostMapping("/transition")
    public ArrayList<String> transitions(@RequestBody SimulationNet net) { return simulationService.getActiveTransitions(net); }
//
//    @PostMapping("/check")
//    public boolean checkNet(@RequestBody SimulationNet net) { return simulationService.checkNetRun(net); }
}
