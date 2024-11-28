package app.parser;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;

public class Fr94Parser {

   
    public void parseAndIndexFR94(String path, IndexWriter writer) throws IOException {
        System.out.println("Parsing and indexing FR94 documents from path: " + path);

       
        File[] files = new File(path).listFiles();
        if (files == null) {
            throw new IOException("Invalid directory path or no files found in: " + path);
        }

        
        for (File file : files) {
            if (file.isFile()) {
                org.jsoup.nodes.Document document = Jsoup.parse(file, "UTF-8", "");
                Elements elements = document.select("DOC");

                for (Element element : elements) {
                    String docNo = element.select("DOCNO").text().trim();
                    String content = element.select("TEXT").text().trim();
                    String headline = element.select("HEADLINE").text().trim();

                    if (!docNo.isEmpty() && !content.isEmpty()) {
                        
                        Document luceneDoc = new Document();
                        luceneDoc.add(new StringField("docNumber", docNo, Field.Store.YES));
                        luceneDoc.add(new TextField("docContent", content, Field.Store.YES));
                        luceneDoc.add(new TextField("docTitle", headline, Field.Store.YES));

                        writer.addDocument(luceneDoc); 
                    }
                }
            }
        }

        System.out.println("Indexing process for fr94 completed.");
    }
}
