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

    
    public void parseAndIndexFTDocs(String path, IndexWriter writer) throws IOException {
        System.out.println("Parsing and indexing FT documents from path: " + path);
        
        File[] directories = new File(path).listFiles(File::isDirectory);
        
        for (File directory : directories) {
            File[] files = directory.listFiles();
            for (File file : files) {
               
                org.jsoup.nodes.Document doc = Jsoup.parse(file, null, "");
                Elements elements = doc.select("DOC");

            
                for (Element element : elements) {
                    FTModel ftDoc = parseFTDocument(element);
                    Document luceneDoc = createLuceneDocument(ftDoc);
                    
                    
                    writer.addDocument(luceneDoc);
                }
            }
        }
        
        System.out.println("Indexing process completed.");
    }


    private FTModel parseFTDocument(Element element) {
        FTModel ftDoc = new FTModel();
        ftDoc.setTitle(element.select("HEADLINE").text());
        ftDoc.setDocNo(element.select("DOCNO").text());
        ftDoc.setContent(element.select("TEXT").text());
        return ftDoc;
    }

    
    private Document createLuceneDocument(FTModel ftModel) {
        Document document = new Document();
        document.add(new StringField("docNumber", ftModel.getDocNo(), Field.Store.YES));

       
        FieldType fieldType = new FieldType(TextField.TYPE_STORED);
        fieldType.setStoreTermVectors(true);

        document.add(new Field("docTitle", ftModel.getTitle(), fieldType));
        document.add(new Field("docContent", ftModel.getContent(), fieldType));

        return document;
    }

}
