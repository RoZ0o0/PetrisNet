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

        ArrayList<String> changes = new ArrayList<>();

        if (!Objects.equals(net.getConnections().toString(), "[]")) {
            ArrayList<String> transitions = new ArrayList<>();
            ArrayList<String> circles = new ArrayList<>();
            Map<String, ArrayList<String>> connectionsTransitionST = new HashMap<>();
            Map<String, ArrayList<String>> connectionsCircles = new HashMap<>();
            Map<String, ArrayList<String>> connectionsTransitionFT = new HashMap<>();
            Map<String, Boolean> checking = new HashMap<>();
            for (int i = 0; i < net.getElements().size(); i++) {
                if (net.getElements().get(i).getName().charAt(1) == 'T') {
                    transitions.add(net.getElements().get(i).getName());
                }
                if (net.getElements().get(i).getName().charAt(1) == 'C') {
                    circles.add(net.getElements().get(i).getName());
                }
            }

            for(String transition: transitions) {
                checking.put(transition, true);
            }

            getConnectionMap(net, transitions, connectionsTransitionST, false);

            getConnectionMap(net, circles, connectionsCircles, true);

            getConnectionMap(net, transitions, connectionsTransitionFT, true);

            Random random = new Random();

            while (changes.isEmpty()) {

                int indexTransition = random.nextInt(connectionsTransitionST.size());

                String transitionKey = "";

                int counter = 0;

                for (String key : connectionsTransitionST.keySet()) {
                    if (counter == indexTransition) {
                        transitionKey = key;
                    }

                    counter++;
                }

                // gdy tranzycja nie jest podłączona jako drugi cel
                if (connectionsTransitionST.get(transitionKey).size() == 0) {
                    // gdy tranzycja jest podłączona jako pierwszy cel
                    if (connectionsTransitionFT.get(transitionKey).size() > 0) {
                        addByOutputArc(net, changes, connectionsTransitionFT, transitionKey);
                    }
                }
                // gdy tranzycja jest drugim celem oraz podłączone jest do niej jedno miejsce
                if (connectionsTransitionST.get(transitionKey).size() == 1) {
                    // gdy podłączone miejsce do tranzycji nie jest podłączone do innej tranzycji
                    if (findCircle(net, connectionsTransitionST.get(transitionKey).get(0)) >= 0) {
                        int numberOfTokens = net.getTokens().get(findCircle(net, connectionsTransitionST.get(transitionKey).get(0))).getToken_amount();
                        if (numberOfTokens > 0) {
                            net.getTokens().get(findCircle(net, connectionsTransitionST.get(transitionKey).get(0))).setToken_amount(numberOfTokens - 1);
                            if (net.getTokens().get(findCircle(net, connectionsTransitionST.get(transitionKey).get(0))).getToken_amount() == 0) {
                                deleteRedundantElements(net, connectionsTransitionST, transitionKey, 0);
                            }
                            changes.add(connectionsTransitionST.get(transitionKey).get(0) + " " + transitionKey);
                            addByOutputArc(net, changes, connectionsTransitionFT, transitionKey);
                        } else {
                            checking.put(transitionKey, false);
                        }
                    } else {
                        checking.put(transitionKey, false);
                    }
                }
                // gdy tranzycja jest drugim celem oraz połączne są do niej wiecej niż 1 miejsce
                if (connectionsTransitionST.get(transitionKey).size() > 1) {
                    ArrayList<Integer> tokens = new ArrayList<>();
                    for (int i = 0; i < connectionsTransitionST.get(transitionKey).size(); i++) {
                        if (findCircle(net, connectionsTransitionST.get(transitionKey).get(i)) == -1) {
                            tokens.add(0);
                        } else {
                            int numberOfTokens = net.getTokens().get(findCircle(net, connectionsTransitionST.get(transitionKey).get(i))).getToken_amount();
                            tokens.add(numberOfTokens);
                        }
                    }
                    boolean canSubstract = true;
                    for (Integer token : tokens) {
                        if (token < 1) {
                            canSubstract = false;
                            break;
                        }
                    }
                    if (canSubstract) {
                        for (int i = 0; i < connectionsTransitionST.get(transitionKey).size(); i++) {
                            net.getTokens().get(findCircle(net, connectionsTransitionST.get(transitionKey).get(i))).setToken_amount(tokens.get(i) - 1);
                            if (net.getTokens().get(findCircle(net, connectionsTransitionST.get(transitionKey).get(i))).getToken_amount() == 0) {
                                deleteRedundantElements(net, connectionsTransitionST, transitionKey, i);
                            }
                            changes.add(connectionsTransitionST.get(transitionKey).get(i) + " " + transitionKey);
                        }
                        addByOutputArc(net, changes, connectionsTransitionFT, transitionKey);
                    } else {
                        checking.put(transitionKey, false);
                    }
                }

                int checkCounter = 0;
                for (String key: checking.keySet()) {
                    if (!checking.get(key)) {
                        checkCounter++;
                    }
                }

                if (checkCounter == checking.size()) {
                    break;
                }
            }
        }

        ArrayList<Tokens> newTokens = new ArrayList<>();
        for (int i = 0; i < net.getTokens().size(); i++) {
            if (net.getTokens().get(i).getToken_amount() != 0) {
                newTokens.add(net.getTokens().get(i));
            }
        }

        net.setTokens(newTokens);

        net.setChanges(changes);

        return net;
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

                System.out.println(newToken.getName());

                net.setTokens(listOfTokens);
                changes.add("Added:" + connectionsTransitionFT.get(transitionKey).get(i));
                counter++;
            }
            int numberOfTokens = net.getTokens().get(findCircle(net, connectionsTransitionFT.get(transitionKey).get(i))).getToken_amount();
            net.getTokens().get(findCircle(net, connectionsTransitionFT.get(transitionKey).get(i))).setToken_amount(numberOfTokens + 1);
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
