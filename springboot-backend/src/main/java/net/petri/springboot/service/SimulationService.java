package net.petri.springboot.service;

import net.petri.springboot.config.models.simulation.SimulationNet;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.*;

@CrossOrigin
@Service
public record SimulationService() {

    public SimulationNet simulation(SimulationNet net) {

        ArrayList<String> changes = new ArrayList<>();

        if (!Objects.equals(net.getConnections().toString(), "[]")) {
            ArrayList<String> transitions = new ArrayList<>();
            ArrayList<String> circles = new ArrayList<>();
            Map<String, ArrayList<String>> connectionsTransitionST = new HashMap<>();
            Map<String, ArrayList<String>> connectionsCircles = new HashMap<>();
            Map<String, ArrayList<String>> connectionsTransitionFT = new HashMap<>();
            for (int i = 0; i < net.getElements().size(); i++) {
                if (net.getElements().get(i).getName().charAt(1) == 'T') {
                    transitions.add(net.getElements().get(i).getName());
                }
                if (net.getElements().get(i).getName().charAt(1) == 'C') {
                    circles.add(net.getElements().get(i).getName());
                }
            }

            getConnectionMap(net, transitions, connectionsTransitionST, false);

            getConnectionMap(net, circles, connectionsCircles, true);

            getConnectionMap(net, transitions, connectionsTransitionFT, true);

            for (String transitionKey: connectionsTransitionST.keySet()) {
                if (connectionsTransitionST.get(transitionKey).size() == 0) {
                    if (connectionsTransitionFT.get(transitionKey).size() > 0) {
                        for (int i = 0; i < connectionsTransitionFT.get(transitionKey).size(); i++) {
                            int numberOfTokens = net.getTokens().get(findCircle(net, connectionsTransitionFT.get(transitionKey).get(i))).getToken_amount();
                            net.getTokens().get(findCircle(net, connectionsTransitionFT.get(transitionKey).get(i))).setToken_amount(numberOfTokens + 1);
                            System.out.println("dodawanie");
                        }
                    }
                }
                for (int j = 0; j < connectionsTransitionST.get(transitionKey).size(); j++) {
                    if (connectionsTransitionST.get(transitionKey).size() == 1) {
                        if (connectionsCircles.get(connectionsTransitionST.get(transitionKey).get(j)).size() == 1) {
                            if (findCircle(net, connectionsTransitionST.get(transitionKey).get(j)) >= 0) {
                                int numberOfTokens = net.getTokens().get(findCircle(net, connectionsTransitionST.get(transitionKey).get(j))).getToken_amount();
                                if (numberOfTokens > 0) {
                                    net.getTokens().get(findCircle(net, connectionsTransitionST.get(transitionKey).get(j))).setToken_amount(numberOfTokens - 1);
                                    System.out.println("usuwanie");
                                }
                            }
                        }
                    }
                }
            }
        }
        return net;
    }

    private void getConnectionMap(SimulationNet net, ArrayList<String> elements, Map<String, ArrayList<String>> connections, boolean side) {
        for (int i = 0; i < elements.size(); i++) {
            ArrayList<String> tempConnections = new ArrayList<>();
            for (int j = 0; j < net.getConnections().size(); j++) {
                if (side && Objects.equals(net.getConnections().get(j).getFT(), elements.get(i))) {
                    tempConnections.add(net.getConnections().get(j).getST());
                }
                if(!side && Objects.equals(net.getConnections().get(j).getST(), elements.get(i))) {
                    tempConnections.add(net.getConnections().get(j).getFT());
                }
            }

            connections.put(elements.get(i), tempConnections);
        }
    }

    private int findCircle(SimulationNet net, String name) {
        int foundIndex = -1;
        for (int i = 0; i < net.getTokens().size(); i++) {
            if (Objects.equals(net.getTokens().get(i).getCircle(), name)) {
                foundIndex = i;
            }
        }

        return foundIndex;
    }
}
