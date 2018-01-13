import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.Dictionary;
import org.apache.lucene.search.spell.PlainTextDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by Umar on 18/11/2016.
 */
public class LoadDictionary {
    static SpellChecker spellChecker;

    public LoadDictionary() throws IOException {
    }

    public static void loadDictionary() {
        try {
            Directory directory = FSDirectory.open(Paths.get("spellingIndex/"));
            spellChecker = new SpellChecker(directory);
            Dictionary dictionary;
            dictionary = new PlainTextDictionary(Paths.get("Dictionary/shakespeareDictionary.txt", new String[0]));
            IndexWriterConfig config = new IndexWriterConfig();
            spellChecker.indexDictionary(dictionary, config, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("DONE LOADING DICTIONARY");
    }


}
