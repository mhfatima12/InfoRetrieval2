package app;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
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
import org.apache.lucene.search.similarities.DFISimilarity;
import org.apache.lucene.search.similarities.IndependenceStandardized;

public class QueryEngine {

    private static final String QUERY_FILE_PATH = "cran/cran.qry";
    private final Analyzer analyzer;
    private final BasicQueryProcessor baseProcessor;
    private final ExpandedQueryProcessor expandedProcessor;
    private Similarity rankSimilarity;
    private Directory directory;
    private IndexWriterConfig indexWriterConfig;
    private IndexWriter indexWriter;

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
                this.rankSimilarity = new DFISimilarity(new IndependenceStandardized());
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
    private void initializeIndexWriterConfig() {
            indexWriterConfig = new IndexWriterConfig(new CustomAnalyser());
            indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            indexWriterConfig.setSimilarity(rankSimilarity);
    }
    
    private void initializeIndexWriter() {
            try {
            indexWriter = new IndexWriter(directory, indexWriterConfig);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize Indexwriter", e);
        }
    }
    
    private void closeDirectoryAndIndexWriter() {
        try{
            indexWriter.close();
            directory.close();
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to close directory & indexwriter", e);
        }
    }
    
    private void parseAndIndexfiles(String filesToBeParsed) {
        
        initializeDirectory();
        initializeIndexWriterConfig();
        initializeIndexWriter();
              
        switch (filesToBeParsed) {
            case "FT":
                try{
                new FTParser().parseAndIndexFTDocs(FT_DIR, indexWriter);
                break;
                }
                catch (IOException e) {
                    throw new RuntimeException("Failed to parse FT directory", e);
                }
            case "FBI":
                try{
                new FbisParser().parseAndIndexFbis(FBI_DIR, indexWriter);
                break;
                }
                catch (IOException e) {
                    throw new RuntimeException("Failed to FBI directory", e);
                }
            case "FR94":
                try{
                new Fr94Parser().parseAndIndexFR94(FR94_DIR, indexWriter);
                break;
                }
                catch (IOException e) {
                    throw new RuntimeException("Failed to FR94 directory", e);
                }
            case "LATIMES":
                try{
                new LAtimesParser().parseLAtimesAndIndex(LATIMES_DIR, indexWriter);
                break;
                }
                catch (IOException e) {
                    throw new RuntimeException("Failed to LATIMES directory", e);
                }
            default:
                break;
        }
        closeDirectoryAndIndexWriter();
    }
    
    

    public void buildIndex() throws IOException, InterruptedException {
            
          parseAndIndexfiles("FT");
          parseAndIndexfiles("FBI");
          parseAndIndexfiles("FR94");
          parseAndIndexfiles("LATIMES");
    }


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