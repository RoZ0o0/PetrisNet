package net.petri.springboot.service;

import net.petri.springboot.config.models.simulation.Connections;
import net.petri.springboot.config.models.simulation.SimulationNet;
import net.petri.springboot.config.models.simulation.Tokens;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@CrossOrigin
@Service
public record SimulationService() {

    public SimulationNet simulation(SimulationNet net) {

        ArrayList<String> changes = new ArrayList<>();

        changes.add("test");

        if (!Objects.equals(net.getTokens().toString(), "[]")) {
            System.out.println("test");
            Tokens token = net.getTokens().get(0);

            if (token.getToken_amount() != 0) {
                token.setToken_amount(token.getToken_amount() - 1);
            }

            List<Tokens> tokens = net.getTokens();


            tokens.set(0, token);

            net.setTokens(tokens);
        }

        net.setChanges(changes);

        return net;
    }
}
