package ads.lab3;

import java.util.Arrays;

public class Ant {

    private final ACO ACO;
    private double tourCost;
    private final int[] tour;
    private final boolean[] visited;

    public Ant(int tourSize, ACO ACO) {
        super();
        this.tour = new int[tourSize + 1];
        this.visited = new boolean[tourSize];
        this.ACO = ACO;
    }

    public double findNearestNeighbourTour() {
        int stage = 0;
        clearVisited();
        startAtRandomPos(stage);
        while (stage < ACO.getNodesSize() - 1) {
            stage++;
            goToNextBest(stage);
        }
        finishTour();
        clearVisited();
        return this.tourCost;
    }

    public void clearVisited() {
        Arrays.fill(visited, false);
    }

    public void startAtRandomPos(int stage) {
        tour[stage] = (int) (Math.random() * ACO.getNodesSize());
        visited[tour[stage]] = true;
    }

    public void goToNextBest(int stage) {
        int nextCity = ACO.getNodesSize();

        int currentCity = tour[stage - 1];

        double minDist = Double.MAX_VALUE;

        for (int city = 0; city < ACO.getNodesSize(); city++) {
            if (!visited[city] && ACO.getCost(currentCity, city) < minDist) {
                nextCity = city;
                minDist = ACO.getCost(currentCity, city);
            }
        }

        tour[stage] = nextCity;
        visited[nextCity] = true;
    }

    public double findTourCost() {
        double tourCost = 0.0;
        for (int i = 0; i < ACO.getNodesSize(); i++) {
            tourCost += ACO.getCost(tour[i], tour[i + 1]);
        }
        return tourCost;
    }

    public void finishTour() {
        tour[ACO.getNodesSize()] = tour[0];
        tourCost = findTourCost();
    }

    public void makeDecision(int stage) {
        int currentCity = this.tour[stage - 1];
        double sumProb = 0.0;

        double[] choiceProb = new double[ACO.getNearestNeighbourSize() + 1];

        for (int j = 0; j < ACO.getNearestNeighbourSize(); j++) {
            if (visited[ACO.getNearestNeighbourNode(currentCity, j)]) {
                choiceProb[j] = 0.0;
            } else {
                choiceProb[j] = ACO.getCostInfo(currentCity, ACO.getNearestNeighbourNode(currentCity, j));
                sumProb += choiceProb[j];
            }
        }
        if (sumProb <= 0) {
            goToNextBest(stage);
        } else {
            double random = Math.random() * sumProb;
            int j = 0;
            double probability = choiceProb[j];

            while (probability <= random || j < ACO.getNearestNeighbourSize()) {
                j++;
                probability += choiceProb[j];
            }

            if (j == ACO.getNearestNeighbourSize()) {
                goToBestNeighbour(stage);
                return;
            }

            tour[stage] = ACO.getNearestNeighbourNode(currentCity, j);
            visited[this.tour[stage]] = true;
        }
    }

    public void goToBestNeighbour(int stage) {
        int helpCity;
        int nextCity = ACO.getNodesSize();

        int currentCity = this.tour[stage - 1];

        double bestValue = -1.0;
        double help;

        for (int i = 0; i < ACO.getNearestNeighbourSize(); i++) {
            helpCity = ACO.getNearestNeighbourNode(currentCity, i);
            if (!this.visited[helpCity]) {
                help = ACO.getCostInfo(currentCity, helpCity);
                if (help > bestValue) {
                    bestValue = help;
                    nextCity = helpCity;
                }
            }
        }
        if (nextCity == ACO.getNodesSize()) {
            goToNextBest(stage);
        } else {
            tour[stage] = nextCity;
            visited[this.tour[stage]] = true;
        }
    }

    public double getTourCost() {
        return tourCost;
    }

    public int getRouteStage(int stage) {
        return tour[stage];
    }
}