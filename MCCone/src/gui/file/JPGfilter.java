package gui.file;


import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * The Class JPGfilter. Contains methods for accepting .jpg files or folder 
 * and getting the description of file.
 */
public class JPGfilter extends FileFilter {

    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
     */
    //Accept all directories and all gif, jpg, tiff, or png files.
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = Utils.getExtension(f);
        if (extension != null) {
            if (extension.equals(Utils.jpeg) ||
                extension.equals(Utils.jpg) ) {
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
        return ".jpg";
    }
}
