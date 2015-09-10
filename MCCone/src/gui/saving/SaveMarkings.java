package gui.saving;

import gui.GUI;
import gui.ShadyMessageDialog;
import gui.file.FileManager;
import information.ID;
import information.ImageLayer;
import information.LayersOfPath;
import information.MarkingLayer;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JPanel;

import operators.XMLreadManager;
import operators.XMLwriteManager;

public class SaveMarkings extends SaverDialog{
	private XMLwriteManager xmlWriteManager;
	private ArrayList<String> imageLayersNamesForFileSelection;

	public SaveMarkings(JFrame frame, GUI gui, ArrayList<ImageLayer> iList) {
		super(frame, gui, iList,ID.SAVE_MARKINGS);
		//xmlWriteManager=new XMLwriteManager();
		this.imageLayersNamesForFileSelection=null;
	}

	protected int checkFileValidity(File file){
		return FileManager.checkFile(file, SelectFileDialog.createFileFilterList(this.savingType), true);
	}

	protected void startSavingProcess(int savingID, int exportType){

		try {
			ShadyMessageDialog dialog;
			setAllMarkingLayerBackgroundsToDefault();
		//	ArrayList<Integer> unsavedLayers=new ArrayList<Integer>();
			ArrayList<Integer> successFullysavedLayers=new ArrayList<Integer>();
			ArrayList<LayersOfPath> layerOfPathList=new ArrayList<LayersOfPath>();
			//go through all panels and pick imageLayers and MarkingLayers which will be saved
			Component[] imPanelList= imageScrollPanel.getComponents();
			if(imPanelList != null && imPanelList.length>0 ){
				// create new LayersOfPath objects and add them to list
				 for (int i = 0; i < imPanelList.length; i++) {
					 SingleImagePanel imp= (SingleImagePanel)imPanelList[i];
					if(imp.getFileValidity() == ID.FILE_OK || imp.getFileValidity() == ID.FILE_NEW_FILE){
						ArrayList<MarkingLayer> selectedMarkingLayers=imp.getAllSelectedMarkingLayers();
						if(selectedMarkingLayers != null && selectedMarkingLayers.size()>0){

							// create new Imagelayer
							ImageLayer newIlayer=new ImageLayer(imp.getImagePath());
							// add selected MarkingLayers
							newIlayer.setMarkingLayerList(selectedMarkingLayers);

							if(layerOfPathList.size()>0){ // contains already some objects
								boolean madeSave=false;
								// go through objects of pathLayerList
								Iterator<LayersOfPath> pathLayerIterator= layerOfPathList.iterator();
								while(pathLayerIterator.hasNext()){

									LayersOfPath lop = (LayersOfPath)pathLayerIterator.next();
									if(lop.getXmlpath().equals(imp.getProperFilePathForSaving())){
										 // found LayersOfPath with right xml path -> add the ImageLayer here
										lop.addImageLayer(newIlayer);
										madeSave=true;
									}

								}
								if(!madeSave){ // not found the proper LayersOfPath for the ImageLayer -> create new
									LayersOfPath newLOP = new LayersOfPath(imp.getProperFilePathForSaving(), imp.getFileValidity());
									newLOP.addImageLayer(newIlayer);
									layerOfPathList.add(newLOP);
								}

							}
							else{ // empty
								// create new LayersOfPath object
								LayersOfPath newLOP = new LayersOfPath(imp.getProperFilePathForSaving(), imp.getFileValidity());
								newLOP.addImageLayer(newIlayer);
								layerOfPathList.add(newLOP);

							}

						}else{
							LOGGER.warning("No markings selected from ImageLayer: "+imp.getImageLayerName());
						}

					}else{
						LOGGER.warning("File Saving Path: "+imp.getProperFilePathForSaving()+ " is not valid. Change saving file.");
					}
				}


			}
			// all selected MarkingLayers are added to ImageLayers which are added to LayersOfPath -objects
			// start saving content of each LayersOfPath to xml-file
			if(layerOfPathList.size()>0){

				Iterator<LayersOfPath> layIterator= layerOfPathList.iterator();
				while(layIterator.hasNext()){
					LayersOfPath lop = layIterator.next();
					if(xmlWriteManager == null){
						xmlWriteManager=new XMLwriteManager();
					}
					if(xmlWriteManager.startWritingProcess(lop)){
						successFullysavedLayers.addAll(xmlWriteManager.getSuccessfullySavedMarkingLayers());
						updateImageLayerXMLpath(lop, xmlWriteManager.getSuccessfullySavedMarkingLayers());
					}
				//	unsavedLayers.addAll(xmlWriteManager.getUnsavedMarkingLayers());
				}

				setSuccesfullSavingBackgrounds(successFullysavedLayers);
				if(successFullysavedLayers.size()>0){
					gui.setMadeChanges(false); // the user has saved at least one MarkingLayer -> no asking to save when quitting MC-Cone (if no changes made before quit)
					if(notInformedSuccessfullSaving){
						dialog = new ShadyMessageDialog(new JFrame(), "Saving succesfull", "Successfully saved markings are shown green.  ", ID.OK, this);
						dialog.showDialog();
					}
				}

			}
			else{
				LOGGER.warning("No Markings selected to save!");
				dialog = new ShadyMessageDialog(new JFrame(), "Saving not succesfull", "Check the file path is acceptable and at least one MarkingLayer is selected  ", ID.OK, this);
				dialog.showDialog();
			}
			dialog=null;
			
		} catch (Exception e) {
			LOGGER.severe("Error in starting to save Markings: "+ e.getMessage());
			e.printStackTrace();
		}


	}

