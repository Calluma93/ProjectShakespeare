
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.Dictionary;
import org.apache.lucene.search.spell.PlainTextDictionary;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class SpellCheckers {

    public static List<String> getSuggestions(String searchTerm) throws Exception {

        Directory directory = FSDirectory.open(Paths.get("spellingIndex/"));

        org.apache.lucene.search.spell.SpellChecker spellChecker = new org.apache.lucene.search.spell.SpellChecker(directory);

        Dictionary dictionary = new PlainTextDictionary(Paths.get("Dictionary/shakespeareDictionary.txt", new String[0]));

        IndexWriterConfig config = new IndexWriterConfig();

        spellChecker.indexDictionary(dictionary, config, false);

        String wordForSuggestions = searchTerm.split(" ")[0];

        int suggestionsNumber = 5;

        List<String> suggestionList = Arrays.asList(spellChecker.suggestSimilar(wordForSuggestions, suggestionsNumber));

        if (suggestionList != null && suggestionList.size() > 0) {
            for (String word : suggestionList) {
                System.out.println("Did you mean:" + word);
            }
        } else {
            System.out.println("No suggestions found for word:" + wordForSuggestions);
        }
        return suggestionList;
    }

}