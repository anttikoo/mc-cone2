package gui.saving;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFrame;

import operators.XMLreadManager;

public class MarkingsSelectFileDialog extends SelectFileDialog {
	private ArrayList<String> imageLayerNamesForXMLSearch;


	public MarkingsSelectFileDialog(JFrame frame, String path, JComponent backPanel, int fileType) {
		super(frame, path, backPanel, fileType);
		// TODO Auto-generated constructor stub
	}





	protected boolean hasImageLayersFound(String fileName){
		XMLreadManager readManager=new XMLreadManager();
	//	return XMLreadManager.foundImageLayer(file, imageLayersNamesForFileSelection);
		return readManager.foundImageLayer(fileName, imageLayerNamesForXMLSearch);
	}

	public void setImageLayerNamesForXMLSearch(ArrayList<String> list){
		this.imageLayerNamesForXMLSearch=list;
	}

}
