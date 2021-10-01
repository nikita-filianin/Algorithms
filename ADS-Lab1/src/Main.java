import puzzle.*;
import search.*;

import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        int[] init = {0, 5, 4, 2, 7, 1, 8, 6, 3};
        int[] goal = {1, 2, 3, 4, 5, 6, 7, 8, 0};
        letsGo(init, goal);

    }

    private static void letsGo(int[] initial, int[] goal) {
        String input;
        Heuristic heuristic;
        Node start;
        System.out.print("Choose algorithm:\n\t1)BFS\n\t2)RBFS\n\t>");
        input = scanner.nextLine();
        switch (input) {
            case "1":
                heuristic = new Heuristic(goal, -1);
                start = new Node(initial, heuristic);
                BFS bfs = new BFS(start);
                System.out.println("\n---------- BFS ----------");
                bfs.solve().printState();
                break;
            case "2":
                heuristic = new Heuristic(goal, 1);
                start = new Node(initial, heuristic);
                RBFS rbfs = new RBFS(start);
                System.out.println("\n---------- RBFS ----------");
                rbfs.solve().printState();
                break;
        }
    }
}
