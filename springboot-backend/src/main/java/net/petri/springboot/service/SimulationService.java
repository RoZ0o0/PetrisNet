package net.petri.springboot.service;

import net.petri.springboot.config.models.simulation.Connections;
import net.petri.springboot.config.models.simulation.SimulationNet;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.*;

@CrossOrigin
@Service
public record SimulationService() {

    public SimulationNet simulation(SimulationNet net) {
        if (net.getElements().size() > 0) {
            Random random = new Random();
            ArrayList<String> changes = new ArrayList<>();
            net.setChanges(changes);
            ArrayList<String> transitions = new ArrayList<>();
            Map<String, ArrayList<String>> connectionsTransitionST = new HashMap<>();
            Map<String, ArrayList<String>> connectionsTransitionFT = new HashMap<>();
            for (int i = 0; i < net.getElements().size(); i++) {
                if (Objects.equals(net.getElements().get(i).getType(), "pn.Transition")) {
                    transitions.add(net.getElements().get(i).getId());
                }
            }

            getConnectionMap(net, transitions, connectionsTransitionST, connectionsTransitionFT);

            ArrayList<String> enabledTransitions = new ArrayList<>();
            for (String transitionKey : connectionsTransitionST.keySet()) {
                boolean isEnabled = checkTransition(net, connectionsTransitionST, connectionsTransitionFT, transitionKey);
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

                net.setChanges(changes);
            }
        }

        return net;
    }

//    public boolean checkNetRun(SimulationNet net) {
//
//        if (!Objects.equals(net.getConnections().toString(), "[]")) {
//            ArrayList<String> transitions = new ArrayList<>();
//            Map<String, ArrayList<String>> connectionsTransitionST = new HashMap<>();
//            Map<String, ArrayList<String>> connectionsTransitionFT = new HashMap<>();
//            for (int i = 0; i < net.getElements().size(); i++) {
//                if (net.getElements().get(i).getName().charAt(1) == 'T') {
//                    transitions.add(net.getElements().get(i).getName());
//                }
//            }
//
//            getConnectionMap(net, transitions, connectionsTransitionST, false);
//
//            getConnectionMap(net, transitions, connectionsTransitionFT, true);
//
//            for (int i = 0; i < net.getTokens().size(); i++) {
//                if (net.getTokens().get(i).getToken_amount() < 0) {
//                    return false;
//                }
//            }
//
//            ArrayList<String> enabledTransitions = new ArrayList<>();
//            for (String transitionKey : connectionsTransitionST.keySet()) {
//                boolean hasInput = false;
//                boolean hasOutput = false;
//                boolean isEnabled = checkTransition(net, connectionsTransitionST, transitionKey);
//
//                if (isEnabled) {
//                    enabledTransitions.add(transitionKey);
//                }
//                if (connectionsTransitionFT.get(transitionKey).size() > 0) {
//                    hasInput = true;
//                }
//                if (connectionsTransitionST.get(transitionKey).size() > 0) {
//                    hasOutput = true;
//                }
//
//                if (!hasInput || !hasOutput) {
//                    return false;
//                }
//            }
//
//            return !enabledTransitions.isEmpty();
//        }
//
//        return false;
//    }
//
    private void fireTransition(SimulationNet net, String transitionKey, ArrayList<String> changes,
                                Map<String, ArrayList<String>> connectionsTransitionST,
                                Map<String, ArrayList<String>> connectionsTransitionFT) {

        for (int i = 0; i < connectionsTransitionST.get(transitionKey).size(); i++) {
            int tokensNumber = 0;
            for (int j = 0; j < net.getElements().size(); j++) {
                if (Objects.equals(net.getElements().get(j).getId(), connectionsTransitionST.get(transitionKey).get(i))) {
                    tokensNumber = net.getElements().get(j).getTokens();
                }
            }

            int connectionWeight = getConnectionWeight(net, transitionKey, connectionsTransitionST.get(transitionKey).get(i));

            for (int j = 0; j < net.getElements().size(); j++) {
                if (Objects.equals(connectionsTransitionST.get(transitionKey).get(i), net.getElements().get(j).getId())) {
                    net.getElements().get(j).setTokens(tokensNumber - connectionWeight);
                }
            }
            changes.add(connectionsTransitionST.get(transitionKey).get(i) + " " + transitionKey);
        }

        addByOutputArc(net, changes, connectionsTransitionFT, transitionKey);
    }

