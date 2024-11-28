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

    public void parseAndIndexFbis(String path, IndexWriter writer) throws IOException {
        System.out.println("Parsing and indexing FBI documents from path: " + path);

        
        File[] files = new File(path).listFiles();
        if (files == null) {
            throw new IOException("Invalid directory path or no files found in: " + path);
        }

        
        for (File file : files) {
            if (file.isFile()) {
                org.jsoup.nodes.Document document = Jsoup.parse(file, "UTF-8", "");
                Elements elements = document.select("DOC");

                
                for (Element element : elements) {
                    FbisModel fbisModel = parseFbisModel(element);
                    Document luceneDoc = createLuceneDocument(fbisModel);

                
                    writer.addDocument(luceneDoc);
                }
            }
        }

        System.out.println("Indexing process completed.");
    }

    
    private FbisModel parseFbisModel(Element element) {
        FbisModel fbisModel = new FbisModel();
        fbisModel.setDocNo(element.select("DOCNO").text());
        fbisModel.setContent(removeNonsense(element.select("TEXT").text()));
        fbisModel.setTitle(extractTitle(element));
        return fbisModel;
    }

   
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

    
    private Document createLuceneDocument(FbisModel fbisModel) {
        Document document = new Document();
        document.add(new StringField("docNumber", fbisModel.getDocNo(), Field.Store.YES));

        
        FieldType fieldType = new FieldType(TextField.TYPE_STORED);
        fieldType.setStoreTermVectors(true);

        document.add(new Field("docTitle", removeNonsense(fbisModel.getTitle()), fieldType));
        document.add(new Field("docContent", removeNonsense(fbisModel.getContent()), fieldType));

        return document;
    }


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

       
        for (String tag : tagList) {
            data = data.replaceAll("<" + tag + ">", "");
            data = data.replaceAll("<" + tag + "/>", "");
        }

        return data;
    }

   
}
