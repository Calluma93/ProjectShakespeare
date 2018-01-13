import org.apache.lucene.document.Document;

/**
 * Created by Umar on 25/10/2016.
 */
public class Result {

    private String path;
    private String title;
    private Document doc;

    public Result(String path, Document doc, String title) {
        this.path = path;
        this.doc = doc;
        this.title = title;
    }


    public Document getDoc() {
        return doc;
    }

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }

    public String toString() {
        return "Title: " + title + ". Path: " + path;
    }
}
