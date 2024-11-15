package app.parser;

import org.apache.lucene.document.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.model.childModel.FTModel;

public class FTParser {

//    private final static File FT_DIR = new File("docs/ft");
    private static ArrayList<FTModel> ftDocsList = new ArrayList<>();
    private List<Document> ftDoc = new ArrayList<>();

    private List<Document> doclist;

    public List<Document> parseFTDocs(String path) throws IOException {
        doclist = new ArrayList<>();
        System.out.println("Parsing FT documents...");
        File[] directories = new File(path).listFiles(File::isDirectory);

        for(File directory : directories){
            File[] files = directory.listFiles();
            for(File file : files) {
                org.jsoup.nodes.Document doc = Jsoup.parse(file, null, "");
                Elements elements = doc.select("DOC");
                for(Element element : elements){
                    FTModel ftdoc = new FTModel();

                    String headlineText = element.select("HEADLINE").text();
//                    String[] arrOfStr = str.split("/", 2);
//                    ftdoc.setDate(arrOfStr[0].split(" ", 2)[1]);
                    ftdoc.setTitle(headlineText);
                    ftdoc.setDocNo(element.select("DOCNO").text());
                    ftdoc.setContent(element.select("TEXT").text());

                    ftDocsList.add(ftdoc);
                }
            }
        }
        System.out.println("Parsing FT done...");
        return returnParsedDocuments();
    }

    public List<Document> returnParsedDocuments() throws IOException{

        for (FTModel ftModel : ftDocsList) {
            Document document = new Document();
            document.add(new StringField("docNumber", ftModel.getDocNo(), Field.Store.YES));
            FieldType fieldType = new FieldType(TextField.TYPE_STORED);
            fieldType.setStoreTermVectors(true);
//            document.add(new TextField("docTitle", ftModel.getTitle(), Field.Store.YES));
//            document.add(new TextField("docContent", ftModel.getContent(), Field.Store.YES));
            document.add(new Field("docTitle", ftModel.getTitle(), fieldType));
            document.add(new Field("docContent", ftModel.getContent(), fieldType));
            ftDoc.add(document);
        }

        return ftDoc;
    }

    public static void main(String[] args) throws IOException {
        new FTParser().parseFTDocs("docs/ft");
    }
}