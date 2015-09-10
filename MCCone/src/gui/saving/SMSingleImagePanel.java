package gui.saving;

import javax.swing.JPanel;

import information.ImageLayer;

public class SMSingleImagePanel extends SingleImagePanel{

	public SMSingleImagePanel(ImageLayer imageLayer, SaverDialog saverDialog) {
		super(imageLayer, saverDialog);
		// TODO Auto-generated constructor stub
	}

	protected JPanel initFilePathPanelWithLabel() throws Exception{
		return initFilePathPanel("Browse xml-file");
	}

}
