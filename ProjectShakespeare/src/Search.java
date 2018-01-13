import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Umar on 25/10/2016.
 */
public class Search {
    private IndexSearcher searcher;
    String index = "Index";
    private List<String> suggestions;
    private String searchField;
    private Boolean isNormalSearch;

    public Search() throws IOException {
        DirectoryReader dir = DirectoryReader.open(FSDirectory.open(Paths.get(index, new String[0])));
        searcher = new IndexSearcher(dir);
    }

    private Query buildSearchQuery(String searchTerm, String field) throws ParseException {
        searchTerm.toLowerCase();
        String searchTempTerm = searchTerm.replaceAll(" and ", " AND ");
        searchTerm = searchTempTerm;
        searchTempTerm = searchTerm.replaceAll(" or ", " OR ");
        searchTerm = searchTempTerm;
        searchTempTerm = searchTerm.replaceAll(" not ", " NOT ");
        searchTerm = searchTempTerm;
        searchTempTerm = searchTerm.replaceAll(" has ", " HAS ");
        searchTerm = searchTempTerm;

        Analyzer analyzer = new StandardAnalyzer(StandardAnalyzer.ENGLISH_STOP_WORDS_SET);
        QueryParser parserContent = new QueryParser("contents", analyzer);
        QueryParser parserTitle = new QueryParser("title", analyzer);
        QueryParser parserPersona = new QueryParser("persona", analyzer);
        QueryParser parserSceneDesc = new QueryParser("sceneDesc", analyzer);
        QueryParser parserSpeaker = new QueryParser("speaker", analyzer);
        QueryParser parserStageDir = new QueryParser("stageDir", analyzer);
        QueryParser parserLine = new QueryParser("line", analyzer);
        QueryParser parserSubtitle = new QueryParser("subtitle", analyzer);

        BooleanQuery.Builder builder = new BooleanQuery.Builder();

        switch (field) {
            case ("title"):
                builder.add(parserTitle.parse(searchTerm), BooleanClause.Occur.SHOULD);
                break;
            case ("contents"):
                builder.add(parserContent.parse(searchTerm), BooleanClause.Occur.SHOULD);
                break;
            case ("persona"):
                builder.add(parserPersona.parse(searchTerm), BooleanClause.Occur.SHOULD);
                break;
            case ("sceneDesc"):
                builder.add(parserSceneDesc.parse(searchTerm), BooleanClause.Occur.SHOULD);
                break;
            case ("speaker"):
                builder.add(parserSpeaker.parse(searchTerm), BooleanClause.Occur.SHOULD);
                break;
            case ("stageDir"):
                builder.add(parserStageDir.parse(searchTerm), BooleanClause.Occur.SHOULD);
                break;
            case ("line"):
                builder.add(parserLine.parse(searchTerm), BooleanClause.Occur.SHOULD);
                break;
            case ("subtitle"):
                builder.add(parserSubtitle.parse(searchTerm), BooleanClause.Occur.SHOULD);
                break;
        }

        return builder.build();
    }

    public List<Result> doSearch(String searchTerm, String field) throws Exception {
        searchField = field;
        isNormalSearch = true;
        suggestions = SpellCheckers.getSuggestions(searchTerm);
        List<Result> resultList = new ArrayList<>();
        Query query = buildSearchQuery(searchTerm, field);
        System.out.println(query.toString());

        TopDocs results = searcher.search(query, Integer.MAX_VALUE);
        ScoreDoc[] hits = results.scoreDocs;
        int numTotalHits = results.totalHits;
        System.out.println("Number of hits: " + numTotalHits);
        for (ScoreDoc scoreDoc : hits) {
            Document doc = searcher.doc(scoreDoc.doc);
            //String path = doc.get("path");
            resultList.add(getResultItem(doc));
        }
        System.out.println(resultList);
        return resultList;
    }

    public List<Result> doPhraseSearch2(String searchTerm, String field, int slop) throws ParseException, IOException {
        searchField = field;
        isNormalSearch = false;

        searchTerm = searchTerm.toLowerCase();

        List<Result> resultList = new ArrayList<>();

        PhraseQuery.Builder builder = new PhraseQuery.Builder();
        MultiPhraseQuery.Builder builder1 = new MultiPhraseQuery.Builder();
        builder1.setSlop(slop);
        String[] words = searchTerm.split(" ");
        //System.out.println(words[1]);
        for (String word : words) {
            System.out.println(word);
            builder.add(new Term("contents", word));
            builder1.add(new Term(field, word));
        }
        MultiPhraseQuery query = builder1.build();
        //PhraseQuery query = builder.build();
        System.out.println(query);

        TopDocs topDocs = searcher.search(query, Integer.MAX_VALUE);
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            //String path = doc.get("path");
            System.out.println(doc.get("title"));
            resultList.add(getResultItem(doc));
        }
        return resultList;

    }

    private Result getResultItem(Document doc) {
        String path = doc.get("path");
        if (path != null) {
            System.out.println("Path: " + path);
            String title = doc.get("title");
            if (title != null) {
                System.out.println("Title: " + title);
                return (new Result(path, doc, title));
            }
        }
        return null;
    }

    public List<String> getSuggestions() throws Exception {
        return suggestions;
    }

    public String getSearchField() {
        return searchField;
    }

    public boolean getIsNormalSearch() {
        return isNormalSearch;
    }



}
