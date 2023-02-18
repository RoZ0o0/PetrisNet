package net.petri.springboot.service;

import net.petri.springboot.config.models.simulation.Elements;
import net.petri.springboot.config.models.simulation.SimulationNet;
import net.petri.springboot.config.models.simulation.Tokens;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.*;

@CrossOrigin
@Service
public record SimulationService() {

    public SimulationNet simulation(SimulationNet net) {
        if (!Objects.equals(net.getConnections().toString(), "[]")) {
            Random random = new Random();
            ArrayList<String> changes = new ArrayList<>();
            net.setChanges(changes);
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


            ArrayList<String> enabledTransitions = new ArrayList<>();
            for (String transitionKey: connectionsTransitionST.keySet()) {
                boolean isEnabled = checkTransition(net, connectionsTransitionST, transitionKey);
                if (isEnabled) {
                    enabledTransitions.add(transitionKey);
                }
            }

            if (!enabledTransitions.isEmpty()) {

                String chosenTransition = "";

                int indexTransition = random.nextInt(enabledTransitions.size());

                int counter = 0;

                for (String key : enabledTransitions) {
                    if (counter == indexTransition) {
                        chosenTransition = key;
                    }

                    counter++;
                }

                fireTransition(net, chosenTransition, changes, connectionsTransitionST, connectionsTransitionFT);

                ArrayList<Tokens> newTokens = new ArrayList<>();
                for (int i = 0; i < net.getTokens().size(); i++) {
                    if (net.getTokens().get(i).getToken_amount() != 0) {
                        newTokens.add(net.getTokens().get(i));
                    }
                }

                net.setTokens(newTokens);

                net.setChanges(changes);
            }
        }

        return net;
    }

    public boolean checkNetRun(SimulationNet net) {

        if (!Objects.equals(net.getConnections().toString(), "[]")) {
            ArrayList<String> transitions = new ArrayList<>();
            Map<String, ArrayList<String>> connectionsTransitionST = new HashMap<>();
            for (int i = 0; i < net.getElements().size(); i++) {
                if (net.getElements().get(i).getName().charAt(1) == 'T') {
                    transitions.add(net.getElements().get(i).getName());
                }
            }

            getConnectionMap(net, transitions, connectionsTransitionST, false);

            ArrayList<String> enabledTransitions = new ArrayList<>();
            for (String transitionKey : connectionsTransitionST.keySet()) {
                boolean isEnabled = checkTransition(net, connectionsTransitionST, transitionKey);
                if (isEnabled) {
                    enabledTransitions.add(transitionKey);
                }
            }

            return !enabledTransitions.isEmpty();
        }

        return false;
    }

    private void fireTransition(SimulationNet net, String transitionKey, ArrayList<String> changes,
                                Map<String, ArrayList<String>> connectionsTransitionST,
                                Map<String, ArrayList<String>> connectionsTransitionFT) {

        boolean isEnabled = checkTransition(net, connectionsTransitionST, transitionKey);

        if (isEnabled) {
            for (int i = 0; i < connectionsTransitionST.get(transitionKey).size(); i++) {
                int numberOfTokens = net.getTokens().get(findCircle(net, connectionsTransitionST.get(transitionKey).get(i))).getToken_amount();

                int connectionWeight = getConnectionWeight(net, transitionKey, connectionsTransitionST, i);

                net.getTokens().get(findCircle(net, connectionsTransitionST.get(transitionKey).get(i))).setToken_amount(numberOfTokens - connectionWeight);

                if (net.getTokens().get(findCircle(net, connectionsTransitionST.get(transitionKey).get(i))).getToken_amount() == 0) {
                    deleteRedundantElements(net, connectionsTransitionST, transitionKey, i);
                }
                changes.add(connectionsTransitionST.get(transitionKey).get(i) + " " + transitionKey);
            }

            addByOutputArc(net, changes, connectionsTransitionFT, transitionKey);
        }

    }

    private boolean checkTransition(SimulationNet net, Map<String, ArrayList<String>> connectionsTransitionST, String transitionKey) {
        for (int i = 0; i < connectionsTransitionST.get(transitionKey).size(); i++) {
            int numberOfTokens = 0;
            if (findCircle(net, connectionsTransitionST.get(transitionKey).get(i)) >= 0) {
                numberOfTokens = net.getTokens().get(findCircle(net, connectionsTransitionST.get(transitionKey).get(i))).getToken_amount();
            }

            int connectionWeight = getConnectionWeight(net, transitionKey, connectionsTransitionST, i);

            if (numberOfTokens < connectionWeight) {
                return false;
            }
        }
        return true;
    }

