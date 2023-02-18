package net.petri.springboot.controller;

import net.petri.springboot.config.models.simulation.SimulationNet;
import net.petri.springboot.service.SimulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping({"/api/simulation"})
public class SimulationController {
    private final SimulationService simulationService;


    @Autowired
    public SimulationController(SimulationService simulationService) { this.simulationService = simulationService; }

    @PostMapping()
    public SimulationNet simulation(@RequestBody SimulationNet net) { return simulationService.simulation(net); }

    @PostMapping("/check")
    public boolean checkNet(@RequestBody SimulationNet net) { return simulationService.checkNetRun(net); }
}
