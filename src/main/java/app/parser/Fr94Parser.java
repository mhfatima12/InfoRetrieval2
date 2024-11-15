package app.parser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Fr94Parser {

    private static final Logger LOGGER = Logger.getLogger(Fr94Parser.class.getName());
    private final List<Document> doclist = new ArrayList<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public List<Document> parseFR94(String path) throws InterruptedException {
        LOGGER.info("Starting to parse FR94 documents from path: " + path);

        File[] directories = new File(path).listFiles(File::isDirectory);
        if (directories == null) {
            LOGGER.warning("No directories found in the specified path.");
            return doclist;
        }

        FieldType fieldType = new FieldType(TextField.TYPE_STORED);
        fieldType.setStoreTermVectors(true);

        List<Future<List<Document>>> futures = new ArrayList<>();

        for (File directory : directories) {
            //LOGGER.info("Processing directory: " + directory.getName());

            File[] files = directory.listFiles();
            if (files == null) {
                LOGGER.warning("No files found in directory: " + directory.getName());
                continue;
            }

            for (File file : files) {
                Future<List<Document>> future = executor.submit(() -> parseFile(file, fieldType));
                futures.add(future);
            }
        }

        for (Future<List<Document>> future : futures) {
            try {
                doclist.addAll(future.get());
            } catch (ExecutionException e) {
                LOGGER.log(Level.SEVERE, "Error parsing file: ", e);
            }
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);

        LOGGER.info("Completed parsing FR94 documents. Total documents parsed: " + doclist.size());
        return doclist;
    }

    private List<Document> parseFile(File file, FieldType fieldType) {
        List<Document> documents = new ArrayList<>();

        try {
            org.jsoup.nodes.Document doc = Jsoup.parse(file, StandardCharsets.UTF_8.name());
            Elements elements = doc.select("DOC");

            for (Element element : elements) {
                String docNumber = element.select("DOCNO").text().trim();
                String docTitle = element.select("DOCTITLE").text().trim();
                String docContent = element.select("TEXT").text().trim();

                if (docNumber.isEmpty() || docContent.isEmpty()) {
                    //LOGGER.warning("Skipping document with missing essential fields in file: " + file.getName());
                    continue;
                }

                Document luceneDoc = new Document();
                luceneDoc.add(new StringField("docNumber", docNumber, Field.Store.YES));
                luceneDoc.add(new Field("docTitle", docTitle, fieldType));
                luceneDoc.add(new Field("docContent", docContent, fieldType));

                documents.add(luceneDoc);
                //LOGGER.info("Parsed document: " + docNumber + " from file: " + file.getName());
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to parse file: " + file.getName(), e);
        }

        return documents;
    }

    public static void main(String[] args) {
        String path = "./Documents/fr94/";
        Fr94Parser parser = new Fr94Parser();

        try {
            List<Document> documents = parser.parseFR94(path);
            LOGGER.info("Total documents parsed: " + documents.size());
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Parsing interrupted", e);
        }
    }
}
