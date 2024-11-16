package app.parser;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.apache.lucene.index.IndexWriter;

public class LAtimesParser {

    /**
     * Parses LA Times documents and directly indexes them to reduce memory usage.
     * @param path The directory containing the LA Times XML files.
     * @param writer The IndexWriter used to index the documents.
     * @throws IOException if there is an error while reading files.
     */
    public void parseLAtimesAndIndex(String path, IndexWriter writer) throws IOException {
        System.out.println("Parsing and indexing LA Times documents from path: " + path);

        // Get all files in the directory
        File[] files = new File(path).listFiles();
        if (files == null) {
            throw new IOException("Invalid directory path or no files found in: " + path);
        }

        // FieldType for storing content and headline with term vectors
        FieldType fieldType = new FieldType(TextField.TYPE_STORED);
        fieldType.setStoreTermVectors(true);

        // Iterate over each file and parse
        for (File file : files) {
            if (file.isFile()) {
                org.jsoup.nodes.Document document = Jsoup.parse(file, "UTF-8", "");
                Elements elements = document.select("DOC");

                // Process each element and index immediately
                for (Element element : elements) {
                    String docNo = element.select("DOCNO").text().trim();
                    String content = element.select("TEXT").text().trim();
                    String headline = element.select("HEADLINE").text().trim();

                    if (!docNo.isEmpty() && !content.isEmpty()) {
                        // Immediately index the parsed document (avoid storing in memory)
                        Document luceneDoc = new Document();
                        luceneDoc.add(new StringField("docNumber", docNo, Field.Store.YES));
                        luceneDoc.add(new Field("docContent", content, fieldType));
                        luceneDoc.add(new Field("docTitle", headline, fieldType));

                        // Index the document immediately
                        writer.addDocument(luceneDoc);
                    }
                }
            }
        }

        System.out.println("Completed parsing and indexing. Documents indexed.");
    }

   
}
