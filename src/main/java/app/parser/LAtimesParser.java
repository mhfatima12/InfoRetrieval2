package app.parser;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LAtimesParser {
    public List<Document> parseLAtimes(String path) throws IOException {
        System.out.println("Parsing LA Times documents");

        // Get all files in the directory
        File[] files = new File(path).listFiles();
        if (files == null) {
            throw new IOException("Invalid directory path or no files found in: " + path);
        }

        List<LatimesModel> modelList = new ArrayList<>();

        // Iterate over each file and parse
        for (File file : files) {
            if (file.isFile()) {
                org.jsoup.nodes.Document document = Jsoup.parse(file, "UTF-8", "");
                Elements elements = document.select("DOC");

                for (Element element : elements) {
                    LatimesModel latimesModel = new LatimesModel();
                    latimesModel.setDocNo(element.select("DOCNO").text());
                    latimesModel.setContent(element.select("TEXT").text());
                    latimesModel.setHeadline(element.select("HEADLINE").text());
                    modelList.add(latimesModel);
                }
            }
        }

        return addDocument(modelList);
    }

    /**
     * Converts parsed models to Lucene Documents.
     * @param latimesModels List of LatimesModel objects.
     * @return List of Lucene Documents.
     */
    private List<Document> addDocument(List<LatimesModel> latimesModels) {
        List<Document> docList = new ArrayList<>();

        for (LatimesModel latimesModel : latimesModels) {
            Document document = new Document();

            // Add DOCNO as StringField (indexed but not tokenized)
            document.add(new StringField("docNumber", latimesModel.getDocNo(), Field.Store.YES));

            // Custom FieldType with term vectors for content and headline
            FieldType fieldType = new FieldType(TextField.TYPE_STORED);
            fieldType.setStoreTermVectors(true);

            // Add content and headline with custom FieldType
            document.add(new Field("docContent", latimesModel.getContent(), fieldType));
            document.add(new Field("docTitle", latimesModel.getHeadline(), fieldType));

            docList.add(document);
        }

        return docList;
    }

    public static void main(String[] args) {
        try {
            LAtimesParser parser = new LAtimesParser();
            List<Document> documents = parser.parseLAtimes("./docs/latimes");
            System.out.println("Total documents parsed: " + documents.size());
        } catch (IOException e) {
            System.err.println("Error during parsing: " + e.getMessage());
        }
    }
}
class LatimesModel {
    private String docNo;
    private String content;
    private String headline;

    public String getDocNo() {
        return docNo;
    }

    public void setDocNo(String docNo) {
        this.docNo = docNo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }
}
