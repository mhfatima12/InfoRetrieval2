package app;

import java.io.IOException;
import java.nio.file.Paths;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import static app.Constant.INDEX_DIRECTORY;
import app.parser.Fr94Parser;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.similarities.BM25Similarity;

public class IndexConstructor {

    /**
     * Initializes the Lucene index and directly streams documents into it without using excessive memory.
     * @param analyzer the analyzer used for indexing
     * @param similarity the similarity model for scoring
     * @throws IOException if an I/O error occurs
     */
    public static void initializeIndex(Analyzer analyzer, Similarity similarity) throws IOException {
        // Open the directory for the index
        try (Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY))) {
            // Set up the index writer configuration
            IndexWriterConfig config = configureIndexWriter(analyzer, similarity);
            
            try (IndexWriter writer = new IndexWriter(directory, config)) {
                // Create parser and start parsing and indexing directly
                Fr94Parser parser = new Fr94Parser();
                parser.parseAndIndexFR94("./docs/fr94/", writer);
                
                System.out.println("Indexing process completed successfully.");
            }
        }
    }

    // Helper method to configure IndexWriter with analyzer and similarity
    private static IndexWriterConfig configureIndexWriter(Analyzer analyzer, Similarity similarity) {
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        config.setSimilarity(similarity);
        return config;
    }

    public static void main(String[] args) throws IOException {
        // Initialize the index with direct parsing and indexing
        initializeIndex(new StandardAnalyzer(), new BM25Similarity());
    }
}
