import org.apache.lucene.queryparser.classic.ParseException;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

public class GUI implements GUIInterface {
    JFrame frame = new JFrame("Shakespeare Search");
    JTabbedPane tabbedPane = new JTabbedPane();
    JTextField searchTerm;
    JTextField phraseSearchTerm;
    String[] tags = {"All", "Title", "Persona", "Scene Description", "Speaker", "Stage Directions", "Line", "Subtitle"};
    JComboBox<String> tagDropdown = new JComboBox<String>(tags);
    JComboBox<String> tagDropdown2 = new JComboBox<String>(tags);
    JPanel mainPanel;
    JPanel advancedPanel;
    JPanel searchPanel;
    JPanel functionPanel;
    JButton searchButton;
    JButton phraseSearchButton;
    JButton closeButton;
    ActionListener actionListener = new Interactions(this);
    String searchName;
    Search s = null;
    int slop;
    int occurences = 0;
    RecentSearches recentSearches = new RecentSearches();
    SerializingAndDeserializing sad = new SerializingAndDeserializing();

    public GUI() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(700, 700));
        tabbedPane.setPreferredSize(new Dimension(400, 400));
        slop = 0;
        checkRecentSearches();
    }
    public static void main(String args[]) {
        GUI searches = new GUI();
        searches.makeNewFrame();
        Indexer indexer = new Indexer();
        indexer.main(args);
    }

    private void checkRecentSearches() {
        recentSearches = sad.deserialzeSearches();

        if (recentSearches == null) {
            recentSearches = new RecentSearches();
        } else {
            recentSearches = sad.deserialzeSearches();
            System.out.println(recentSearches.getSearchItem(0));
        }
    }

    public void makeNewFrame() {
        makeMenuBar(frame);
        mainPanel = new JPanel();
        mainPanel.setPreferredSize(new Dimension(400, 600));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        //Initialize Search Panel
        searchPanel = new JPanel();
        searchPanel.setPreferredSize(new Dimension(100, 1));
        //Create stuff for Search Panel
        JLabel searchLabel = new JLabel("Search Term(s): ", JLabel.LEFT);
        searchTerm = new JTextField(12);
        searchPanel.add(searchLabel);
        searchPanel.add(searchTerm);
        JLabel tagLabel = new JLabel("Tag: ", JLabel.LEFT);
        searchPanel.add(tagLabel);
        searchPanel.add(tagDropdown);
        searchButton = new JButton("Search");
        searchPanel.add(searchButton);
        searchButton.addActionListener(new Interactions(this));
        //Initialize Advanced Search Panel
        advancedPanel = new JPanel();
        advancedPanel.setPreferredSize(new Dimension(300, 1));
        phraseSearchButton = new JButton("Search ");
        phraseSearchButton.addActionListener(new Interactions(this));
        JLabel advancedSearchLabel = new JLabel("Phrase Search: ", JLabel.RIGHT);
        JLabel tagLabel2 = new JLabel("Tag: ", JLabel.LEFT);
        phraseSearchTerm = new JTextField(12);
        advancedPanel.add(advancedSearchLabel);
        advancedPanel.add(phraseSearchTerm);
        advancedPanel.add(tagLabel2);
        advancedPanel.add(tagDropdown2);
        advancedPanel.add(phraseSearchButton);
        //Initialize Function Panel
        functionPanel = new JPanel();
        closeButton = new JButton("Close");
        functionPanel.add(closeButton);
        closeButton.addActionListener(new Interactions(this));
        //Initialize mainPanel
        mainPanel.add(searchPanel);
        mainPanel.add(advancedPanel);
        mainPanel.add(tabbedPane);
        mainPanel.add(functionPanel);
        frame.add(mainPanel);
        frame.setVisible(true);
        frame.pack();
    }

    public void createTab(List<Result> searches, List<String> suggestions, String searchField) {
        JPanel displayPane = new JPanel();
        displayPane.setBackground(Color.white);
        JTextPane total = new JTextPane();
        total.setText("We found the following plays which matched your query: ");
        total.setEditable(false);
        displayPane.add(total);
        JTextPane empty = new JTextPane();
        empty.setText("Total Results: " + searches.size() + "\n");
        empty.setEditable(false);
        displayPane.add(empty);
        displayPane.setLayout(new FlowLayout());
        displayPane.setLayout(new GridLayout(searches.size() + 3, 1));
        JScrollPane scrollPane = new JScrollPane(displayPane);
        for (Result r : searches) {
            //JTextPane result = new JTextPane();
            //result.setText(r.toString());
            //displayPane.add(result);

            JButton resultButton = new JButton(new AbstractAction(r.getTitle()) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    File file = new File(r.getPath());
                    int counter = 0;
                    //Desktop.getDesktop().open(file);
                    JFrame f = new JFrame(r.getTitle());
                    //JPanel panel = new JPanel();
                    JTextArea textArea = new JTextArea();
                    textArea.setText(r.getTitle());
                    textArea.setText(XMLRetrieval.toPrettyString(r.getPath(), 4));
                    Highlighter highlighter = textArea.getHighlighter();
                    Highlighter.HighlightPainter painter =
                            new DefaultHighlighter.DefaultHighlightPainter(Color.pink);
                    String text = XMLRetrieval.toPrettyString(r.getPath(), 4);
                    String searchT = searchTerm.getText();
                    int scrollIndex = 0;
                    String[] lines = text.split(System.getProperty("line.separator"));


                    for (int i = 0; i < lines.length; i++) {
                        if (lines[i].toLowerCase().contains(searchT.toLowerCase())) {
                            int p0 = text.indexOf(lines[i]);
                            int p1 = p0 + lines[i].length();
                            System.out.println(lines[i]);
                            scrollIndex = text.indexOf(lines[i]);
                            counter = counter + 1;
                            try {
                                highlighter.addHighlight(p0, p1, painter);
                            } catch (BadLocationException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }

                        }
                    }

                    occurences = counter;
                    System.out.println(counter + " occurences");

                    textArea.append(" \n [" + counter + " occurences]");
                    textArea.setEditable(false);
                    JScrollPane scrollPane = new JScrollPane(textArea);
                    textArea.setCaretPosition(scrollIndex);
                    f.add(scrollPane);
                    f.setVisible(true);
                    f.setSize(700, 700);
                }
            });

            displayPane.add(resultButton);

        }
        JButton suggestButton = new JButton(new AbstractAction("Is this what you expected? \n " +
                "CLICK here to see suggested searches.") {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSuggestions(suggestions, searchField);
            }
        });
        suggestButton.setBorderPainted(false);

       /* JButton suggestionsLabel = new JButton("Is this what you expected? \n " +
                "Perhaps we/you made an error click here to see similar words");
        suggestionsLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showSuggestions(suggestions, searchField);
            }
        });*/

        displayPane.add(suggestButton);

        tabbedPane.addTab(searchName, scrollPane);
        int i = 0;
        for (i = 0; i < tabbedPane.getTabCount(); i++)
            tabbedPane.setSelectedIndex(i);
    }

    private void showSuggestions(List<String> suggestions, String searchField) {
        String[] suggestionArray = (String[]) suggestions.toArray();

        String input = (String) JOptionPane.showInputDialog(null, "Pick a suggestion if you wish: ", "Possible Suggestions ", JOptionPane.QUESTION_MESSAGE, null, // Use
                // default
                // icon
                suggestionArray, // Array of choices
                suggestionArray[0]); // Initial choice

        try {
            if (!input.equals(suggestionArray[0])) {
                doSuggestionSearch(input, searchField);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void makeMenuBar(JFrame frame) {
        JMenuBar menubar = new JMenuBar();
        frame.setJMenuBar(menubar);

        JMenu file = new JMenu("Menu");
        menubar.add(file);

        JMenuItem settings = new JMenuItem("Settings");
        settings.addActionListener(new Interactions(this));
        KeyStroke cmdS = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK);
        settings.setAccelerator(cmdS);
        file.add(settings);

        JMenuItem loadDictionary = new JMenuItem("Load Dictionary");
        loadDictionary.addActionListener(new Interactions(this));
        KeyStroke cmdL = KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_DOWN_MASK);
        loadDictionary.setAccelerator(cmdL);
        file.add(loadDictionary);

        JMenuItem searchHistory = new JMenuItem("Recent Searches");
        searchHistory.addActionListener(new Interactions(this));
        KeyStroke cmdR = KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK);
        searchHistory.setAccelerator(cmdR);
        file.add(searchHistory);

        JMenuItem definition = new JMenuItem("Shakespearean Dictionary");
        definition.addActionListener(new Interactions(this));
        KeyStroke cmdD = KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK);
        definition.setAccelerator(cmdD);
        file.add(definition);

        JMenuItem help = new JMenuItem("Help");
        help.addActionListener(new Interactions(this));
        KeyStroke cmdH = KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_DOWN_MASK);
        help.setAccelerator(cmdH);
        file.add(help);

        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(new Interactions(this));
        KeyStroke cmdQ = KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK);
        exit.setAccelerator(cmdQ);
        file.add(exit);
    }

    public void settingsMenuOption() {
        Integer[] choices = {0, 1, 2, 3};
        int input = (int) JOptionPane.showInputDialog(null, "Set Slop (Phrase Search Accuracy): ", "Set Slop (Phrase Search Accuracy) ", JOptionPane.QUESTION_MESSAGE, null, // Use
                // default
                // icon
                choices, // Array of choices
                choices[slop]); // Initial choice
        slop = input;
    }

    public void helpMenuOption() {
        JOptionPane.showMessageDialog(frame, "Normal Search:\n"
                        + "1. Enter search terms using OR, AND or NOT separators.\n"
                        + "2. Choose which parts of the plays you'd like to search using the dropdown menu.\n"
                        + "3. Search\n\n"
                        + "Phrase Search:\n"
                        + "1. Enter search phrase e.g. Lady MacBeth\n"
                        + "2. Choose which parts of the plays you'd like to search using the dropdown menu.\n"
                        + "3. Search\n\n"
                        + "Search Suggestions:\n"
                        + "At the bottom of each search tab there will be a message asking if the results were as expected.\n"
                        + "Click this message to see related words and search using the new term.\n\n"
                        + "Settings:\n"
                        + "This allows you to set the application's slop value. Slop alters how accurate the phrase search is.\n\n"
                        + "Load Dictionary:\n"
                        + "Here you can reload the dictionary to make sure you have the most uptodate Shakespearean Dictionary.\n\n"
                        + "Recent Searches:\n"
                        + "This shows your last 12 searches.\n"
                        + "Select one from the drop down menu and hit okay to make the search again.\n\n"
                        + "Shakespearean Dictionary:\n"
                        + "Learn more about Shakespearean terms.\n"
                , "Help", JOptionPane.PLAIN_MESSAGE);
    }

    public void exitMenuOption() {
        UIManager.put("OptionPane.yesButtonText", "Yes");
        UIManager.put("OptionPane.noButtonText", "No");
        int reply = JOptionPane.showConfirmDialog(null, "Are you Sure?", "Quit", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (reply == JOptionPane.YES_OPTION) {
            System.exit(0);
        } else {
            return;
        }
    }

    public boolean addErrorChecking() {
        if ((searchTerm.getText().isEmpty())) {
            JOptionPane.showMessageDialog(frame, "Cannot add empty fields!");
            return false;
        } else
            return true;
    }

    public void searchButton() {
        String defaultTabName = "NS: " + "\"" + searchTerm.getText() + "\" in " + tagDropdown.getSelectedItem().toString();
        String tag = tagDropdown.getSelectedItem().toString().toLowerCase();
        if (tag.equalsIgnoreCase("Scene Description"))
            tag = "sceneDesc";
        else if (tag.equalsIgnoreCase("Stage Directions"))
            tag = "stageDir";
        else if (tag.equalsIgnoreCase("All"))
            tag = "contents";
        searchName = (String) JOptionPane.showInputDialog(frame, "Search Name: ", "New Search", JOptionPane.PLAIN_MESSAGE, null, null, defaultTabName);
        if (searchName != null) {
            try {
                s = new Search();
                List<Result> resultList = s.doSearch(searchTerm.getText(), tag);
                createTab(resultList, s.getSuggestions(), s.getSearchField());
                frame.pack();
                recentSearches.addSearchItem(searchTerm.getText(), s.getSearchField(), s.getIsNormalSearch());
                sad.serializeSearches(recentSearches);
            } catch (Exception e) {
                e.printStackTrace();
            }
            searchName = null;
        }
    }

    public void phraseSearchButton() {
        String defaultTabName = "PS: " + "\"" + phraseSearchTerm.getText() + "\" in " + tagDropdown2.getSelectedItem().toString();
        String tag = tagDropdown2.getSelectedItem().toString().toLowerCase();
        if (tag.equalsIgnoreCase("Scene Description"))
            tag = "sceneDesc";
        else if (tag.equalsIgnoreCase("Stage Directions"))
            tag = "stageDir";
        else if (tag.equalsIgnoreCase("All"))
            tag = "contents";
        searchName = (String) JOptionPane.showInputDialog(frame, "Search Name: ", "New Search", JOptionPane.PLAIN_MESSAGE, null, null, defaultTabName);
        if (searchName != null) {
            try {
                s = new Search();
                //search.doPhraseSearch(phraseSearchTerm.getText(), tag, slop);
                List<Result> resultList = s.doPhraseSearch2(phraseSearchTerm.getText(), tag, slop);
                createTab(resultList, s.getSuggestions(), s.getSearchField());
                frame.pack();
            } catch (Exception e) {
                e.printStackTrace();
            }
            searchName = null;
        }
    }

    public void closeButton() {
        Component selected = tabbedPane.getSelectedComponent();
        if (selected != null) {
            tabbedPane.remove(selected);
        }
        if (tabbedPane.getTabCount() == 0) {
            searchTerm.setText("");
            phraseSearchTerm.setText("");
        }
    }

    @Override
    public void loadDictionaryMenuOption() {
        LoadDictionary.loadDictionary();
    }


    public void showDefinitions() {
        ShakespeareDefinitionViewer.view();
    }


    public void showSearchHistory() {
        Map<String, String> searches = new LinkedHashMap<>();
        List<String> searchesList = new ArrayList<>();

        for (int i = 0; i < recentSearches.size(); i++) {
            searches.put(recentSearches.getSearchTerm(i), recentSearches.getSearchTag(i));
            searchesList.add("Term: " + recentSearches.getSearchTerm(i) + " Tag: " + recentSearches.getSearchTag(i));
        }

        String[] searchesArray = searchesList.toArray(new String[searchesList.size()]);
        String input = (String) JOptionPane.showInputDialog(null, "Recent Searches: ", "Recent Searches ", JOptionPane.QUESTION_MESSAGE, null, // Use
                // default
                // icon
                searchesArray, // Array of choices
                searchesArray[0]); // Initial choice

        try {
            int i = searchesList.indexOf(input);
            searches.values().toArray();
            doSuggestionSearch((String) searches.keySet().toArray()[i], (String) searches.values().toArray()[i]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void doSuggestionSearch(String input, String searchField) throws Exception {
        Search search = new Search();
        List<Result> resultList;
        String defaultTabName = "NS: " + "\"" + input + "\" in " + searchField;
        System.out.println("SEARCH THIS: " + input);
        resultList = search.doSearch(input, searchField);
        searchName = (String) JOptionPane.showInputDialog(frame, "Search Name: ", "New Search", JOptionPane.PLAIN_MESSAGE, null, null, defaultTabName);

        if (searchName != null) {
            createTab(search.doSearch(input, searchField), search.getSuggestions(), input);
            frame.pack();
            searchName = null;
        }
    }
}