package gui.file;




import java.io.File;

import javax.swing.ImageIcon;

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


    /*
     * Get the extension of a file.
     */
    public static String getExtension(File f) {

        String s = f.getName();
        return getExtension(s);
    }

    public static String getExtension(String s){
    	String ext = null;
    	int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

    /** Returns an ImageIcon, or null if the path was invalid. */
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
