package gui.saving.ImageSet;

import information.ID;
import information.ImageLayer;
import information.MarkingLayer;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import operators.ImageCreator;
import gui.GUI;
import gui.ShadyMessageDialog;
import gui.saving.ExportResults;
import gui.saving.SaverDialog;
import gui.saving.SingleImagePanel;

public class SelectAndCreateImageFiles extends ExportResults {
	private ArrayList<BufferedImageWithName> createdImages;

	/**
	 * Class constructor. Shows window where user can select ImageLayers and MarkingLayers to shown.
	 * @param frame parent JFrame
	 * @param gui GUI object of Graphical interface
	 * @param iList	ArrayList of ImageLayers that are opened in main program
	 * @param savingTypeID int type of savingType
	 */
	public SelectAndCreateImageFiles(JFrame frame, GUI gui,ArrayList<ImageLayer> iList, int savingTypeID) {
		super(frame, gui, iList, savingTypeID);
		// TODO Auto-generated constructor stub

	}
	
	public SelectAndCreateImageFiles(JDialog d, GUI gui,ArrayList<ImageLayer> iList, int savingTypeID) {
		super(d, gui, iList, savingTypeID);
		// TODO Auto-generated constructor stub

	}

	protected JPanel initBrowsingPanelWithLabel() throws Exception{
		return null;
	}

	protected JPanel initImageViewPanelWithTitle(){
		return initImageViewPanel("Select ImageLayer and MarkingLayers");
	}

	protected JPanel initSaveButtonWithTitle() throws Exception{
		return initSaveButton("Create Images");
	}

	protected void startSavingProcess(int exportID, int exportType){
		try {
			createdImages=new ArrayList<BufferedImageWithName>();
				double sizeMultiplier=gui.getSizeMultiplier();
				ImageCreator imageCreator=new ImageCreator(sizeMultiplier, this.gui.taskManager);
				//go through all panels and pick imageLayers and MarkingLayers which are taken to images
				Component[] imPanelList= imageScrollPanel.getComponents();
				if(imPanelList != null && imPanelList.length>0 ){
					// create new LayersOfPath objects and add them to list
					 for (int i = 0; i < imPanelList.length; i++) {
						SingleImagePanel imp= (SingleImagePanel)imPanelList[i];
						ArrayList<MarkingLayer> selectedMarkingLayers = imp.getAllSelectedMarkingLayers();
						if(selectedMarkingLayers != null && selectedMarkingLayers.size()>0){
						BufferedImage created = imageCreator.createBufferedImage(imp.getImageLayer(), imp.getAllSelectedMarkingLayerIDs());
						if(created != null)
							this.createdImages.add(new BufferedImageWithName(created, imp.getImageLayerName()));

						}
					 }
				}

				cancelSelected();

			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	public ArrayList<BufferedImageWithName> getCreatedBufferedImages(){
		return this.createdImages;
	}

}
