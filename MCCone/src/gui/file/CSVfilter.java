package gui.file;


import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * The Class CSVfilter. Contains methods for accepting file as .csv file or folder 
 * and getting the description of file.
 */
public class CSVfilter extends FileFilter {

    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
     */
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

    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#getDescription()
     */
    //The description of this filter
    public String getDescription() {
        return ".csv";
    }
}
