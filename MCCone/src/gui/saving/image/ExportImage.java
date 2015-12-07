package gui.saving.image;

import gui.Color_schema;
import gui.GUI;
import gui.MouseListenerCreator;
import gui.ShadyMessageDialog;
import gui.file.FileManager;
import gui.saving.SaverDialog;
import gui.saving.SelectFileDialog;
import gui.saving.SingleImagePanel;
import operators.CheckBoxIcon;
import information.Fonts;
import information.ID;
import information.ImageLayer;
import information.LayersOfPath;
import information.MarkingLayer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import operators.ImageCreator;

public class ExportImage extends SaverDialog{
	//private JCheckBox showGridCheckBox;
	final static Logger LOGGER = Logger.getLogger("MCCLogger");
	
	
	
	public ExportImage(JFrame frame, GUI gui, ArrayList<ImageLayer> iList) {
		super(frame, gui, iList, ID.EXPORT_IMAGE);
	}

	protected JPanel initImageViewPanelWithTitle(){
		return initImageViewPanel("Export Image With Markings");
	}

	protected JPanel initBrowsingPanelWithLabel() throws Exception{
		return null;//initBrowsingPanel(0,"Show Grid of MarkingLayers if used.");
	}

	protected JPanel initSaveButtonWithTitle() throws Exception{
		return initSaveButton("Export Image");
	}

	protected SingleImagePanel createSingleImagePanel(ImageLayer layer){
		return new ExImaSingleImagePanel(layer,this);
	}

	protected void selectPathForSingleImagePanel(String properPath, int panelID) throws Exception{
		if(this.selectFileDialog == null)
			initSelectFileDialog();
		
	//	this.selectFileDialog.setProperFilePathForSaving(properPath);
		this.selectFileDialog.setProperFolderPathForSaving(properPath);

		this.selectFileDialog.setVisible(true);

		if(this.selectFileDialog.fileWritingType != ID.CANCEL && this.selectFileDialog.fileWritingType != ID.ERROR){
			setFileWritingType(this.selectFileDialog.getFileWritingType());
			setFilePathForSingleImagePanel(this.selectFileDialog.getSelectedFilePath(), panelID);

		}
			this.selectFileDialog.setVisible(false);
			this.selectFileDialog.dispose();


	}
	
	/* Checks the validity of file and has same file selected to another ImageLayer.
	 * @see gui.saving.SaverDialog#checkFileValidity(java.io.File)
	 */
	protected int checkFileValidity(File file){
		int validity = super.checkFileValidity(file);
		
		if(isFilePathFound(file.getAbsolutePath())){
			return ID.FILE_FILE_SELECT_BY_ANOTHER;
		}
		return validity;
	}

	protected void setSaveButtonEnabledByFileValidity(int vID){ //vID not used
		// go through all SingleImagePanels and if any has fileValidity ok -> enable saveButton
		Component[] imPanelList= imageScrollPanel.getComponents();

		if(imPanelList != null && imPanelList.length>0){
			 for (int i = 0; i < imPanelList.length; i++) {
				 int filVal=((SingleImagePanel)imPanelList[i]).getFileValidity();
				if(filVal==ID.FILE_OK || filVal == ID.FILE_NEW_FILE){
					this.saveJButton.setEnabled(true);
					return;
				}

			}
		}
		this.saveJButton.setEnabled(false);
	}

	protected void startSavingProcess(int exportID, int exportType){
		try {
			boolean wantedExport=false;
			ShadyMessageDialog dialog;
			setAllMarkingLayerBackgroundsToDefault();
				double sizeMultiplier=gui.getSizeMultiplier();
				ImageCreator imageCreator=new ImageCreator(sizeMultiplier, this.gui.taskManager);
				boolean filePathAccepted=false;
				boolean hasSelectedMarkingLayers=false;

			ArrayList<Integer> successfullIDs=new ArrayList<Integer>();

				//go through all panels and pick imageLayers and MarkingLayers which will be saved
				Component[] imPanelList= imageScrollPanel.getComponents();
				if(imPanelList != null && imPanelList.length>0 ){
					// create new LayersOfPath objects and add them to list
					 for (int i = 0; i < imPanelList.length; i++) {
						ExImaSingleImagePanel imp= (ExImaSingleImagePanel)imPanelList[i];
						if(imp.isSelected()){
							wantedExport=true;
							ArrayList<MarkingLayer> selectedMarkingLayers = imp.getAllSelectedMarkingLayers();
							// check first is the file path accepted
							if( (imp.getFileValidity() == ID.FILE_OK || imp.getFileValidity() == ID.FILE_NEW_FILE))
								filePathAccepted=true;
							if(selectedMarkingLayers != null && selectedMarkingLayers.size()>0 ){
								hasSelectedMarkingLayers=true;
								if( (imp.getFileValidity() == ID.FILE_OK || imp.getFileValidity() == ID.FILE_NEW_FILE)){
									// update the grid drawing to MarkingLayers of ImageLayer in imp
									imp.updateGridDrawing();
									ArrayList<Integer> savedMarkingIDList=imageCreator.createImage(imp.getImageLayer(), imp.getAllSelectedMarkingLayerIDs(), imp.getProperFilePathForSaving());
									if(savedMarkingIDList != null && savedMarkingIDList.size()>0){
										successfullIDs.addAll(savedMarkingIDList);
										// save to ImageLayer the exporting path -> remembers next time when starting exporting
										imp.getImageLayer().setExportImagePath(imp.getProperFilePathForSaving());
	
									}
								}
							}
					 	}

					 }

					 setSuccesfullSavingBackgrounds(successfullIDs);

					if(successfullIDs.size() >0){
						dialog = new ShadyMessageDialog(this, "Exporting succesfull", "Successfully exported MarkingLayers are at green background.  ", ID.OK, this);
						dialog.showDialog();

					}
					else{
						String info =  "unacceptable file path and lack of selected MarkingLayers.";
						if(wantedExport){
							if(filePathAccepted && !hasSelectedMarkingLayers){
								info =  "lack of selected MarkingLayers.";
							}
							else{
								if(!filePathAccepted && hasSelectedMarkingLayers)
								info =  "unacceptable file path.";
							}
						}
						else
							info =  " no any images selected.";
						dialog = new ShadyMessageDialog(this, "No Images Exported", "No any images exported possible due to "+info, ID.OK, this);
						dialog.showDialog();
					}
					dialog=null;

				}


			} catch (Exception e) {
				LOGGER.severe("Error in Exporting results: "+ e.getMessage());
				e.printStackTrace();
			}


	}

	protected JPanel initBrowsingPanel(int savingID, String label) throws Exception{
		return null;
	}



}
