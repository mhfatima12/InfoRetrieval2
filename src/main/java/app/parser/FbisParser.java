package app.parser;

import app.model.childModel.FbisModel;
import org.apache.lucene.document.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.lucene.index.IndexWriter;

public class FbisParser {

    private static final List<String> tagList = List.of(
            "TI", "HT", "PHRASE", "DATE1", "ABS", "FIG",
            "F", "F P=100", "F P=101", "F P=102", "F P=103", "F P=104", "F P=105", "F P=106", "F P=107",
            "TI", "H1", "H2", "H3", "H4", "H5", "H6", "H7", "H8", "TR", "TXT5", "HEADER", "TEXT", "AU");

    /**
     * Parses FBI documents and directly indexes them without storing them in memory.
     * @param path The directory containing the FBI XML files.
     * @param writer The IndexWriter to index documents.
     * @throws IOException if an I/O error occurs.
     */
    public void parseAndIndexFbis(String path, IndexWriter writer) throws IOException {
        System.out.println("Parsing and indexing FBI documents from path: " + path);

        // Get all files in the directory
        File[] files = new File(path).listFiles();
        if (files == null) {
            throw new IOException("Invalid directory path or no files found in: " + path);
        }

        // Process each file and parse documents directly
        for (File file : files) {
            if (file.isFile()) {
                org.jsoup.nodes.Document document = Jsoup.parse(file, "UTF-8", "");
                Elements elements = document.select("DOC");

                // Process each element in the document
                for (Element element : elements) {
                    FbisModel fbisModel = parseFbisModel(element);
                    Document luceneDoc = createLuceneDocument(fbisModel);

                    // Index the Lucene document directly after parsing
                    writer.addDocument(luceneDoc);
                }
            }
        }

        System.out.println("Indexing process completed.");
    }

    /**
     * Parses an FbisModel from the document element.
     * @param element The document element containing the information.
     * @return The parsed FbisModel.
     */
    private FbisModel parseFbisModel(Element element) {
        FbisModel fbisModel = new FbisModel();
        fbisModel.setDocNo(element.select("DOCNO").text());
        fbisModel.setContent(removeNonsense(element.select("TEXT").text()));
        fbisModel.setTitle(extractTitle(element));
        return fbisModel;
    }

    /**
     * Extracts the title from the relevant header tags (H3-H8).
     * @param element The document element containing the headers.
     * @return The extracted title.
     */
    private String extractTitle(Element element) {
        StringBuilder titleBuilder = new StringBuilder();
        for (int i = 3; i <= 8; i++) {
            String cssQuery = "H" + i;
            String headerText = element.select(cssQuery).text();
            if (!headerText.isEmpty()) {
                titleBuilder.append(" ").append(headerText);
            }
        }
        return titleBuilder.toString().trim();
    }

    /**
     * Creates a Lucene document from the FbisModel.
     * @param fbisModel The FbisModel containing the document data.
     * @return The corresponding Lucene Document.
     */
    private Document createLuceneDocument(FbisModel fbisModel) {
        Document document = new Document();
        document.add(new StringField("docNumber", fbisModel.getDocNo(), Field.Store.YES));

        // Use a FieldType with term vectors for storing text and its analysis
        FieldType fieldType = new FieldType(TextField.TYPE_STORED);
        fieldType.setStoreTermVectors(true);

        document.add(new Field("docTitle", removeNonsense(fbisModel.getTitle()), fieldType));
        document.add(new Field("docContent", removeNonsense(fbisModel.getContent()), fieldType));

        return document;
    }

    /**
     * Removes unnecessary or nonsense characters from the text.
     * @param data The text data to be cleaned.
     * @return The cleaned text.
     */
    private String removeNonsense(String data) {
        if (data.contains("\n")) {
            data = data.replaceAll("\n", " ").trim();
        }
        if (data.contains("[")) {
            data = data.replaceAll("\\[", "").trim();
        }
        if (data.contains("]")) {
            data = data.replaceAll("]", "").trim();
        }

        // Remove all the tags from the list
        for (String tag : tagList) {
            data = data.replaceAll("<" + tag + ">", "");
            data = data.replaceAll("<" + tag + "/>", "");
        }

        return data;
    }

   
}
