package app;

import java.util.Scanner;

public class App {

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        QueryEngine searchEngine = new QueryEngine(selectAlgorithm(scanner));

        // Build index and Populate it using the datasets,
        // process to extract relevant data and then store it using index writer.
        searchEngine.buildIndex();

        // Extract and build queries and use Boolean and Term Query to search the index
        // Write out the scores and document ids in results file
        searchEngine.runQueries();

        // Shut down Search Engine after queries are run
        searchEngine.shutdown();

        scanner.close();
    }


    public static QueryEngine.ScoringAlgorithm selectAlgorithm(Scanner scanner) {
        QueryEngine.ScoringAlgorithm algorithm = null;
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
                    algorithm = QueryEngine.ScoringAlgorithm.Classic;
                    break;
                case "2":
                    algorithm = QueryEngine.ScoringAlgorithm.BM25;
                    break;
                case "3":
                    algorithm = QueryEngine.ScoringAlgorithm.Boolean;
                    break;
                case "4":
                    algorithm = QueryEngine.ScoringAlgorithm.LMDirichlet;
                    break;
                case "5":
                    algorithm = QueryEngine.ScoringAlgorithm.DFISimilarity;
                    break;
                default:
                    break;
            }
        }
        return algorithm;
    }
}
