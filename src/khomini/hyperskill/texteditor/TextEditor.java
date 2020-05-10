package khomini.hyperskill.texteditor;

import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class TextEditor extends JFrame {

    private String text;
    private final FileChooser fileChooser;
    JTextArea textArea;
    JTextField searchField;
    private int currentIndex;
    private boolean isCheckedRegexp;
    private JCheckBox regExpCheckBox;
    private JCheckBoxMenuItem useRegExpMenuItem;
    ArrayList<Pair<Integer, Integer>> indexes;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public TextEditor() {
        this.indexes = new ArrayList<>();
        this.currentIndex = 0;
        this.isCheckedRegexp = false;
        this.fileChooser = new FileChooser(".");
        this.initTextEditor();
    }

    private void initTextEditor() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Text Editor");
        setSize(570, 440);
        setLocationRelativeTo(null);
        add(createPanel(), BorderLayout.NORTH);
        createMenu();
        add(this.fileChooser);
        add(createScrollPane());
        setVisible(true);
    }

    private JScrollPane createScrollPane() {
        textArea = new JTextArea();
        textArea.setName("TextArea");
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setName("ScrollPane");
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        return scrollPane;
    }

    private void createMenu() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        menuBar.add(createFileMenu());
        menuBar.add(createSearchMenu());
    }

    private JMenu createSearchMenu() {
        JMenu searchMenu = new JMenu("Search");
        searchMenu.setName("MenuSearch");
        JMenuItem startSearchMenuItem = new JMenuItem("Start search");
        startSearchMenuItem.setName("MenuStartSearch");
        startSearchMenuItem.addActionListener(actionEvent -> searchText());

        JMenuItem previousSearchMenuItem = new JMenuItem("Previous search");
        previousSearchMenuItem.setName("MenuPreviousMatch");
        previousSearchMenuItem.addActionListener(previousMatch);

        JMenuItem nextSearchMenuItem = new JMenuItem("Next search");
        nextSearchMenuItem.setName("MenuNextMatch");
        nextSearchMenuItem.addActionListener(nextMatch);

        useRegExpMenuItem = new JCheckBoxMenuItem("Use regular expressions");
        useRegExpMenuItem.setName("MenuUseRegExp");
        useRegExpMenuItem.setSelected(isCheckedRegexp);
        useRegExpMenuItem.addActionListener(setCheckAction);

        searchMenu.add(startSearchMenuItem);
        searchMenu.add(previousSearchMenuItem);
        searchMenu.add(nextSearchMenuItem);
        searchMenu.add(useRegExpMenuItem);

        return searchMenu;
    }

    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.setName("MenuFile");
        JMenuItem loadMenuItem = new JMenuItem("Open");
        loadMenuItem.setName("MenuOpen");
        loadMenuItem.addActionListener(actionEvent -> loadFile());

        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.setName("MenuSave");
        saveMenuItem.addActionListener(actionEvent -> saveFile());

        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setName("MenuExit");
        exitMenuItem.addActionListener(actionEvent -> System.exit(0));

        fileMenu.add(loadMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        return fileMenu;
    }

    private JPanel createPanel() {

        JPanel panel = new JPanel();
        panel.setBounds(10, 10, 300, 30);
        Dimension buttonsSize = new Dimension(20, 20);

        JButton loadButton = new JButton(new ImageIcon("resources/openIcon16.png"));
        loadButton.setName("OpenButton");
        loadButton.setPreferredSize(buttonsSize);
        loadButton.addActionListener(actionEvent -> loadFile());

        JButton saveButton = new JButton(new ImageIcon("resources/saveIcon16.png"));
        saveButton.setName("SaveButton");
        saveButton.setPreferredSize(buttonsSize);
        saveButton.addActionListener(actionEvent -> saveFile());

        JButton searchButton = new JButton(new ImageIcon("resources/searchIcon16.png"));
        searchButton.setName("StartSearchButton");
        searchButton.setPreferredSize(buttonsSize);
        searchButton.addActionListener(actionEvent -> searchText());

        JButton previousButton = new JButton(new ImageIcon("resources/backIcon16.png"));
        previousButton.setName("PreviousMatchButton");
        previousButton.setPreferredSize(buttonsSize);
        previousButton.addActionListener(previousMatch);

        JButton nextButton = new JButton(new ImageIcon("resources/forwardIcon16.png"));
        nextButton.setName("NextMatchButton");
        nextButton.setPreferredSize(buttonsSize);
        nextButton.addActionListener(nextMatch);

        searchField = new JTextField(30);
        searchField.setName("SearchField");

        regExpCheckBox = new JCheckBox("Use regex");
        regExpCheckBox.setName("UseRegExCheckbox");
        regExpCheckBox.setSelected(isCheckedRegexp);
        regExpCheckBox.addActionListener(setCheckAction);

        panel.add(loadButton);
        panel.add(saveButton);
        panel.add(searchField);
        panel.add(searchButton);
        panel.add(previousButton);
        panel.add(nextButton);
        panel.add(regExpCheckBox);

        return panel;
    }

    ActionListener nextMatch = actionEvent -> {
        currentIndex++;
        showFoundText();
    };
    ActionListener previousMatch = actionEvent -> {
        currentIndex--;
        showFoundText();
    };
    ActionListener setCheckAction = actionEvent -> {
        isCheckedRegexp = !isCheckedRegexp;
        regExpCheckBox.setSelected(isCheckedRegexp);
        useRegExpMenuItem.setSelected(isCheckedRegexp);

    };

    private void searchText() {
        indexes.clear();
        currentIndex = 0;
        String foundText = searchField.getText();

        new TextSearch(indexes, foundText, this.textArea, isCheckedRegexp).execute();
    }

    private void showFoundText() {
        if (indexes.size() < 1) {
            return;
        }

        if (currentIndex < 0) {
            currentIndex = indexes.size() - 1;
        } else if (currentIndex >= indexes.size()) {
            currentIndex = 0;
        }
        int startIndex = indexes.get(currentIndex).getKey();
        int endIndex = indexes.get(currentIndex).getValue();

        textArea.setCaretPosition(endIndex);
        textArea.select(startIndex, endIndex);
        textArea.grabFocus();
    }

    private void saveFile() {

        File file = fileChooser.getSaveFile();
        if (file == null) {
            return;
        }

        setText(textArea.getText());

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(this.getText());
        } catch (IOException e) {
            //System.out.println("IOException at save");
        }
    }

    private void loadFile() {

        File file = fileChooser.getLoadFile();
        if (file == null) {
            return;
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] fileText = fis.readAllBytes();
            this.setText(new String(fileText));
        } catch (IOException e) {
            this.setText("");
            //System.out.println("IOException at load");
        }
        textArea.setText(TextEditor.this.getText());
    }
}
