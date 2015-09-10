package operators;

import information.InformationCenter;

import java.net.URL;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

public class GetResources {
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");

	public static ImageIcon getImageIcon(String path) {

		try {
			URL url = InformationCenter.class.getResource(path);
			ImageIcon img = new ImageIcon(url);
			return img;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.severe("Error in adding SingleMarkingPanel " +e.getClass().toString() + " :" +e.getMessage());
			return null;
		}
}
}
