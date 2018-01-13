import java.util.List;
import javax.swing.JFrame;
public interface GUIInterface {
    public void createTab(List<Result> searches, List<String> suggestions, String searchField);
    public void makeMenuBar(JFrame frame);

    public void settingsMenuOption();
    public void helpMenuOption();
    public void exitMenuOption();
    public void searchButton();

    public void phraseSearchButton();
    public void closeButton();

    public void loadDictionaryMenuOption();

    public void showDefinitions();

    public void showSearchHistory();
}