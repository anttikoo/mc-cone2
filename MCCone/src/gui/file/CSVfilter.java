package gui.file;


import java.io.File;
import javax.swing.filechooser.FileFilter;

public class CSVfilter extends FileFilter {

    //Accept all directories and xml files
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = Utils.getExtension(f);
        if (extension != null) {
            if (extension.equals(Utils.csv)) {
                    return true;
            } else {
                return false;
            }
        }

        return false;
    }

    //The description of this filter
    public String getDescription() {
        return ".csv";
    }
}
