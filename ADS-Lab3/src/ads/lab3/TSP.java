package ads.lab3;

import java.util.Random;

public class TSP {
    public static int[][] getDistances() {
        int[][] distances = new int[200][200]; // number of distances for 200 nodes
        fillDistances(distances);
        return distances;
    }

    private static void fillDistances(int[][] distances) {
        for (int i = 0; i < distances.length; i++) {
            for (int j = i; j < distances.length; j++) {
                if (i == j)
                    continue;
                Random random = new Random();
                distances[i][j] = random.nextInt(40) + 1; // the distance between nodes is random from 1 to 40
                distances[i][j] += (double) (Math.round(random.nextDouble() * 200)) / 200;
                distances[j][i] = distances[i][j];
            }
        }
    }
}

