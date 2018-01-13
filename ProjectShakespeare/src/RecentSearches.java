import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Umar on 29/11/2016.
 */
public class RecentSearches implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> searchStrings, tags;
    private List<Boolean> types;

    public RecentSearches() {
        searchStrings = new ArrayList<>();
        tags = new ArrayList<>();
        types = new ArrayList<>();
    }

    public void addSearchItem(String searchTerm, String searchTag, boolean searchType) {
        if (searchStrings.size() > 12) {
            searchStrings.remove(0);
            tags.remove(0);
            types.remove(0);
        }
        searchStrings.add(searchTerm);
        tags.add(searchTag);
        types.add(searchType);
    }

    public String getSearchTag(int i) {
        return tags.get(i);
    }

    public String getSearchTerm(int i) {
        return searchStrings.get(i);
    }

    public String getSearchItem(int i) {
        return "YOU: " + searchStrings.get(i);
    }

    public int size() {
        return searchStrings.size();
    }


}