    private boolean checkTransition(SimulationNet net, Map<String, ArrayList<String>> connectionsTransitionST, Map<String, ArrayList<String>> connectionsTransitionFT, String transitionKey) {
        if (connectionsTransitionST.get(transitionKey).size() > 0) {
            for (int i = 0; i < connectionsTransitionST.get(transitionKey).size(); i++) {
                int numberOfTokens = 0;
                for (int j = 0; j < net.getElements().size(); j++) {
                    if (Objects.equals(net.getElements().get(j).getId(), connectionsTransitionST.get(transitionKey).get(i))) {
                        numberOfTokens = net.getElements().get(j).getTokens();
                    }
                }

                int connectionWeight = getConnectionWeight(net, transitionKey, connectionsTransitionST.get(transitionKey).get(i));

                if (numberOfTokens < connectionWeight) {
                    return false;
                }
            }
        } else {
            if (connectionsTransitionFT.get(transitionKey).size() <= 0) {
                return false;
            }
        }

        return true;
    }

    private int getConnectionWeight(SimulationNet net, String transitionId, String placeId) {
        int connectionWeight = 1;

        for (Connections connection : net.getConnections()) {
            if (Objects.equals(connection.getTarget(), transitionId) && Objects.equals(connection.getSource(), placeId)) {
                connectionWeight = connection.getWeight();
            }
        }

        return connectionWeight;
    }

    private void addByOutputArc(SimulationNet net, ArrayList<String> changes, Map<String, ArrayList<String>> connectionsTransitionFT, String transitionKey) {
        for (int i = 0; i < connectionsTransitionFT.get(transitionKey).size(); i++) {
            int connectionWeight = 1;

            for (int j = 0; j < net.getConnections().size(); j++) {
                if (Objects.equals(net.getConnections().get(j).getSource(), transitionKey) && Objects.equals(net.getConnections().get(j).getTarget(), connectionsTransitionFT.get(transitionKey).get(i))){
                    connectionWeight = net.getConnections().get(j).getWeight();
                }
            }
            for (int j = 0; j < net.getElements().size(); j++) {
                if (Objects.equals(net.getElements().get(j).getId(), connectionsTransitionFT.get(transitionKey).get(i))) {
                    int numberOfTokens = net.getElements().get(j).getTokens();
                    net.getElements().get(j).setTokens(numberOfTokens + connectionWeight);
                    changes.add(transitionKey + " " + connectionsTransitionFT.get(transitionKey).get(i));
                }
            }
        }
    }

    private void getConnectionMap(SimulationNet net, ArrayList<String> transitions, Map<String, ArrayList<String>> connectionsST, Map<String, ArrayList<String>> connectionsFT) {
        for (String element : transitions) {
            ArrayList<String> tempConnectionsST = new ArrayList<>();
            ArrayList<String> tempConnectionsFT = new ArrayList<>();
            for (int i = 0; i < net.getConnections().size(); i++) {
                if (Objects.equals(element, net.getConnections().get(i).getTarget())) {
                    for (int j = 0; j< net.getElements().size(); j++) {
                        if (Objects.equals(net.getElements().get(j).getId(), net.getConnections().get(i).getSource())) {
                            tempConnectionsST.add(net.getElements().get(j).getId());
                        }
                    }
                } else if (Objects.equals(element, net.getConnections().get(i).getSource())) {
                    for (int j = 0; j< net.getElements().size(); j++) {
                        if (Objects.equals(net.getElements().get(j).getId(), net.getConnections().get(i).getTarget())) {
                            tempConnectionsFT.add(net.getElements().get(j).getId());
                        }
                    }
                }
            }

            connectionsST.put(element, tempConnectionsST);
            connectionsFT.put(element, tempConnectionsFT);
        }
    }
}
