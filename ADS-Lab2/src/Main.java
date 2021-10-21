import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        BTree tree = new BTree(25);
        tree.getTree();

//        tree.add(8, "Alfa Romeo");
//        tree.add(25, "Aston Martin");
//        tree.add(56, "Audi");
//        tree.add(74, "Tesla");
//        tree.add(97, "Bentley");
//        tree.add(121, "BMW");
//        tree.add(142, "Porsche");
//        tree.add(154, "Cadillac");
//        tree.add(157, "Chevrolet");
//        tree.add(168, "Chrysler");
//        tree.add(175, "Citroen");
//        tree.add(180, "Dacia");
//        tree.add(193, "Dodge");
//        tree.add(197, "Ferrari");
//        tree.add(204, "Fiat");
//        tree.add(209, "Ford");
//        tree.add(218, "Volkswagen");
//        tree.add(224, "Honda");
//        tree.add(265, "Hummer");
//        tree.add(384, "Hyundai");
//        tree.add(424, "Opel");
//        tree.add(475, "Isuzu");
//        tree.add(502, "Jaguar");
//        tree.add(572, "Jeep");
//        tree.add(592, "Kia");
//        tree.add(613, "Lamborghini");
//        tree.add(672, "Mercedes-Benz");
//        tree.add(721, "Toyota");
//        tree.add(824, "Mitsubishi");

        tree.traverse();

        System.out.println(tree.search(592));
        tree.search(154);
        tree.search(168);
        tree.search(193);
        tree.search(218);
        tree.search(265);
        tree.search(384);
        tree.search(475);
        tree.search(224);
        tree.search(572);
        tree.search(502);
        tree.search(592);
        tree.search(613);
        tree.search(672);
        tree.search(721);
        tree.search(56);
        tree.search(97);

//       tree.remove(175);
//        tree.remove(424);
//        System.out.println();
//        tree.traverse();


        tree.saveData();
//        tree.getTree();
//        tree.traverse();
    }
}