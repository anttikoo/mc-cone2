package gui.saving;

import javax.swing.JPanel;

import information.ID;
import information.ImageLayer;

public class ExSingleImagePanel extends SingleImagePanel{

	public ExSingleImagePanel(ImageLayer imageLayer, SaverDialog saverDialog) {
		super(imageLayer, saverDialog);
		// TODO Auto-generated constructor stub
	}
	protected JPanel initFilePathPanelWithLabel() throws Exception{
		return null;
	}

	public void setProperFileForMarkings(String properFileForMarkings, boolean showMessage, int fileValidityID) {
		// do nothing
	}

}
