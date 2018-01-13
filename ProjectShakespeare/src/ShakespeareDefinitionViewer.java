import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Created by Umar on 29/11/2016.
 */
public class ShakespeareDefinitionViewer {


    public static void view() {
        JEditorPane jep = new JEditorPane();
        jep.setEditable(false);

        try {
            jep.setPage("http://learn.lexiconic.net/shakewords.htm#");
        } catch (IOException e) {
            jep.setContentType("text/html");
            jep.setText("<html>Could not load</html>");
        }

        JScrollPane scrollPane = new JScrollPane(jep);
        JFrame f = new JFrame("Shakespeare Definitions");
        f.getContentPane().add(scrollPane);
        f.setSize(800, 400);
        f.setLocation(200, 200);
        f.setVisible(true);
    }
}
