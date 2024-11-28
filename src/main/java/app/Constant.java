package app;

public interface Constant {
    String INDEX_DIRECTORY = "index";

    String RESULTS_FILE = "results/out-";

    String RESULTS_DIR = "results/results.txt";

    String FBI_DIR = "docs/fbis";
    
    String FR94_DIR = "docs/fr94";
    
    String LATIMES_DIR = "docs/latimes";

    String FT_DIR = "docs/ft";

    String STOPWORD_FILE = "stopwords.txt";

    String TOPICS = "topics";

    String[] searchFields = {"docTitle", "docContent"};

    Integer MAX_CLAUSE = 1000;

}