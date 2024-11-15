package app;

import app.model.childModel.TopicModel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueryBuilder {

    public List<TopicModel> parseQuery(String queryFilePath) throws IOException {
        File inputFile = new File(queryFilePath);
        org.jsoup.nodes.Document htmlDocument = Jsoup.parse(inputFile, "UTF-8", "");
        Elements topicElements = htmlDocument.select("top");
        List<TopicModel> topics = new ArrayList<>();
        for (Element topicElement : topicElements) {
            TopicModel topic = new TopicModel();
            topic.setTopicNum(topicElement.select("num").text().split(":")[1].substring(1, 4));
            topic.setTitle(topicElement.select("title").text());
            topic.setDescription(topicElement.select("desc").text().toLowerCase());
            topic.setNarrative(topicElement.select("narr").text().toLowerCase());
            topic.setRelevant(true);
            topics.add(topic);
        }

        return getQueryList(topics);
    }

    private List<TopicModel> getQueryList(List<TopicModel> topics) {
        for (TopicModel topic : topics) {
            topic.setQuery(analyseQuery(topic.toString()));
        }

        return topics;
    }

    private String analyseQuery(String queryText) {
        return removeStopword(queryText);
    }

    private String removeStopword(String text) {
        StringBuilder filteredText = new StringBuilder();

        List<String> stopwords = Arrays.asList(readFile(Constant.STOPWORD_FILE).toLowerCase().split("\n"));
        String[] sentences = text.split("\\.");
        for (String sentence : sentences) {
            String[] tokens = sentence.trim().split("\\s+");
            for (String token : tokens) {
                if (!token.isEmpty() && !stopwords.contains(token)) {
                    filteredText.append(token).append(" ");
                }
            }
        }

        return filteredText.toString().trim();
    }

    private String readFile(String path) {
        String content = "";
        try {
            content = new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }

    public static void main(String[] args) throws IOException {
        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.parseQuery("topics");
        System.out.println();
    }
}
