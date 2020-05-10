package khomini.hyperskill.texteditor;

import javafx.util.Pair;

import javax.swing.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextSearch extends SwingWorker<ArrayList<Pair<Integer, Integer>>, Pair<Integer, Integer>> {

    private final ArrayList<Pair<Integer, Integer>> indexes;
    private final String foundText;
    private final String text;
    private final JTextArea textArea;
    private final boolean regExp;

    public TextSearch(ArrayList<Pair<Integer, Integer>> indexes, String foundText, JTextArea textArea, boolean regExp) {
        this.indexes = indexes;
        this.foundText = foundText;
        this.textArea = textArea;
        this.regExp = regExp;
        this.text = this.textArea.getText();
    }

    @Override
    protected ArrayList<Pair<Integer, Integer>> doInBackground() {
        return this.regExp ? findByRegExp() : findByPlainText();
    }

    @Override
    protected void done() {
        try {
            indexes.clear();
            indexes.addAll(get());
            showFirstFound();
        } catch (Exception ignoreForNow) {
            //
        }
    }

    private void showFirstFound() {
        if (indexes.size() < 1) {
            return;
        }
        int startIndex = indexes.get(0).getKey();
        int endIndex = indexes.get(0).getValue();
        textArea.setCaretPosition(endIndex);
        textArea.select(startIndex, endIndex);
        textArea.grabFocus();
    }

    private ArrayList<Pair<Integer, Integer>> findByRegExp() {
        ArrayList<Pair<Integer, Integer>> results = new ArrayList<>();
        Pattern pattern = Pattern.compile(this.foundText);
        Matcher matcher = pattern.matcher(this.text);
        while (matcher.find()) {
            results.add(new Pair<>(matcher.start(), matcher.end()));
        }
        return results;
    }

    private ArrayList<Pair<Integer, Integer>> findByPlainText() {
        ArrayList<Pair<Integer, Integer>> results = new ArrayList<>();
        if (this.text.contains(foundText)) {
            int index = this.text.indexOf(foundText);
            while (index >= 0) {
                results.add(new Pair<>(index, index + foundText.length()));
                index = this.text.indexOf(foundText, index + 1);
            }
        }
        return results;
    }

}
