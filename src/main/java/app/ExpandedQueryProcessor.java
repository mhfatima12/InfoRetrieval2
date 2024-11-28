package app;

import app.model.childModel.TopicModel;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queries.mlt.MoreLikeThisQuery;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
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


public class ExpandedQueryProcessor {
    
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

        List<String> results = new ArrayList<>();
        for (TopicModel topicModel : topicModels) {
            List<String> narrativeParts = parseTopicContent(topicModel.getNarrative(), topicModel);

            String combinedQuery = QueryParser.escape(
                    topicModel.getTitle() + " " + topicModel.getDescription() + " " + narrativeParts.get(0)
            );
            if (topicModel.isRelevant()) {
                combinedQuery = QueryParser.escape(topicModel.getTitle() + " " + topicModel.getDescription());
            }

            combinedQuery = combinedQuery.replaceAll("\\.", "");
            Query luceneQuery = queryParser.parse(combinedQuery);

            ScoreDoc[] scoreDocs = {};
            Query expandedQuery = expandQuery(indexSearcher, analyzer, luceneQuery, scoreDocs, indexReader);

            scoreDocs = indexSearcher.search(expandedQuery, Constant.MAX_CLAUSE).scoreDocs;
            for (int rankIndex = 0; rankIndex < scoreDocs.length; rankIndex++) {
                ScoreDoc docHit = scoreDocs[rankIndex];
                int rank = rankIndex + 1;
                results.add(
                        topicModel.getTopicNum() + " Q0 " 
                        + indexSearcher.doc(docHit.doc).get("docNumber") + " " 
                        + rank + " " 
                        + docHit.score + " CustomAnalyser"+ stringSimilarity
                );
            }
        }

        indexDirectory.close();
        indexReader.close();
        writeRank2File(results);
    }

    private Map<String, Float> createBoostMap() {
        Map<String, Float> boostMap = new HashMap<>();
        boostMap.put("docTitle", 0.1f);
        boostMap.put("docContent", 0.9f);
        return boostMap;
    }

    private static void writeRank2File(List<String> results) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Constant.RESULTS_DIR))) {
            for (String result : results) {
                writer.write(result);
                writer.newLine();
            }
        }
        System.out.println("Finished writing results.");
    }

    private static List<String> parseTopicContent(String narrative, TopicModel topicModel) {
        StringBuilder positiveContent = new StringBuilder();
        StringBuilder negativeContent = new StringBuilder();
        String[] narrativeSentences = narrative.toLowerCase().split("[.;?]");
        List<String> parsedContent = new ArrayList<>();

        for (String sentence : narrativeSentences) {
            if (!sentence.contains("not relevant") && !sentence.contains("irrelevant")) {
                String refined = sentence.replaceAll(
                        "a relevant document|a document will|to be relevant|relevant documents|a document must|relevant|will contain|will discuss|will provide|must cite", 
                        ""
                );
                positiveContent.append(refined);
                topicModel.setRelevant(false);
            } else {
                String refined = sentence.replaceAll(
                        "are also not relevant|are not relevant|are irrelevant|is not relevant", 
                        ""
                );
                negativeContent.append(refined);
                topicModel.setRelevant(true);
            }
        }
        parsedContent.add(positiveContent.toString());
        parsedContent.add(negativeContent.toString());

        return parsedContent;
    }

    private static Query expandQuery(IndexSearcher indexSearcher, Analyzer analyzer, Query baseQuery, ScoreDoc[] hits, IndexReader indexReader) throws Exception {
        BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();
        booleanQueryBuilder.add(baseQuery, BooleanClause.Occur.SHOULD);

        TopDocs topDocs = indexSearcher.search(baseQuery, 4);

        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document document = indexReader.document(scoreDoc.doc);
            String docContent = document.getField("docContent").stringValue();
            String[] moreLikeThisField = { "docContent" };

            MoreLikeThisQuery moreLikeThisQuery = new MoreLikeThisQuery(docContent, moreLikeThisField, analyzer, "docContent");
            Query expandedQuery = moreLikeThisQuery.rewrite(indexReader);
            booleanQueryBuilder.add(expandedQuery, BooleanClause.Occur.SHOULD);
        }

        return booleanQueryBuilder.build();
    }

    public static void main(String[] args) throws Exception {
        ExpandedQueryProcessor queryResolver = new ExpandedQueryProcessor();
        queryResolver.runQuery(new CustomAnalyser(), new BM25Similarity());
    }
}