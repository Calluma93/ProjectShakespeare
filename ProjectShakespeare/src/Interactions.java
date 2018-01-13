import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
public class Interactions implements ActionListener {
    GUIInterface gui;

    public Interactions(GUIInterface g) {
        gui = g;
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getActionCommand() == "Settings") {
            gui.settingsMenuOption();
        }
        if (e.getActionCommand() == "Load Dictionary") {
            gui.loadDictionaryMenuOption();
        }
        if (e.getActionCommand() == "Help") {
            gui.helpMenuOption();
        }
        if (e.getActionCommand() == "Exit") {
            gui.exitMenuOption();
        }
        if (e.getActionCommand() == "Search") {
            gui.searchButton();
        }
        if (e.getActionCommand() == "Search ") {
            gui.phraseSearchButton();
        }
        if (e.getActionCommand() == "Close") {
            gui.closeButton();
        }
        if (e.getActionCommand() == "Shakespearean Dictionary") {
            gui.showDefinitions();
        }
        if (e.getActionCommand() == "Recent Searches") {
            gui.showSearchHistory();
        }
    }
}