	private void updateImageLayerXMLpath(LayersOfPath lop, ArrayList<Integer> successfullysavedMarkings) throws Exception{

		Iterator<ImageLayer> iIterator = this.imageLayerList.iterator();
		while(iIterator.hasNext()){
			ImageLayer iLayer=iIterator.next();
			for (Iterator<Integer> iterator = successfullysavedMarkings.iterator(); iterator.hasNext();) {
				int mLayerID= (int) iterator.next();
				if(iLayer.hasMarkingLayer(mLayerID)){

					iLayer.setMarkingsFilePath(lop.getXmlpath());
					break;
				}

			}
		}
	}

	protected void setSaveButtonEnabledByFileValidity(int vID){ // vID is not used
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


	protected void selectPathForSingleImagePanel(String properPath, int panelID) throws Exception{
		if(this.selectFileDialog == null)
			initSelectFileDialog();
		this.selectFileDialog.setProperFilePathForSaving(properPath);
		this.selectFileDialog.setImageLayerNamesForXMLSearch(collectSingleSelectedImageLayerName(panelID));
		this.selectFileDialog.setVisible(true);

		if(this.selectFileDialog.fileWritingType != ID.CANCEL && this.selectFileDialog.fileWritingType != ID.ERROR){
			setFileWritingType(this.selectFileDialog.getFileWritingType());
			setFilePathForSingleImagePanel(this.selectFileDialog.getSelectedFilePath(), panelID);
			setMarkingLayerBackgroundsToDefault(panelID); // sets for single imagepanel
		}
			this.selectFileDialog.setVisible(false);
		this.selectFileDialog.dispose();
		this.imageLayersNamesForFileSelection=null;

	}

	protected void selectPathForAllSingleImagePanels() throws Exception{
		if(this.selectFileDialog == null)
			initSelectFileDialog();
		//get properpath of first SingleImagePanel
		String path = getfirstProperPathOfSingleImagePanels();
		this.selectFileDialog.setProperFilePathForSaving(path);
		this.selectFileDialog.setImageLayerNamesForXMLSearch(collectAllImageLayerNames());
	//	this.selectFileDialog.setMultiFileSelectionEnabled(false);
		this.selectFileDialog.setVisible(true);
		if(this.selectFileDialog.fileWritingType != ID.CANCEL && this.selectFileDialog.fileWritingType != ID.ERROR){
			setFileWritingType(this.selectFileDialog.getFileWritingType());

			setFilePathsForAll(this.selectFileDialog.getSelectedFilePath());
			setMarkingLayerBackgroundsToDefault(ID.IMAGELAYER_UNDEFINED); // sets for all imagePanels

		}
		this.selectFileDialog.setVisible(false);
		this.selectFileDialog.dispose();
		this.imageLayersNamesForFileSelection=null;


	}

	private ArrayList<String> collectAllImageLayerNames(){
		ArrayList<String> list=new ArrayList<String>();
		Component[] imPanelList= imageScrollPanel.getComponents();

		if(imPanelList != null && imPanelList.length>0){
			 for (int i = 0; i < imPanelList.length; i++) {
				list.add(((SingleImagePanel)imPanelList[i]).getImageLayerName());
			}
		}

		return list;
	}

	private ArrayList<String> collectSingleSelectedImageLayerName(int id){
		ArrayList<String> list=new ArrayList<String>();
		Component[] imPanelList= imageScrollPanel.getComponents();

		if(imPanelList != null && imPanelList.length>0){
			 for (int i = 0; i < imPanelList.length; i++) {
				if(((SingleImagePanel)imPanelList[i]).hasSelectedMarkingLayer() && ((SingleImagePanel)imPanelList[i]).getLayerID() == id){
					list.add(((SingleImagePanel)imPanelList[i]).getImageLayerName());
				}

			}
		}
		return list;
	}

	public boolean hasImageLayersFound(String fileName){
		XMLreadManager readManager=new XMLreadManager();
	//	return XMLreadManager.foundImageLayer(file, imageLayersNamesForFileSelection);
		return readManager.foundImageLayer(fileName, imageLayersNamesForFileSelection);
	}
	/*
	protected void selectPathForSingleImagePanel(String properPath, int panelID){
		this.selectFileDialog.setProperFilePathForSaving(properPath);
		int fileWritingType=this.selectFileDialog.showDialog();
		if(fileWritingType != ID.CANCEL && fileWritingType != ID.ERROR){




		}

	}
*/
	public JPanel initImageViewPanelWithTitle(){
		return initImageViewPanel("Save Markings");
	}
	protected JPanel initSaveButtonWithTitle() throws Exception{
		return initSaveButton("Save Markings");
	}

	protected SingleImagePanel createSingleImagePanel(ImageLayer layer){
		return new SMSingleImagePanel(layer, this);
	}

	protected JPanel initBrowsingPanelWithLabel() throws Exception{
		return initBrowsingPanel(this.savingType, "Browse file for for all");
	}

	protected void initSelectFileDialog() throws Exception{
		String path = gui.getPresentFolder();
		if(path== null)
			path = System.getProperty("user.home");
		this.selectFileDialog = new MarkingsSelectFileDialog(this.gui, path, this.backPanel, this.savingType);
	}



}
