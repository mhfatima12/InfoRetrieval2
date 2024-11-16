package app.parser;

import org.apache.lucene.document.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;

import app.model.childModel.FTModel;
import org.apache.lucene.index.IndexWriter;

public class FTParser {

    /**
     * Parses FT documents and directly indexes them to reduce memory usage.
     * @param path The directory containing the FT documents.
     * @param writer The IndexWriter to add documents to the index.
     * @throws IOException if an I/O error occurs.
     */
    public void parseAndIndexFTDocs(String path, IndexWriter writer) throws IOException {
        System.out.println("Parsing and indexing FT documents from path: " + path);
        
        File[] directories = new File(path).listFiles(File::isDirectory);
        
        for (File directory : directories) {
            File[] files = directory.listFiles();
            for (File file : files) {
                // Parse the document
                org.jsoup.nodes.Document doc = Jsoup.parse(file, null, "");
                Elements elements = doc.select("DOC");

                // Process each element and index immediately
                for (Element element : elements) {
                    FTModel ftDoc = parseFTDocument(element);
                    Document luceneDoc = createLuceneDocument(ftDoc);
                    
                    // Index the document immediately
                    writer.addDocument(luceneDoc);
                }
            }
        }
        
        System.out.println("Indexing process completed.");
    }

    /**
     * Parses the FT document from the element.
     * @param element The document element.
     * @return The parsed FTModel object.
     */
    private FTModel parseFTDocument(Element element) {
        FTModel ftDoc = new FTModel();
        ftDoc.setTitle(element.select("HEADLINE").text());
        ftDoc.setDocNo(element.select("DOCNO").text());
        ftDoc.setContent(element.select("TEXT").text());
        return ftDoc;
    }

    /**
     * Converts FTModel to a Lucene Document.
     * @param ftModel The FTModel object containing document data.
     * @return The corresponding Lucene Document.
     */
    private Document createLuceneDocument(FTModel ftModel) {
        Document document = new Document();
        document.add(new StringField("docNumber", ftModel.getDocNo(), Field.Store.YES));

        // Use a FieldType with term vectors for storing text and its analysis
        FieldType fieldType = new FieldType(TextField.TYPE_STORED);
        fieldType.setStoreTermVectors(true);

        document.add(new Field("docTitle", ftModel.getTitle(), fieldType));
        document.add(new Field("docContent", ftModel.getContent(), fieldType));

        return document;
    }

}
