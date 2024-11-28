package app;

import app.model.childModel.TopicModel;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.lucene.search.similarities.BooleanSimilarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.DFISimilarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;

public class BasicQueryProcessor {
    
    public String getSimilarity(Similarity similarity)
    {
        String stringSimilarity = "";
        if (similarity instanceof BM25Similarity) {
            return stringSimilarity = "BM25";
        } 
        else if (similarity instanceof ClassicSimilarity) {
            return stringSimilarity = "Classic";
        }
        else if (similarity instanceof BooleanSimilarity) {
            return stringSimilarity = "Boolean";
        }
        else if (similarity instanceof DFISimilarity) {
            return stringSimilarity = "DFI";
        }
        else if (similarity instanceof LMDirichletSimilarity) {
            return stringSimilarity = "LMDirichlet";
        }
        return stringSimilarity;
        
    }

    public void runQuery(Analyzer analyzer, Similarity similarity) throws Exception {
        System.out.println("Running Queries Now...");
        String stringSimilarity = getSimilarity(similarity);
        Directory indexDirectory = FSDirectory.open(Paths.get(Constant.INDEX_DIRECTORY));
        IndexReader indexReader = DirectoryReader.open(indexDirectory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        indexSearcher.setSimilarity(similarity);

        MultiFieldQueryParser queryParser = new MultiFieldQueryParser(Constant.searchFields, analyzer, createBoostMap());

        QueryBuilder queryBuilder = new QueryBuilder();
        List<TopicModel> topicModels = queryBuilder.parseQuery(Constant.TOPICS);

        List<String> rankedResults = new ArrayList<>();
        for (TopicModel topic : topicModels) {
            String parsedQuery = QueryParser.escape(topic.getQuery().trim());
            Query luceneQuery = queryParser.parse(parsedQuery);
            ScoreDoc[] searchResults = indexSearcher.search(luceneQuery, Constant.MAX_CLAUSE).scoreDocs;

            for (int rankIndex = 0; rankIndex < searchResults.length; rankIndex++) {
                ScoreDoc documentHit = searchResults[rankIndex];
                int rank = rankIndex + 1;
                rankedResults.add(
                        topic.getTopicNum() + " Q0 " 
                        + indexSearcher.doc(documentHit.doc).get("docNumber") + " " 
                        + rank + " " 
                        + documentHit.score + " CustomAnalyser"+ stringSimilarity
                );
            }
        }

        indexDirectory.close();
        indexReader.close();
        writeRank2File(rankedResults);
    }

    private Map<String, Float> createBoostMap() {
        Map<String, Float> boostMap = new HashMap<>();
        boostMap.put("docTitle", 0.3f);
        boostMap.put("docAuthor", 0.05f);
        boostMap.put("docContent", 0.7f);

        return boostMap;
    }

    private static void writeRank2File(List<String> rankedResults) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Constant.RESULTS_DIR))) {
            for (String result : rankedResults) {
                writer.write(result);
                writer.newLine();
            }
        }
        System.out.println("Finished writing results.");
    }

    public static void main(String[] args) throws Exception {
        BasicQueryProcessor queryResolver = new BasicQueryProcessor();
        queryResolver.runQuery(new CustomAnalyser(), new BM25Similarity());
    }
}