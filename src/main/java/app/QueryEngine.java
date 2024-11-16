package app;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.BooleanSimilarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import static app.Constant.FBI_DIR;
import static app.Constant.FR94_DIR;
import static app.Constant.FT_DIR;
import static app.Constant.INDEX_DIRECTORY;
import static app.Constant.LATIMES_DIR;
import app.parser.FTParser;
import app.parser.FbisParser;
import app.parser.Fr94Parser;
import app.parser.LAtimesParser;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;

public class QueryEngine {

    private static final String QUERY_FILE_PATH = "cran/cran.qry";
    private final Analyzer analyzer;
    private final BasicQueryProcessor baseProcessor;
    private final ExpandedQueryProcessor expandedProcessor;
    private Similarity rankSimilarity;
    private Directory directory;

    public enum ScoringMethod { BM25, Classic, Boolean, LMDirichlet, DFISimilarity }

    public QueryEngine(ScoringMethod rankingMethod) {
        this.analyzer = new CustomAnalyser();
        this.baseProcessor = new BasicQueryProcessor();
        this.expandedProcessor = new ExpandedQueryProcessor();

        switch (rankingMethod) {
            case BM25:
                this.rankSimilarity = new BM25Similarity();
                break;
            case Classic:
                this.rankSimilarity = new ClassicSimilarity();
                break;
            case Boolean:
                this.rankSimilarity = new BooleanSimilarity();
                break;
            case DFISimilarity:
                // this.rankSimilarity = new DFISimilarity();
                break;
            case LMDirichlet:
                this.rankSimilarity = new LMDirichletSimilarity();
                break;
            default:
                break;
        }
        initializeDirectory();
    }

    private void initializeDirectory() {
        try {
            directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize directory", e);
        }
    }

    public void buildIndex() throws IOException, InterruptedException {
//        IndexConstructor indexConstructor = new IndexConstructor();
        //List<Document> docs;
        Directory dir = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(new CustomAnalyser());
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        indexWriterConfig.setSimilarity(new BM25Similarity());
        
        
        IndexWriter indexWriter = new IndexWriter(dir, indexWriterConfig);
        new FTParser().parseAndIndexFTDocs(FT_DIR, indexWriter);
        indexWriter.close();
        dir.close();
        //indexDocuments(docs, indexConstructor);
        
        indexWriterConfig = new IndexWriterConfig(new CustomAnalyser());
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        indexWriterConfig.setSimilarity(new BM25Similarity());
        dir = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
        indexWriter = new IndexWriter(dir, indexWriterConfig);
        new FbisParser().parseAndIndexFbis(FBI_DIR, indexWriter);
        indexWriter.close();
        dir.close();
        //indexDocuments(docs, indexConstructor);
        
        indexWriterConfig = new IndexWriterConfig(new CustomAnalyser());
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        indexWriterConfig.setSimilarity(new BM25Similarity());
        dir = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
        indexWriter = new IndexWriter(dir, indexWriterConfig);
        new Fr94Parser().parseAndIndexFR94(FR94_DIR, indexWriter);
        indexWriter.close();
        dir.close();
        //indexDocuments(docs, indexConstructor);
        
        indexWriterConfig = new IndexWriterConfig(new CustomAnalyser());
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        indexWriterConfig.setSimilarity(new BM25Similarity());
        dir = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
        indexWriter = new IndexWriter(dir, indexWriterConfig);
        new LAtimesParser().parseLAtimesAndIndex(LATIMES_DIR, indexWriter);
        indexWriter.close();
        dir.close();
        //indexDocuments(docs, indexConstructor);
    }

//    private void indexDocuments(List<Document> documents, IndexConstructor indexConstructor) throws IOException {
//        if (documents != null && !documents.isEmpty()) {
//            indexConstructor.initializeIndex(documents, analyzer, rankSimilarity);
//        }
//    }

    public void executeQueries() {
        System.out.println("Executing Queries\n1) Standard Query\n2) Expanded Query");

        try (Scanner scanner = new Scanner(System.in)) {
            String choice = scanner.nextLine();
            if ("1".equals(choice)) {
                baseProcessor.runQuery(analyzer, rankSimilarity);
            } else if ("2".equals(choice)) {
                expandedProcessor.runQuery(analyzer, rankSimilarity);
            } else {
                System.out.println("Invalid selection.");
            }
        } catch (Exception e) {
            System.err.println("Error running queries: " + e.getMessage());
        }
    }

    public void shutdown() throws IOException {
        if (directory != null) {
            directory.close();
        }
    }
}