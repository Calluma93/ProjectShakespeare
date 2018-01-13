import java.io.IOException;

/**
 * Created by Umar on 25/10/2016.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        SpellCheckers.getSuggestions("ok");

        Search s = null;
        try {
            s = new Search();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {

            int slop = 0;
            s.doPhraseSearch2("hath so much", "line", 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
