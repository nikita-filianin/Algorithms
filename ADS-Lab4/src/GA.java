public class GA {

    public static void main(String[] args) {
        long start = System.nanoTime();  // computation time
        int populationSize = 30; // population size
        int numCities = 15; // number of cities
        double mutationRate = 1.5; // mutation rate
        double crossoverRate = 0.8; // crossover Rate
        int numberOfGenerations = 0; // current number of generations
        int stopAt = 100; // stop condition

        // Population
        Population pop;
        pop = new Population(populationSize, numCities, crossoverRate, mutationRate);

        // Sorting the population from Fitness / Evaluating
        pop.FitnessOrder();

        // Prints each path ID/Cost/City order(with coordinates)
        for (int i = 0; i < pop.getPopulation().length; i++) {
            System.out.println("Path ID: " + i + " | Cost: " + pop.getPopulation()[i].getCost());
            System.out.print("Path is: ");
            for (int j = 0; j < pop.getPopulation()[i].getPath().length; j++) {
                System.out.print(pop.getPopulation()[i].getPath()[j].getId() + "(" + pop.getPopulation()[i].getPath()[j].getX() + "," + pop.getPopulation()[i].getPath()[j].getY() + ")  ");

            }

            System.out.println("\n------------------------------------------------------");

            if (i == 29) {
                System.out.println();
                System.out.println("Cost of the best path found: " + pop.getPopulation()[i].getCost());
                System.out.println();
            }
        }

        // Start looking for possible solution
        while (numberOfGenerations != stopAt) {

            // Select / Crossover
            while (!pop.Mate()) ;
            // Mutate
            for (int i = 0; i < pop.getNextGen().length; i++) {
                pop.getNextGen()[i].setPath(pop.Mutation(pop.getNextGen()[i].getPath()));

            }

            // Setting the new Generation to current Generation
            pop.setPopulation(pop.getNextGen());
            pop.setDone(0);
            // Sorting the new population from Fitness / Evaluating
            pop.FitnessOrder();
            // Increment number of Generations
            numberOfGenerations++;
        }

        System.out.println("Algorithm has finished its work due to the reached limit of " + stopAt + " generations");
        long elapsedTime = System.nanoTime() - start;
        System.out.println("Algorithm took about " + elapsedTime / 1000000 + " milliseconds to find a solution");
    }
}