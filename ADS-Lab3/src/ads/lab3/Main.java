package ads.lab3;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        ACO ACO = new ACO(TSP.getDistances());

        ACO.generateNearestNeighbourList();
        ACO.generateAntPopulation();
        ACO.generateAntColony();

        int n = 0;
        int avgValue = 0;
        int iterations = 1000;
        while (n < iterations) {
            ACO.solve();
            ACO.renewPheromone();
            if ((n + 1) % 20 == 0) {
                Ant[] ants = ACO.getAnts();
                double[] tourCosts = Arrays.stream(ants).mapToDouble(Ant::getTourCost).sorted().toArray();
                avgValue += tourCosts[0];
                System.out.println("After " + (n + 1) + " iterations, the objective function value is: " + tourCosts[0]);
            }
            n++;
        }
        avgValue = avgValue / 50;
        System.out.println("--------------------------------------------------------------");
        System.out.println("Average value of the objective function is: " + avgValue);
    }
}
