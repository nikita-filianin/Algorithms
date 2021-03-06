import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Population {

    private final int populationSize;
    private final int numCities;
    private Path[] population;
    private final double crossoverRate;
    private final double mutationRate;
    private final Path[] nextGen;
    private int done;

    public Population(int populationSize, int numCities, double crossoverRage, double mutationRate) {
        this.populationSize = populationSize;
        this.numCities = numCities;
        population = new Path[populationSize];
        this.crossoverRate = crossoverRage;
        this.mutationRate = mutationRate;
        this.nextGen = new Path[populationSize];
        Path p = new Path(numCities);
        done = 0;
        CreatePopulation(p);

    }

    public void CreatePopulation(Path p) {
        int i = 0;

        while (i < populationSize) {
            City[] tmpCity = new City[numCities];
            for (int j = 0; j < tmpCity.length; j++) {
                tmpCity[j] = p.getPath()[j];
            }
            Collections.shuffle(Arrays.asList(tmpCity));
            Path tmpPath = new Path(numCities);
            tmpPath.setPath(tmpCity);
            population[i] = tmpPath;
            i++;
        }
    }

    public int SelectParent() {
        // Selecting parents
        int totalCost = calculateTotalFitness();

        int fit = RandomNum(0, totalCost);
        int value = 0;
        for (int i = 0; i < population.length; i++) {
            value += population[i].getFitness();
            if (fit <= value) {
                return i;
            }
        }
        return -1;

    }

    public boolean Mate() {
        // Generate a random number to check if the parents cross
        int check = RandomNum(0, 100);
        int parent1 = SelectParent();
        int parent2 = SelectParent();
        while (parent1 == parent2) {
            parent2 = SelectParent();
        }

        // Check if there is going to be a crossover
        if (check <= (crossoverRate * 100)) {

            int crossoverPoint = RandomNum(0, population[parent1].getPath().length - 1);
            City[] child1 = new City[numCities];
            City[] child2 = new City[numCities];

            // Crossing over
            for (int i = 0; i < crossoverPoint; i++) {
                child1[i] = population[parent2].getPath()[i];
                child2[i] = population[parent1].getPath()[i];
            }
            for (int i = crossoverPoint; i < numCities; i++) {
                child1[i] = population[parent1].getPath()[i];
                child2[i] = population[parent2].getPath()[i];
            }


            // Rearrange children considering city repetition
            int cityChild1;
            int cityChild2;
            ArrayList<Integer> list1 = new ArrayList<>();
            ArrayList<Integer> list2 = new ArrayList<>();

            for (int i = 0; i < crossoverPoint; i++) {
                cityChild1 = child1[i].getId();
                cityChild2 = child2[i].getId();

                //Get the positions of repeated values
                for (int j = crossoverPoint; j < numCities; j++) {
                    if (cityChild1 == child1[j].getId()) {
                        list1.add(j);
                    }
                    if (cityChild2 == child2[j].getId()) {
                        list2.add(j);
                    }
                }
            }

            // Find the missing values
            for (int i = 0; i < numCities; i++) {
                boolean found = false;
                // Fixing Child1
                for (int j = 0; j < numCities; j++) {
                    if (population[parent2].getPath()[i] == child1[j]) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    child1[list1.remove(list1.size() - 1)] = population[parent2].getPath()[i];
                }
                found = false;
                // Fixing Child2
                for (int j = 0; j < numCities; j++) {
                    if (population[parent1].getPath()[i] == child2[j]) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    child2[list2.remove(list2.size() - 1)] = population[parent1].getPath()[i];
                }
            }


            return AddToGenerationCheckFull(child1, child2);


        } else {

            return AddToGenerationCheckFull(population[parent1].getPath(), population[parent1].getPath());
        }
    }

    public void setDone(int done) {
        this.done = done;
    }

    public boolean AddToGenerationCheckFull(City[] child1, City[] child2) {
        if (done == populationSize) {
            return true;
        }
        Path newGenChild1 = new Path(numCities);
        Path newGenChild2 = new Path(numCities);
        newGenChild1.setPath(child1);
        newGenChild2.setPath(child2);
        if (done < populationSize - 2) {
            this.nextGen[done] = newGenChild1;
            this.nextGen[done + 1] = newGenChild2;
            this.done += 2;
            return false;
        } else if (done == populationSize - 2) {
            this.nextGen[done] = newGenChild1;
            this.nextGen[done + 1] = newGenChild2;
            done += 2;
            return true;
        } else {
            this.nextGen[done] = newGenChild1;
            done += 1;
            return true;
        }

    }

    public Path[] getNextGen() {
        return nextGen;
    }


    public City[] Mutation(City[] child) {
        int check = RandomNum(0, 100);

        // Checks if it is going to mutate
        if (check <= (mutationRate * 100)) {

            // Finds the 2 cities that "mutate"
            int point1 = RandomNum(0, numCities - 1);
            int point2 = RandomNum(0, numCities - 1);
            while (point2 == point1) {
                point2 = RandomNum(0, numCities - 1);
            }

            // Cities are switched as a result of mutation
            City city1 = child[point1];
            City city2 = child[point2];
            child[point1] = city2;
            child[point2] = city1;

        }
        return child;
    }

    public int RandomNum(int min, int max) {
        return min + (new Random()).nextInt(max - min);
    }

    public void FitnessOrder() {
        int a = 0;
        Arrays.sort(population);
        for (Path path : population) {
            a = 100000 / (path.getCost() + 1);
            path.setFitness(a);
        }
    }

    public int calculateTotalFitness() {
        int cost = 0;
        for (Path path : population) {
            cost += path.getFitness();
        }
        return cost;
    }

    public Path[] getPopulation() {
        return population;
    }

    public void setPopulation(Path[] population) {
        this.population = population;
    }

}