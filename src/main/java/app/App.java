package app;

import java.util.Scanner;

public class App {

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        QueryEngine searchEngine = new QueryEngine(selectAlgorithm(scanner));

        searchEngine.buildIndex();
        searchEngine.executeQueries();

        // Shut down Search Engine after queries are run
        searchEngine.shutdown();

        scanner.close();
    }


    public static QueryEngine.ScoringMethod selectAlgorithm(Scanner scanner) {
        QueryEngine.ScoringMethod algorithm = null;
        while (algorithm == null) {
            System.out.println(
                    "Select scoring method:\n"
                            + "1)\tClassic Similarity\n"
                            + "2)\tBM25 Similarity\n"
                            + "3)\tBoolean Similarity\n"
                            + "4)\tLM Dirichlet Similarity\n"
                            + "5)\tDFI Similarity\n");
            String userResponse = scanner.nextLine();
            switch (userResponse) {
                case "1":
                    algorithm = QueryEngine.ScoringMethod.Classic;
                    break;
                case "2":
                    algorithm = QueryEngine.ScoringMethod.BM25;
                    break;
                case "3":
                    algorithm = QueryEngine.ScoringMethod.Boolean;
                    break;
                case "4":
                    algorithm = QueryEngine.ScoringMethod.LMDirichlet;
                    break;
                case "5":
                    algorithm = QueryEngine.ScoringMethod.DFISimilarity;
                    break;
                default:
                    break;
            }
        }
        return algorithm;
    }
}
