package gui.saving.ImageSet;

import information.ImageLayer;
import information.MarkingLayer;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import operators.ImageCreator;
import gui.GUI;
import gui.saving.ExportResults;
import gui.saving.SingleImagePanel;

/**
 * The Class SelectAndCreateImageFiles extends ExportResult to export image files.
 */
public class SelectAndCreateImageFiles extends ExportResults {
	
	/** The created images. The list of BUfferedImageWithName -objects. */
	private ArrayList<BufferedImageWithName> createdImages;

	
	/**
	 * Instantiates a new SelectAndCreateImageFiles.
	 *
	 * @param d the d
	 * @param gui the gui
	 * @param iList the i list
	 * @param savingTypeID the saving type id
	 */
	public SelectAndCreateImageFiles(JDialog d, GUI gui,ArrayList<ImageLayer> iList, int savingTypeID) {
		super(d, gui, iList, savingTypeID);
	}
	
	/**
	 * Class constructor. Shows window where user can select ImageLayers and MarkingLayers to be exported.
	 * @param frame parent JFrame
	 * @param gui GUI object of Graphical interface
	 * @param iList	ArrayList of ImageLayers that are opened in main program
	 * @param savingTypeID int type of savingType
	 */
	public SelectAndCreateImageFiles(JFrame frame, GUI gui,ArrayList<ImageLayer> iList, int savingTypeID) {
		super(frame, gui, iList, savingTypeID);
		
	}

	/**
	 * Returns the created buffered images.
	 *
	 * @return the created buffered images
	 */
	public ArrayList<BufferedImageWithName> getCreatedBufferedImages(){
		return this.createdImages;
	}

	/* (non-Javadoc)
	 * @see gui.saving.ExportResults#initBrowsingPanelWithLabel()
	 */
	protected JPanel initBrowsingPanelWithLabel() throws Exception{
		return null;
	}

	/* (non-Javadoc)
	 * @see gui.saving.ExportResults#initImageViewPanelWithTitle()
	 */
	protected JPanel initImageViewPanelWithTitle(){
		return initImageViewPanel("Select ImageLayer and MarkingLayers");
	}

	/* (non-Javadoc)
	 * @see gui.saving.ExportResults#initSaveButtonWithTitle()
	 */
	protected JPanel initSaveButtonWithTitle() throws Exception{
		return initSaveButton("Create Images");
	}

	/* (non-Javadoc)
	 * @see gui.saving.ExportResults#startSavingProcess(int, int)
	 */
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
						if(created != null) // creation successful -> add to list.
							this.createdImages.add(new BufferedImageWithName(created, imp.getImageLayerName()));

						}
					 }
				}
				cancelSelected();

			} catch (Exception e) {
				e.printStackTrace();
			}
	}

}
