package app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.FlattenGraphFilter;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.en.KStemFilter;
import org.apache.lucene.analysis.miscellaneous.TrimFilter;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class CustomAnalyser extends Analyzer {

    private static final CharArraySet STOP_WORDS = loadStopWords();

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        StandardTokenizer tokenizer = new StandardTokenizer();
        TokenStream tokenStream = applyFilters(tokenizer);

        return new TokenStreamComponents(tokenizer, tokenStream);
    }

    private TokenStream applyFilters(TokenStream tokenStream) {
        tokenStream = new LowerCaseFilter(tokenStream);
        tokenStream = new FlattenGraphFilter(new WordDelimiterGraphFilter(tokenStream,
                WordDelimiterGraphFilter.SPLIT_ON_NUMERICS | 
                WordDelimiterGraphFilter.GENERATE_WORD_PARTS | 
                WordDelimiterGraphFilter.GENERATE_NUMBER_PARTS | 
                WordDelimiterGraphFilter.PRESERVE_ORIGINAL |
                WordDelimiterGraphFilter.CATENATE_WORDS,
                null));
        tokenStream = new TrimFilter(tokenStream);
        tokenStream = new EnglishPossessiveFilter(tokenStream);
        tokenStream = new KStemFilter(tokenStream);
        tokenStream = new StopFilter(tokenStream, STOP_WORDS);

        return tokenStream;
    }

    private static CharArraySet loadStopWords() {
        try {
            String stopWordsContent = Files.readString(Paths.get(Constant.STOPWORD_FILE)).toLowerCase();
            List<String> stopWords = Arrays.stream(stopWordsContent.split("\\R")) // Split by line endings
                                           .filter(word -> !word.isBlank()) // Remove empty lines
                                           .collect(Collectors.toList());
            return new CharArraySet(stopWords, true);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load stop words from file: " + Constant.STOPWORD_FILE, ex);
        }
    }
}