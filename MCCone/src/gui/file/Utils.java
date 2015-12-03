package gui.file;




import java.io.File;

import javax.swing.ImageIcon;

/**
 * The Class Utils. Contains some variables of file extensions and methdods to manage them.
 */
public class Utils {
    public final static String jpeg = "jpeg";
    public final static String jpg = "jpg";
    public final static String gif = "gif";
    public final static String tiff = "tiff";
    public final static String tif = "tif";
    public final static String png = "png";
    public final static String xml= "xml";
    public final static String csv= "csv";
    public final static String txt= "txt";


    /**
     * Gets the extension.
     *
     * @param f the f
     * @return the extension
     */
    
    public static String getExtension(File f) {

        String s = f.getName();
        return getExtension(s);
    }

    /**
     * Gets the extension.
     *
     * @param s the s
     * @return the extension
     */
    public static String getExtension(String s){
    	String ext = null;
    	int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

    /**
     *  Returns an ImageIcon, or null if the path was invalid.
     *
     * @param path the path
     * @return the image icon
     */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = Utils.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
}
