package app;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import static app.Constant.INDEX_DIRECTORY;
import app.parser.Fr94Parser;

public class IndexConstructor {

    // Static method for creating the index, without instantiating IndexBuilder
    public static void initializeIndex(List<Document> documents, Analyzer analyzer, Similarity similarity) throws IOException {
        try (Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY))) {
            IndexWriterConfig config = configureIndexWriter(analyzer, similarity);
            try (IndexWriter writer = new IndexWriter(directory, config)) {
                indexDocuments(writer, documents);
                System.out.println("Indexing process completed successfully.");
            }
        }
    }

    // Private helper method to set up IndexWriterConfig
    private static IndexWriterConfig configureIndexWriter(Analyzer analyzer, Similarity similarity) {
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        config.setSimilarity(similarity);
        return config;
    }

    // Helper method to add documents to the index writer
    private static void indexDocuments(IndexWriter writer, List<Document> documents) {
        documents.forEach(document -> {
            try {
                writer.addDocument(document);
            } catch (IOException e) {
                System.err.println("Failed to add document to index: " + e.getMessage());
            }
        });
    }

    // Stopwords removal process placeholder
    private static void cleanDocuments(List<Document> documents) {
        // Implement logic to remove stopwords if necessary
    }

    public static void main(String[] args) throws IOException, InterruptedException  {
        Fr94Parser parser = new Fr94Parser();
        List<Document> documents = parser.parseFR94("./docs/fr94/");
        cleanDocuments(documents); 
        
        //initializeIndex(documents, new StandardAnalyzer(), new BM25Similarity());
    }
}