    private int getConnectionWeight(SimulationNet net, String transitionKey, Map<String, ArrayList<String>> connectionsTransitionST, int i) {
        int connectionWeight = 1;

        for (int j = 0; j < net.getConnections().size(); j++) {
            if (Objects.equals(net.getConnections().get(j).getFT(), connectionsTransitionST.get(transitionKey).get(i))
                    && Objects.equals(net.getConnections().get(j).getST(), transitionKey)) {
                for (int z = 0; z < net.getConnectionWeights().size(); z++) {
                    if (Objects.equals(net.getConnectionWeights().get(z).getName(), net.getConnections().get(j).getName())) {
                        connectionWeight = net.getConnectionWeights().get(z).getWeight();
                    }
                }
            }
        }

        return connectionWeight;
    }

    private void deleteRedundantElements(SimulationNet net, Map<String, ArrayList<String>> connectionsTransitionST, String transitionKey, int i) {
        ArrayList<Elements> newElements = new ArrayList<>();
        for (int j = 0; j < net.getElements().size(); j++) {
            if (!Objects.equals(net.getElements().get(j).getName(), net.getTokens().get(findCircle(net, connectionsTransitionST.get(transitionKey).get(i))).getName()) &&
                    !Objects.equals(net.getElements().get(j).getName(), net.getTokens().get(findCircle(net, connectionsTransitionST.get(transitionKey).get(i))).getLabel_name())) {
                newElements.add(net.getElements().get(j));
            }
        }
        net.setElements(newElements);
    }

    private void addByOutputArc(SimulationNet net, ArrayList<String> changes, Map<String, ArrayList<String>> connectionsTransitionFT, String transitionKey) {
        int counter = 0;
        for (int i = 0; i < connectionsTransitionFT.get(transitionKey).size(); i++) {
            if (findCircle(net, connectionsTransitionFT.get(transitionKey).get(i)) == -1) {
                List<Tokens> listOfTokens;
                listOfTokens = net.getTokens();

                int lastElementNumber = Integer.parseInt(net.getElements().get(net.getElements().size() - 1).getName().split("\\D+")[1]);

                Tokens newToken = new Tokens("EE" + (lastElementNumber + 1 + counter), "EE" + (lastElementNumber + 1 + counter),
                        "EL" + (lastElementNumber + 2 + counter), connectionsTransitionFT.get(transitionKey).get(i), 0);

                listOfTokens.add(newToken);

                net.setTokens(listOfTokens);
                changes.add("Added:" + connectionsTransitionFT.get(transitionKey).get(i));
                counter++;
            }

            int connectionWeight = 1;

            for (int j = 0; j < net.getConnections().size(); j++) {
                if (Objects.equals(net.getConnections().get(j).getST(), connectionsTransitionFT.get(transitionKey).get(i))
                        && Objects.equals(net.getConnections().get(j).getFT(), transitionKey)) {
                    for (int z = 0; z < net.getConnectionWeights().size(); z++) {
                        if (Objects.equals(net.getConnectionWeights().get(z).getName(), net.getConnections().get(j).getName())) {
                            connectionWeight = net.getConnectionWeights().get(z).getWeight();
                        }
                    }
                }
            }
            int numberOfTokens = net.getTokens().get(findCircle(net, connectionsTransitionFT.get(transitionKey).get(i))).getToken_amount();
            net.getTokens().get(findCircle(net, connectionsTransitionFT.get(transitionKey).get(i))).setToken_amount(numberOfTokens + connectionWeight);
            changes.add(transitionKey + " " + connectionsTransitionFT.get(transitionKey).get(i));
        }
    }

    private void getConnectionMap(SimulationNet net, ArrayList<String> elements, Map<String, ArrayList<String>> connections, boolean side) {
        for (String element : elements) {
            ArrayList<String> tempConnections = new ArrayList<>();
            for (int j = 0; j < net.getConnections().size(); j++) {
                if (side && Objects.equals(net.getConnections().get(j).getFT(), element)) {
                    tempConnections.add(net.getConnections().get(j).getST());
                }
                if (!side && Objects.equals(net.getConnections().get(j).getST(), element)) {
                    tempConnections.add(net.getConnections().get(j).getFT());
                }
            }

            connections.put(element, tempConnections);
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
