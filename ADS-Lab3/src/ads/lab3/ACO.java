package ads.lab3;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class ACO {

    public static double alpha = 3.0; // pheromone
    public static double beta = 2.0; // heuristic
    public static double p = 0.7; // pheromone vaporizing velocity
    public static int antsNum = 45; // number of ants
    public static int nearestNeighbourSize = 20; // size of nearest neighbour list for each node

    private final int[][] graph;
    private int[][] nearestNeighbourList;
    private double[][] pheromone;
    private double[][] choiceInfo;
    private Ant[] ants;

    public ACO(int[][] graph) {
        super();
        this.graph = graph;
    }

    public void generateNearestNeighbourList() {
        nearestNeighbourList = new int[getNodesSize()][getNearestNeighbourSize()];

        for (int i = 0; i < getNodesSize(); i++) {
            Integer[] nodeIndex = new Integer[getNodesSize()];
            Double[] nodeData = new Double[getNodesSize()];
            for (int j = 0; j < getNodesSize(); j++) {
                nodeIndex[j] = j;
                nodeData[j] = getCost(i, j);
            }

            nodeData[i] = Collections.max(Arrays.asList(nodeData));
            Arrays.sort(nodeIndex, Comparator.comparingDouble(o -> nodeData[o]));
            for (int r = 0; r < getNearestNeighbourSize(); r++) {
                nearestNeighbourList[i][r] = nodeIndex[r];
            }
        }
    }

    public void generateAntPopulation() {
        ants = new Ant[getAntsNumber()];
        for (int k = 0; k < getAntsNumber(); k++) {
            ants[k] = new Ant(getNodesSize(), this);
        }
    }

    public void generateAntColony() {
        pheromone = new double[getNodesSize()][getNodesSize()];
        choiceInfo = new double[getNodesSize()][getNodesSize()];
        double initTrace = 1.0 / (p * ants[0].findNearestNeighbourTour());
        for (int i = 0; i < getNodesSize(); i++) {
            for (int j = i; j < getNodesSize(); j++) {
                pheromone[i][j] = initTrace;
                pheromone[j][i] = initTrace;
                choiceInfo[i][j] = initTrace;
                choiceInfo[j][i] = initTrace;
            }
        }
        calculateChoice();
    }

    public void calculateChoice() {
        for (int i = 0; i < getNodesSize(); i++) {
            for (int j = 0; j < i; j++) {
                double heuristic = (1.0 / (getCost(i, j) + 0.1));
                choiceInfo[i][j] = Math.pow(pheromone[i][j], alpha) * Math.pow(heuristic, beta);
                choiceInfo[j][i] = choiceInfo[i][j];
            }
        }
    }

    public void solve() {
        int stage = 0;
        for (int k = 0; k < getAntsNumber(); k++) {
            ants[k].clearVisited();
            ants[k].startAtRandomPos(stage);
        }

        while (stage < getNodesSize() - 1) {
            stage++;
            for (int k = 0; k < getAntsNumber(); k++) {
                ants[k].makeDecision(stage);
            }
        }

        for (int k = 0; k < getAntsNumber(); k++) {
            ants[k].finishTour();
        }
    }

    public void renewPheromone() {
        vaporizePheromone();
        for (int k = 0; k < getAntsNumber(); k++) {
            leavePheromone(ants[k]);
        }
        calculateChoice();
    }

    public void vaporizePheromone() {
        for (int i = 0; i < getNodesSize(); i++) {
            for (int j = i; j < getNodesSize(); j++) {
                pheromone[i][j] = (1 - p) * pheromone[i][j];
                pheromone[j][i] = pheromone[i][j];
            }
        }
    }

    public void leavePheromone(Ant ant) {
        double t = 1.0 / ant.getTourCost(); // t = tau
        for (int i = 0; i < getNodesSize(); i++) {
            int j = ant.getRouteStage(i);
            int l = ant.getRouteStage(i + 1);
            pheromone[j][l] = pheromone[j][l] + t;
            pheromone[l][j] = pheromone[j][l];
        }
    }

    public int getNodesSize() {
        return graph.length;
    }

    public int getNearestNeighbourSize() {
        return nearestNeighbourSize;
    }

    public double getCost(int from, int to) {
        return graph[from][to];
    }

    public int getAntsNumber() {
        return antsNum;
    }

    public int getNearestNeighbourNode(int from, int index) {
        return this.nearestNeighbourList[from][index];
    }

    public double getCostInfo(int from, int to) {
        return choiceInfo[from][to];
    }

    public Ant[] getAnts() {
        return ants;
    }
}