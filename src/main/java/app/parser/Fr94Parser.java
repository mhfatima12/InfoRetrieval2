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

public class Fr94Parser {

    private final List<Document> doclist = new ArrayList<>();

    public List<Document> parseFR94(String path) throws IOException {
        System.out.println("Parsing FR94 documents...");

        File[] directories = new File(path).listFiles(File::isDirectory);
        if (directories == null) return doclist;

        FieldType fieldType = new FieldType(TextField.TYPE_STORED);
        fieldType.setStoreTermVectors(true);

        for (File directory : directories) {
            File[] files = directory.listFiles();
            if (files == null) continue;

            for (File file : files) {
                org.jsoup.nodes.Document doc = Jsoup.parse(file, null, "");
                Elements elements = doc.select("DOC");

                for (Element element : elements) {
                    String docNumber = element.select("DOCNO").text();
                    String docTitle = element.select("DOCTITLE").text();
                    String docContent = element.select("TEXT").text();

                    Document luceneDoc = new Document();
                    luceneDoc.add(new StringField("docNumber", docNumber, Field.Store.YES));
                    luceneDoc.add(new Field("docTitle", docTitle, fieldType));
                    luceneDoc.add(new Field("docContent", docContent, fieldType));

                    doclist.add(luceneDoc);
                    //System.out.println("Parsed document: " + docNumber);
                }
            }
        }
        System.out.println("Parsing FR94 done...");
        return doclist;
    }

    public static void main(String[] args) throws IOException {
        new Fr94Parser().parseFR94("./docs/fr94/");
    }
}