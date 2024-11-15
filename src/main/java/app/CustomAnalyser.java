package app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.FlattenGraphFilter;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.en.KStemFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.TrimFilter;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class CustomAnalyser extends Analyzer {

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        StandardTokenizer baseTokenizer = new StandardTokenizer();
        TokenStream tokenStream = new LowerCaseFilter(baseTokenizer);

        tokenStream = applyFilters(tokenStream);

        return new TokenStreamComponents(baseTokenizer, tokenStream);
    }

    private TokenStream applyFilters(TokenStream tokenStream) {
        tokenStream = new FlattenGraphFilter(new WordDelimiterGraphFilter(tokenStream,
                WordDelimiterGraphFilter.SPLIT_ON_NUMERICS | WordDelimiterGraphFilter.GENERATE_WORD_PARTS
                        | WordDelimiterGraphFilter.GENERATE_NUMBER_PARTS | WordDelimiterGraphFilter.PRESERVE_ORIGINAL,
                null));
        tokenStream = new TrimFilter(tokenStream);
        tokenStream = new PorterStemFilter(tokenStream);
        tokenStream = new EnglishPossessiveFilter(tokenStream);
        tokenStream = new KStemFilter(tokenStream);
        tokenStream = new StopFilter(tokenStream, loadStopWords());
        tokenStream = new SnowballFilter(tokenStream, "English");

        return tokenStream;
    }

    private CharArraySet loadStopWords() {
        List<String> stopWords = Arrays.asList(loadFileContent(Constant.STOPWORD_FILE).toLowerCase().split("\n"));
        return new CharArraySet(stopWords, true);
    }

    private String loadFileContent(String path) {
        StringBuilder content = new StringBuilder();
        try {
            content.append(new String(Files.readAllBytes(Paths.get(path))));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return content.toString();
    }
}
