package khomini.hyperskill.texteditor;

import javax.swing.*;
import java.io.File;

public class FileChooser extends JFileChooser {


    public FileChooser(String path) {
        super(path);
        this.setName("FileChooser");
        this.setVisible(false);
    }

    public File getLoadFile() {
        this.setVisible(true);
        int returnValue = this.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            this.setVisible(false);
            return this.getSelectedFile();
        }
        this.setVisible(false);
        return null;
    }

    public File getSaveFile() {
        this.setVisible(true);
        int returnValue = this.showSaveDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            this.setVisible(false);
            return this.getSelectedFile();
        }
        this.setVisible(false);
        return null;
    }
}
