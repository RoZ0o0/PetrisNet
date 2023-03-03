package net.petri.springboot.service;

import net.petri.springboot.config.models.simulation.Connections;
import net.petri.springboot.config.models.simulation.Elements;
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

    public SimulationNet checkNetRun(SimulationNet net) {
        SimulationNet blockedNet = net;
        SimulationNet isReachableNet = net;
        String lastPlace = "";
        for (int j = net.getElements().size() - 1; j >= 0; j--) {
            if (Objects.equals(net.getElements().get(j).getType(), "pn.Place")) {
                lastPlace = net.getElements().get(j).getId();
                break;
            }
        }

        SimulationNet stateNet = net;
        ArrayList<ArrayList<Integer>> states = generateStates(stateNet);

        ArrayList<String> reason = new ArrayList<>();

        boolean placeExist = false;
        boolean transitionExist = false;

        for (int i = 0; i < net.getElements().size(); i++) {
            if (Objects.equals(net.getElements().get(i).getType(), "pn.Place")) {
                placeExist = true;
            }
            if (Objects.equals(net.getElements().get(i).getType(), "pn.Transition")) {
                transitionExist = true;
            }
        }

        if (!placeExist || !transitionExist || net.getConnections().size() <= 0) {
            reason.add("Model nie jest poprawny!");
            net.setChanges(reason);
            return net;
        }

        boolean lastelem = false;

        for (int i = 0; i < 1000; i++) {
            if (Objects.equals(net.getElements().get(i).getType(), "pn.Place")) {
                if (net.getElements().get(i).getTokens() < 0) {
                    reason.add("Model posiada stan ujemny!");
                    net.setChanges(reason);
                    return net;
                }
                if (!isReachablePlace(isReachableNet, net.getElements().get(i).getId(), i, states)) {
                    reason.add("Model posiada stan nieosiągalny");
                    net.setChanges(reason);
                    return net;
                }
                if (Objects.equals(net.getElements().get(i).getId(), lastPlace)) {
                    lastelem = true;
                    break;
                }
            }
        }
        if (isBlocked(blockedNet, states)) {
            reason.add("Model posiada stan zblokowany!");
            net.setChanges(reason);
            return net;
        }
        if (lastelem) {
            net.setChanges(reason);
            return net;
        }
        reason.add("Nie osiągnięto stanu końcowego!");
        net.setChanges(reason);
        return net;
    }

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
            changes.add(connectionsTransitionST.get(transitionKey).get(i) + " " + transitionKey + " " + connectionWeight);
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
                    changes.add(transitionKey + " " + connectionsTransitionFT.get(transitionKey).get(i) + " " + connectionWeight);
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

    private boolean isBlocked(SimulationNet net, ArrayList<ArrayList<Integer>> states) {
        ArrayList<String> transitions = new ArrayList<>();
        Map<String, ArrayList<String>> connectionsTransitionST = new HashMap<>();
        Map<String, ArrayList<String>> connectionsTransitionFT = new HashMap<>();
        for (int i = 0; i < net.getElements().size(); i++) {
            if (Objects.equals(net.getElements().get(i).getType(), "pn.Transition")) {
                transitions.add(net.getElements().get(i).getId());
            }
        }

        getConnectionMap(net, transitions, connectionsTransitionST, connectionsTransitionFT);

        for (int i = 0; i < states.size(); i++) {
            int counter = 0;
            for (int j = 0; j < net.getElements().size(); j++) {
                if (Objects.equals(net.getElements().get(j).getType(), "pn.Place")) {
                    net.getElements().get(j).setTokens(states.get(i).get(counter));
                    counter++;
                }
            }
            ArrayList<String> enabledTransitions = new ArrayList<>();
            for (String transitionKey : connectionsTransitionST.keySet()) {
                boolean isEnabled = checkTransition(net, connectionsTransitionST, connectionsTransitionFT, transitionKey);
                if (isEnabled) {
                    enabledTransitions.add(transitionKey);
                }
            }
            if (enabledTransitions.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private boolean isReachablePlace(SimulationNet net, String placeId, int index, ArrayList<ArrayList<Integer>> states) {
        int placeCounter = 0;
        for (int i = 0; i < net.getElements().size(); i++) {
            if (Objects.equals(net.getElements().get(i).getType(), "pn.Place") && !Objects.equals(net.getElements().get(i).getId(), placeId)) {
                placeCounter++;
            }
        }

        for (int i = 0; i < states.size(); i++) {
            if (states.get(i).get(placeCounter) > 0) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<Integer> generateState(SimulationNet net) {
        ArrayList<Integer> state = new ArrayList<>();
        ArrayList<String> transitions = new ArrayList<>();
        Map<String, ArrayList<String>> connectionsTransitionST = new HashMap<>();
        Map<String, ArrayList<String>> connectionsTransitionFT = new HashMap<>();
        for (int i = 0; i < net.getElements().size(); i++) {
            if (Objects.equals(net.getElements().get(i).getType(), "pn.Transition")) {
                transitions.add(net.getElements().get(i).getId());
            }
        }

        getConnectionMap(net, transitions, connectionsTransitionST, connectionsTransitionFT);

        for (int i = 0; i < net.getElements().size(); i++) {
            if (Objects.equals(net.getElements().get(i).getType(), "pn.Place")) {
                state.add(net.getElements().get(i).getTokens());
            }
        }

        return state;
    }

    private ArrayList<ArrayList<Integer>> generateStates(SimulationNet net) {
        ArrayList<ArrayList<Integer>> states = new ArrayList<>();
        ArrayList<String> transitions = new ArrayList<>();
        Map<String, ArrayList<String>> connectionsTransitionST = new HashMap<>();
        Map<String, ArrayList<String>> connectionsTransitionFT = new HashMap<>();
        Map<String, Integer> visitedTransitions = new HashMap<>();

        for (int i = 0; i < net.getElements().size(); i++) {
            if (Objects.equals(net.getElements().get(i).getType(), "pn.Transition")) {
                transitions.add(net.getElements().get(i).getId());
            }
        }

        getConnectionMap(net, transitions, connectionsTransitionST, connectionsTransitionFT);

        Queue<SimulationNet> queue = new LinkedList<>();
        queue.add(net);

        while (!queue.isEmpty()) {
            SimulationNet currentState = queue.poll();
            ArrayList<Integer> state = generateState(currentState);

            if (!states.contains(state)) {
                states.add(state);
                ArrayList<String> enabledTransitions = new ArrayList<>();


                for (String transitionKey : connectionsTransitionST.keySet()) {
                    boolean isEnabled = checkTransition(currentState, connectionsTransitionST, connectionsTransitionFT, transitionKey);
                    if (isEnabled) {
                        enabledTransitions.add(transitionKey);
                    }
                }

                for (String transition : enabledTransitions) {
                    if (visitedTransitions.get(transition) == null) {
                        visitedTransitions.put(transition, 1);
                    } else {
                        int visitCount = visitedTransitions.get(transition);
                        visitedTransitions.put(transition, visitCount + 1);
                    }
                    if (visitedTransitions.get(transition) < 50) {
                        SimulationNet nextState = currentState;
                        ArrayList<String> changes = new ArrayList<>();
                        fireTransition(nextState, transition, changes, connectionsTransitionST, connectionsTransitionFT);
                        queue.add(nextState);
                    }
                }
            }
        }

        return states;
    }
}
