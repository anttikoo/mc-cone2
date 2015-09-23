package gui;

import gui.file.ImageFilter;
import gui.file.OpenImageFilesDialog;
import gui.file.OpenMarkingFileDialog;
import gui.graphics.BigCloseIcon;
import gui.graphics.BigMarkingIcon;
import gui.graphics.MediumCloseIcon;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import information.*;

/**
 * Class AddImageLayerDialog is a window where user can import image files as ImageLayers and import previously saved MarkingLayers.
 * Also deleting ImageLayers is possible in this window.
 * @author Antti Kurronen
 *
 */
public class AddImageLayerDialog extends JDialog{

private GUI gui;
private JPanel backPanel;
private JButton cancelJButton;
private JPanel imageScrollPanel;
private ArrayList<ImageLayer> dialogImageLayerList;
private JScrollPane imageScrollingPane;
private JButton createImageLayersJButton;
private JButton addImageJButton;
private int typeOfDialog;
private Dimension importAllowedImageDimension=null;


	private final static Logger LOGGER = Logger.getLogger("MCCLogger");

	/**
	 * Class constructor for only creating new ImageLayers and importing markings
	 * @param frame Owner JFrame
	 * @param gui GUI object where from this class is called.
	 */
	public AddImageLayerDialog(JFrame frame, GUI gui){

			super(frame, true);
			try{
				typeOfDialog=ID.CREATE_NEW_IMAGELAYERS;
				dialogImageLayerList = new ArrayList<ImageLayer>();
				this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
				this.gui=gui;
				initComponents();


				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						selectAndAddImages();

					}
				});
				this.setVisible(true);


		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.severe("Error in adding new ImageLayer:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
		}
	}




	/**
	 * Class constructor for creating new ImageLayers from given image files.
	 * @param frame Owner JFrame
	 * @param gui GUI object where from this class is called.
	 */
	public AddImageLayerDialog(JFrame frame, GUI gui, File[] imageFiles){

			super(frame, true);
			try{
				typeOfDialog=ID.CREATE_NEW_IMAGELAYERS;
				dialogImageLayerList = new ArrayList<ImageLayer>();
				this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
				this.gui=gui;
				initComponents();
				if(imageFiles != null && imageFiles.length>0)
					addImagesToImageLayerList(imageFiles);
				this.setVisible(true);


		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.severe("Error in adding new ImageLayer:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
		}
	}

	/**
	 * Class constructor which shows already open ImageLayers enabling managing markings
	 * @param frame Owner JFrame
	 * @param gui GUI object where from this class is called.
	 */
	public AddImageLayerDialog(JFrame frame, GUI gui, ArrayList<ImageLayer> iList){

			super(frame, true);
			try{
				this.typeOfDialog=ID.MANAGE_IMAGE_LAYERS;
				this.dialogImageLayerList =   makeCopyOfList(iList);
				this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
				this.gui=gui;
				initComponents();
				updateImageList();
				this.setVisible(true);


		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.severe("Error in adding new ImageLayer:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
		}
	}

	/**
	 * Initializes components of window.
	 */
	private void initComponents(){	
		//init whole window to dim it
		this.setBounds(gui.getBounds());
		this.setUndecorated(true);
		this.setBackground(new Color(0,0,0,0));
		this.setContentPane(new ContentPane());
		this.getContentPane().setBackground(Color_schema.dark_30);
		this.getContentPane().setLayout(new GridBagLayout());
		//init backpanel where all funtions are shown.
		backPanel = new JPanel();
		backPanel.setBackground(new Color(0,0,0));
		backPanel.setLayout(new BorderLayout());
		backPanel.setBorder(BorderFactory.createLineBorder(Color_schema.button_light_border, 5));
		backPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		backPanel.setAlignmentY(JComponent.CENTER_ALIGNMENT);
		backPanel.setMaximumSize(new Dimension((int)(this.getBounds().getWidth()*0.95), (int)(this.getBounds().getHeight()*0.95)));
		backPanel.setMinimumSize(new Dimension((int)(this.getBounds().getWidth()*0.5), (int)(this.getBounds().getHeight()*0.5)));
		backPanel.setPreferredSize(new Dimension((int)(this.getBounds().getWidth()*0.6), (int)(this.getBounds().getHeight()*0.6)));
		//if backpanel size too small -> use more space of whole screen.
		if(backPanel.getPreferredSize().getWidth()<800 || backPanel.getPreferredSize().getHeight() <500)
			backPanel.setPreferredSize(new Dimension((int)(this.getBounds().getWidth()*0.95), (int)(this.getBounds().getHeight()*0.95)));

		backPanel.add(initImageViewPanel(), BorderLayout.CENTER);
		backPanel.add(initDownPanel(),BorderLayout.PAGE_END);

		this.add(backPanel);
		this.validate();
		this.repaint();
	}

	/**
	 * Returns the size of BackPanel.
	 * @return @see Rectangle size of BackPanel
	 */
	public Rectangle getBackPanelSize(){
		return this.backPanel.getBounds();
	}

	/**
	 * Returns  path of previously used folder.
	 * @return String path of previously used folder
	 */
	public String getPresentFolder(){
		return gui.getPresentFolder();
	}

	public void setPresentFolder(String folder){
		gui.setPresentFolder(folder);
	}

	private ArrayList<ImageLayer> makeCopyOfList(ArrayList<ImageLayer> iLayer){

		try {
			ArrayList<ImageLayer> copyList = new ArrayList<ImageLayer>();
			if(iLayer != null && iLayer.size()>0){
				Iterator<ImageLayer> iterator = iLayer.iterator();
				while(iterator.hasNext()){
					ImageLayer im = (ImageLayer)iterator.next();
					if(im != null){
						ImageLayer copyImageLayer = im.makeCopy();
						if(copyImageLayer != null)
							copyList.add(copyImageLayer);
					}
				}


			}
			return copyList;
		} catch (Exception e) {
			LOGGER.severe("Error in creating copy of imageLayerList " +e.getClass().toString() + " :" +e.getMessage());
			return null;
		}

	}




	/**
	 * Initializes an action for a JButton.
	 * @param button JButton where the action is added
	 * @param typeOfItem int type of action  @see information.ID
	 * @throws Exception
	 */
	private void initActionsToButtons(JButton button, int typeOfItem) throws Exception{
		// deleting ImageLayer
		if(typeOfItem == ID.DELETE_IMAGELAYER)
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				deleteImageLayer(((ImageAndMarkingPanel)((JPanel)((JPanel)((JButton)arg0.getSource()).getParent()).getParent()).getParent()).getPath());
				updateImageList();
			}
		});


		// deleting MarkingLayer
			if(typeOfItem == ID.DELETE_MARKINGLAYER){
				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						//remove markinglayer and update panels
						String imagelayerPath =((SingleMarking)((JButton)arg0.getSource()).getParent()).getImageLayerPath();
						String markingLayerName =((SingleMarking)((JButton)arg0.getSource()).getParent()).getMarkingName();
						// deletes the MarkingLayer of the ImageLayer
						deleteMarkingLayer(imagelayerPath, markingLayerName);
						// update visible list of window
						updateImageList();
					}
				});
			}
			else

			// Browsing xml-file for adding new markinglayer to selected ImageLayer.
					if(typeOfItem == ID.OPEN_MARKING_FILE){
						button.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								// activate filechooser to open markings. Gives the image path of ImageLayer as parameter
							//	openFileDialog(ID.OPEN_MARKING_FILE,((ImageAndMarkingPanel)((JPanel)((JPanel)((JButton)arg0.getSource()).getParent()).getParent()).getParent()).getPath());
								selectAndAddMarkings(((ImageAndMarkingPanel)((JPanel)((JPanel)((JButton)arg0.getSource()).getParent()).getParent()).getParent()).getPath(), false);
								// update visible list of window
								updateImageList();
							}
						});
					}
					else
					// CREATE IMAGELAYERS -button
					if(typeOfItem == ID.CREATE_NEW_IMAGELAYERS){
						button.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								continueCreatingImageLayers(); // closes dialog and adds imagelayers to InformationCenter
							}
						});
					}
					else
						// UPDATE IMAGELAYERS -button
						if(typeOfItem == ID.MANAGE_IMAGE_LAYERS){
							button.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent arg0) {
									continueUpdatingImageLayers();
								}
							});
						}
						else
							// CANCEL -button
							if(typeOfItem == ID.CANCEL){
								button.addActionListener(new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent arg0) {
										cancelSelected();
									}
								});
							}
	}

	/**
	 * Creates ImageLayer objects to imageLayerList by inserting paths of parameter file(s).
	 * If path is already used in an imageLayer object, no new ImageLayer is created.
	 * @param imageFiles List of files, which path are used in creating  ImageLayer objects
	 */
	private void addImagesToImageLayerList(File[] imageFiles){
		ShadyMessageDialog dialog;
		if(imageFiles !=null && imageFiles.length>0)

		for(int i=0; i<imageFiles.length;i++){
			if(imageFiles[i] != null && imageFiles[i].exists() && imageFiles[i].getAbsolutePath().length() >3){
				if(isImageFile(imageFiles[i])){
					if(!imageNameAlreadyInList(imageFiles[i].getName()) && !gui.imageNameAlreadyUsed(imageFiles[i].getName())){
						dialogImageLayerList.add(new ImageLayer(imageFiles[i].getAbsolutePath())); // create new ImageLayer by giving the path of image
					}
					else{
						// inform user that image with same name is already used
						dialog = new ShadyMessageDialog(new JFrame(), "Refused opening image", " Image name:  "+imageFiles[i].getName() + " is already open", ID.OK, this);
						dialog.showDialog();
					}
				} // if file is wrong format of the dimensio is wrong -> informed in isImageFile -method

			}else{
				// inform user that image with same name is already used
				dialog = new ShadyMessageDialog(new JFrame(), "Refused opening image", " Image name:  "+imageFiles[i].getName() + " doesn't exist!", ID.OK, this);
				dialog.showDialog();

			}
		}
		updateImageList();
		dialog=null;
		imageFiles=null;

	}

	/** Checks is the given file proper image file (supported images : tiff, jpg, png...)
	 *  and is the image size same as already imported images. ONLY IMAGES WITH SAME PIXEL SIZE ARE ALLOWED TO OPEN!
	 *
	 * @param file the file to be checked
	 * @return boolean value is file proper image file
	 */
	private boolean isImageFile(File file){
		ShadyMessageDialog dialog;
		try {
			ImageFilter filterImage=new ImageFilter();
			if(filterImage.accept(file)){
				if(this.gui.isAllowedImageDimension(file) && isImportingAllowedImageDimension(file)){
					LOGGER.fine("image file has allowed dimension");
					return true;
				}
				else{
					LOGGER.warning("Dimension of selected file "+file.getName()+" differs from other images! Not imported!");
					dialog = new ShadyMessageDialog(new JFrame(), "Refused opening image", " Dimension of selected file "+file.getName()+" differs from other images! Not imported!", ID.OK, this);
					dialog.showDialog();
					dialog=null;

					return false;
				}
			}
			else{
				LOGGER.warning("The image format of selected file "+file.getName()+" is not acceptable! Not imported");
				dialog = new ShadyMessageDialog(new JFrame(), "Refused opening image", "The image format of selected file "+file.getName()+" is not acceptable! Not imported", ID.OK, this);
				dialog.showDialog();

				return false;
			}
		} catch (Exception e) {
			LOGGER.severe("Error in adding Markings to ImageLayer :" +e.getMessage());

			dialog = new ShadyMessageDialog(new JFrame(), "Refused opening image", "The image  "+file.getName()+" may be broken! Not imported", ID.OK, this);
			dialog.showDialog();

			return false;
		}
		finally{dialog=null;}

	}

	private boolean isImportingAllowedImageDimension(File file) {
		try {
			Dimension iDimension=this.gui.taskManager.getImageDimension(file);

			if(this.importAllowedImageDimension == null){
				this.importAllowedImageDimension=iDimension;
				return true;
			}
			if(iDimension != null && this.importAllowedImageDimension.width == iDimension.width && this.importAllowedImageDimension.height == iDimension.height)
				return true;

			return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

	private JButton addKeyListenerToButton(final JButton button){

		InputMap inputMap= (button).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(KeyStroke.getKeyStroke("pressed ENTER"), "enter_pressed");
		ActionMap actionMap = 	(button).getActionMap();
		actionMap.put("enter_pressed", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				button.doClick();

			}

		});

		return button;
	}

	/** Adds markings from XML-file to wanted ImageLayer
	 * @param xmlFile External file containing markings for selected imageLayer
	 * @param imageLayerPath The absolute filepath of selected ImageLayer
	 */
	private void addMarkingsToSelectedImageLayer(File xmlFile, String imageLayerPath){

		try {
			int importedNumber=0;
			if(xmlFile !=null && xmlFile.exists()){
				Iterator<ImageLayer> iIterator = dialogImageLayerList.iterator();
				while(iIterator.hasNext()){
					ImageLayer im = (ImageLayer)iIterator.next();
					if(imageLayerPath != null && imageLayerPath.length()>0 && im.hasSameImageName(imageLayerPath) || imageLayerPath == null){ // ImageLayerPath match (the path is unique)
						// get all possible markinglayers for selected imageLayer

						//create new copy of ImageLayer im to make search of markings from xml file -> check duplicates afterwards
						ImageLayer copyImageLayer = im.makeCopy();
						// remove all MarkingLayers. Only MarkingLayers found from XML file are need to be in this object.
						copyImageLayer.removeAllMarkingLayers();

						 // Make search after search copyImageLayer will contain MarkingLayers found from XML
						gui.getMarkingOfXML(xmlFile, copyImageLayer);
						if(copyImageLayer.getMarkingLayers() != null && copyImageLayer.getMarkingLayers().size()>0){ // did any markinglayers were found
							// add found MarkingLayers. If found MarkingLayer exists -> confirm overwrite
							if(addMarkingLayerIfNotExists(im, copyImageLayer)
									&& (im.getMarkingsFilePath() == null || im.getMarkingsFilePath().length()==0))
								im.setMarkingsFilePath(xmlFile.getAbsolutePath());
							importedNumber++;
						}
						else{ // no markins found -> inform user

							if(imageLayerPath != null){
								ShadyMessageDialog dialog = new ShadyMessageDialog(new JFrame(), "MARKINGS NOT FOUND!", "No markings for selected ImageLayer were found!", ID.OK, this);
								dialog.showDialog();
								dialog=null;
							}
						}


					}

				}
				if(imageLayerPath == null){ // imported for several ImageLayers
					if(dialogImageLayerList != null && dialogImageLayerList.size() > importedNumber){
						if(importedNumber >0){
						ShadyMessageDialog dialog = new ShadyMessageDialog(new JFrame(), "Importing successfull!", "Imported markings for "+importedNumber+" of "+dialogImageLayerList.size()+ " ImageLayers.", ID.OK, this);
						dialog.showDialog();
						dialog=null;
						}
						else{
							ShadyMessageDialog dialog = new ShadyMessageDialog(new JFrame(), "Importing not successfull!", "No any Markings found for ImageLayers.", ID.OK, this);
							dialog.showDialog();
							dialog=null;
						}
					}
				}

			}
			updateImageList();
		} catch (Exception e) {
			LOGGER.severe("Error in adding Markings to ImageLayer " +e.getClass().toString() + " :" +e.getMessage());
			e.printStackTrace();
		}
	}

	private boolean addMarkingLayerIfNotExists(ImageLayer originalIL, ImageLayer copyIL){
		try {
			boolean madeSave=false; // is any markings saved
			if(originalIL!= null && originalIL.getMarkingLayers() != null
					&& copyIL != null && copyIL.getMarkingLayers() != null && copyIL.getMarkingLayers().size() >0){
				// go through all MarkingLayer objects of copyIL
				Iterator<MarkingLayer> mIterator = copyIL.getMarkingLayers().iterator();
				while(mIterator.hasNext()){
					MarkingLayer maCopy = (MarkingLayer)mIterator.next();
					// Check is MarkingLayer already in original ImageLayer
					if(originalIL.isMarkingLayerInList(maCopy)){
						// found same MarkingLayer (name) from both ImageLayers -> confirm overwrite
						JFrame frame = new JFrame();
						ShadyMessageDialog dialog = new ShadyMessageDialog(frame, "Imported MarkingLayer "+ maCopy.getLayerName()+ " already exists!", "OVERWRITE?", ID.YES_NO, this);
						if(dialog.showDialog() == ID.YES){
							LOGGER.fine("overwriting markingLayer: " + maCopy.getLayerName());
							//remove original
							originalIL.removeMarkingLayer(maCopy); // this method compares names of MarkingLayers and if same -> removed
							// add new one
							originalIL.addMarkingLayer(maCopy); // add new one
							madeSave=true;
						}
						dialog=null;

					}
					else{ // not found -> add MarkingLayer to original ImageLayer
						originalIL.addMarkingLayer(maCopy.makeCopy()); // for safety add copy of Markinglayer
						madeSave=true;
					}
				}
			}
			return madeSave;
		} catch (HeadlessException e) {
			LOGGER.severe("Error in adding Markings to ImageLayer " +e.getClass().toString() + " :" +e.getMessage());
			return false;
		}
	}


	private void addMouseListenerToButtons(JButton button, int id) throws Exception{
		if(id == ID.BUTTON_CLOSE_SMALL){
		button.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
				((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.button_orange_border, 1));
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
				((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.button_grey_border, 1));
			}
			@Override
			public void mousePressed(MouseEvent arg0) {
				((JButton)arg0.getSource()).setIcon(getImageIcon("/images/close_small_selected.png"));
			}
			@Override
			public void mouseReleased(MouseEvent arg0) {
				((JButton)arg0.getSource()).setIcon(getImageIcon("/images/close_small.png"));
			}
		});
		}
		else if(id == ID.BUTTON_CLOSE_25){
			button.addMouseListener(new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent arg0) {
					// TODO Auto-generated method stub
				}
				@Override
				public void mouseEntered(MouseEvent arg0) {
					((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.button_orange_border, 1));
				}
				@Override
				public void mouseExited(MouseEvent arg0) {
					((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.dark_40, 1));
				}
				@Override
				public void mousePressed(MouseEvent arg0) {
					((JButton)arg0.getSource()).setIcon(new BigCloseIcon(true));
				}
				@Override
				public void mouseReleased(MouseEvent arg0) {
					((JButton)arg0.getSource()).setIcon(new BigCloseIcon(false));
				}
			});
		}
		else if(id == ID.BUTTON_CLOSE_BIG){
			button.addMouseListener(new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent arg0) {
					// TODO Auto-generated method stub
				}
				@Override
				public void mouseEntered(MouseEvent arg0) {
					((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.button_orange_border, 1));
				}
				@Override
				public void mouseExited(MouseEvent arg0) {
					((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.button_grey_border, 1));
				}
				@Override
				public void mousePressed(MouseEvent arg0) {
					((JButton)arg0.getSource()).setIcon(getImageIcon("/images/close_big_selected.png"));
				}
				@Override
				public void mouseReleased(MouseEvent arg0) {
					((JButton)arg0.getSource()).setIcon(getImageIcon("/images/close_big.png"));
				}
			});
		}
		else if(id == ID.BUTTON_IMPORT_MARKINGS){
			button.addMouseListener(new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent arg0) {
					// TODO Auto-generated method stub
				}
				@Override
				public void mouseEntered(MouseEvent arg0) {
					((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.button_orange_border, 1));
				}
				@Override
				public void mouseExited(MouseEvent arg0) {
					((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.dark_40, 1));
				}
				@Override
				public void mousePressed(MouseEvent arg0) {
					((JButton)arg0.getSource()).setIcon(new BigMarkingIcon(true));
				}
				@Override
				public void mouseReleased(MouseEvent arg0) {
					((JButton)arg0.getSource()).setIcon(new BigMarkingIcon(false));
				}
			});
		}else if(id == ID.BUTTON_CANCEL){
			button.addMouseListener(new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent e) {
					// TODO Auto-generated method stub
				}
				@Override
				public void mouseEntered(MouseEvent e) {
					if(((JButton)e.getSource()).isEnabled())
					((JButton)e.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.button_orange_border, 2));
				}
				@Override
				public void mouseExited(MouseEvent e) {
					((JButton)e.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.button_grey_border, 2));
				}
				@Override
				public void mousePressed(MouseEvent e) {
					((JButton)e.getSource()).setForeground(Color_schema.red);
				}
				@Override
				public void mouseReleased(MouseEvent e) {
					((JButton)e.getSource()).setForeground(Color_schema.orange_dark);
				}
			});
		}else if(id == ID.BUTTON_NORMAL){
			button.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {
					// TODO Auto-generated method stub
				}
				@Override
				public void mouseEntered(MouseEvent e) {
					if(((JButton)e.getSource()).isEnabled())
					((JButton)e.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.button_orange_border, 2));
				}
				@Override
				public void mouseExited(MouseEvent e) {
					((JButton)e.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.button_grey_border, 2));
				}
				@Override
				public void mousePressed(MouseEvent e) {
					((JButton)e.getSource()).setForeground(Color_schema.orange_dark);
				}
				@Override
				public void mouseReleased(MouseEvent e) {
					((JButton)e.getSource()).setForeground(Color_schema.white_230);
				}
			});
		}
	}






	private void cancelSelected(){
		this.setVisible(false);

	}



private void continueCreatingImageLayers(){
	this.setVisible(false);
	gui.addImageLayerList(this.dialogImageLayerList);

	this.dispose();

}

private void continueUpdatingImageLayers(){
	this.setVisible(false);
	this.gui.setImageLayerList(this.dialogImageLayerList);
	this.dispose();
}





private ImageAndMarkingPanel createImagePanel(ImageLayer layer){
	return new ImageAndMarkingPanel(layer);
}

private void deleteImageLayer(String path){
	try {
		LOGGER.fine("delete: " +path);
		Iterator<ImageLayer> iIterator = dialogImageLayerList.iterator();
		while(iIterator.hasNext()){
			ImageLayer im = (ImageLayer)iIterator.next();
			if(im.getImageFilePath().equals(path)){

				// ask the user should the ImageLayer being deleted

				ShadyMessageDialog dialog = new ShadyMessageDialog(new JFrame(), "DELETE", "Delete ImageLayer:  "+im.getImageFileName(), ID.YES_NO, this);
				if(dialog.showDialog() == ID.YES){
					LOGGER.fine("deleted imageLayer: " + im.getImageFilePath());
					iIterator.remove();
				}
				dialog=null;
			}
		}
	} catch (Exception e) {
		LOGGER.severe("Error in deleting imageLayer from IMAGE LIST " +e.getClass().toString() + " :" +e.getMessage());

	}
}



private void deleteMarkingLayer(String imagePath, String markingName){
	try {
		LOGGER.fine("delete: " +markingName);
		ImageLayer im = getImageLayer(imagePath);
		if(im != null && im.getMarkingLayers() != null && im.getMarkingLayers().size() >0){

			Iterator<MarkingLayer> mIterator = im.getMarkingLayers().iterator();
			while(mIterator.hasNext()){
				MarkingLayer ma = (MarkingLayer)mIterator.next();
				if(ma.getLayerName().equals(markingName)){
					// ask the user should the ImageLayer being deleted
					ShadyMessageDialog dialog = new ShadyMessageDialog(new JFrame(), "DELETE", "Delete MarkingLayer:  "+ma.getLayerName(), ID.YES_NO, this);
					if(dialog.showDialog() == ID.YES){
						LOGGER.fine("deleted markingLayer: " + ma.getLayerName());
						mIterator.remove();
					}
					dialog=null;
				}
			}
		}

	} catch (Exception e) {
		LOGGER.severe("Error in deleting imageLayer from IMAGE LIST " +e.getClass().toString() + " :" +e.getMessage());

	}
}

private ImageIcon getImageIcon(String path) {

		try {
			URL url = this.getClass().getResource(path);
			ImageIcon img = new ImageIcon(url);
			return img;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.severe("Error in getting imageIcon" +e.getClass().toString() + " :" +e.getMessage());
			return null;
		}
}

private ImageLayer getImageLayer(String path){
	try {
		LOGGER.fine("get: " +path);
		Iterator<ImageLayer> iIterator = dialogImageLayerList.iterator();
		while(iIterator.hasNext()){
			ImageLayer im = (ImageLayer)iIterator.next();
			if(im.getImageFilePath().equals(path)){

				return im;
			}

		}
		return null;
	} catch (Exception e) {
		LOGGER.severe("Error in deleting imageLayer from IMAGE LIST " +e.getClass().toString() + " :" +e.getMessage());
		return null;
	}
}

/**
 *  Method for checking is image path already in an ImageLayer
 * @param item a path String which is searched from imageLayerList
 * @return boolean value true if path is already in an ImageLayer of imageLayerList
 */
private boolean imageNameAlreadyInList(String item){
	try {

		Iterator<ImageLayer> iIterator = dialogImageLayerList.iterator();
		while(iIterator.hasNext()){
			ImageLayer im = (ImageLayer)iIterator.next();
			if(im.getImageFileName().equals(item)){
				return true;
			}

		}
		return false;
	} catch (Exception e) {
		LOGGER.severe("Error in deleting imageLayer from IMAGE LIST " +e.getClass().toString() + " :" +e.getMessage());
		return true;
	}

}

/**
 * Creates JPanel containing JButtons for accept or cancel managing layers.
 * @return JPanel which contains JButtons
 */
private JPanel initDownPanel(){

	try {
		// SETUP buttons of lowest part of tab
		JPanel downButtonPanel =new JPanel();
		downButtonPanel.setLayout(new BoxLayout(downButtonPanel, BoxLayout.LINE_AXIS));
		downButtonPanel.setBackground(Color_schema.dark_40);
		downButtonPanel.setMinimumSize(new Dimension((int)backPanel.getMinimumSize().getWidth(), 50));
		downButtonPanel.setPreferredSize(new Dimension(100,50));
		downButtonPanel.setMaximumSize(new Dimension(100,50));

		JPanel createImageLayerJPanel = new JPanel();
		createImageLayerJPanel.setLayout(new BorderLayout());
		createImageLayerJPanel.setPreferredSize(new Dimension(220,30));
		createImageLayerJPanel.setMaximumSize(new Dimension(220,30));
		createImageLayersJButton = new JButton("CREATE IMAGELAYERS");
		createImageLayersJButton.setEnabled(false);
		createImageLayersJButton.setPreferredSize(new Dimension(220,30));
		createImageLayersJButton.setFocusable(false);

		//createImageLayersJButton.setContentAreaFilled(false);
		if(this.typeOfDialog == ID.CREATE_NEW_IMAGELAYERS){
			initActionsToButtons(createImageLayersJButton, ID.CREATE_NEW_IMAGELAYERS);
		}
		else{
			createImageLayersJButton.setText("UPDATE IMAGELAYERS");
			initActionsToButtons(createImageLayersJButton, ID.MANAGE_IMAGE_LAYERS);
		}

		addKeyListenerToButton(createImageLayersJButton); // when enter pressed -> this button activated

		MouseListenerCreator.addMouseListenerToNormalButtons(createImageLayersJButton); // changes the colors of button when pressed


		createImageLayerJPanel.add(createImageLayersJButton, BorderLayout.CENTER);
		JPanel cancelJpanel = new JPanel();
		cancelJpanel.setLayout(new BorderLayout());
		cancelJpanel.setPreferredSize(new Dimension(150,50));
		cancelJpanel.setMaximumSize(new Dimension(100,30));
		cancelJButton = new JButton("CANCEL");
//		cancelJButton.setContentAreaFilled(false);
		cancelJButton.setFocusable(false);
		cancelJButton.setForeground(Color_schema.orange_dark);
		initActionsToButtons(cancelJButton, ID.CANCEL);	// the action
		MouseListenerCreator.addMouseListenerToCancelButtons(cancelJButton); // changes the colors of button when pressed

		cancelJButton.setPreferredSize(new Dimension(100,50));

		cancelJpanel.add(cancelJButton);
		downButtonPanel.add(Box.createRigidArea(new Dimension(20,0)));
		downButtonPanel.add(createImageLayerJPanel);
		downButtonPanel.add(Box.createHorizontalGlue());
		downButtonPanel.add(cancelJpanel);
		downButtonPanel.add(Box.createRigidArea(new Dimension(20,0)));
		return downButtonPanel;

	} catch (Exception e) {
		LOGGER.severe("Error in creating buttons of open images windown:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
		return null;
	}

}



	private JPanel initImageViewPanel(){
		// SETUP THE Panel which shows the list of images and markings
					try {

						JPanel iBackPanel = new JPanel();
					//	iBackPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
						iBackPanel.setLayout(new BorderLayout());
						iBackPanel.setBackground(new Color(0,0,0));
						iBackPanel.setLayout(new BorderLayout());
						iBackPanel.setMaximumSize(this.getMaximumSize());
						iBackPanel.setBorder(BorderFactory.createLineBorder(Color_schema.button_light_border, 1));

						JPanel upPanel = new JPanel();
						upPanel.setBackground(Color_schema.dark_30);
						upPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
						upPanel.setMaximumSize(new Dimension(200,40));
						upPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
						upPanel.setBorder(BorderFactory.createEmptyBorder());
						JLabel titleLabel = new JLabel("OPEN IMAGES");
						if(this.typeOfDialog == ID.MANAGE_IMAGE_LAYERS)
							titleLabel.setText("Manage ImageLayers and MarkingLayers");
						titleLabel.setFont(new Font("Consolas", Font.BOLD,20));
						titleLabel.setForeground(Color_schema.white_230);
						upPanel.add(titleLabel);
						iBackPanel.add(upPanel, BorderLayout.PAGE_START);

						// CENTER PANEL
						JPanel centerPanel=new JPanel();
						centerPanel.setBackground(Color_schema.dark_40);
						centerPanel.setLayout(new BorderLayout());
						centerPanel.setBorder(BorderFactory.createEmptyBorder());
						JPanel scrollBiggerPanel = new JPanel();
						scrollBiggerPanel.setLayout(new BorderLayout());
						scrollBiggerPanel.setBorder(BorderFactory.createEmptyBorder());
						scrollBiggerPanel.setBackground(Color_schema.dark_40);

						imageScrollPanel = new JPanel();
						imageScrollPanel.setBackground(Color_schema.dark_40);
						imageScrollPanel.setMinimumSize(new Dimension(((int)(backPanel.getMinimumSize().getWidth()*0.4)), ((int)(backPanel.getMinimumSize().getHeight()*0.7)-40)));
					//	imageScrollPanel.setPreferredSize(preferredSize);
						imageScrollPanel.setLayout(new BoxLayout(imageScrollPanel, BoxLayout.PAGE_AXIS));
						imageScrollingPane = new JScrollPane(imageScrollPanel);
						imageScrollingPane.setBorder(BorderFactory.createLineBorder(Color_schema.button_grey_border, 2));
						imageScrollingPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
						imageScrollingPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
						imageScrollingPane.setPreferredSize(new Dimension((int)backPanel.getPreferredSize().getWidth()-20,(int)backPanel.getPreferredSize().getHeight()-140));
						imageScrollingPane.setMaximumSize(new Dimension((int)backPanel.getPreferredSize().getWidth()-20,(int)backPanel.getPreferredSize().getHeight()-140));
						// SETUP Button and it's JPanel for adding selected pahths to list
						JPanel imageButtonJPanel = new JPanel();
						imageButtonJPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
					//	addFilePathsJPanel.setLayout(new BoxLayout(addFilePathsJPanel, BoxLayout.LINE_AXIS));
						imageButtonJPanel.setBackground(Color_schema.dark_30);
						imageButtonJPanel.setMinimumSize(new Dimension(200,50));
						imageButtonJPanel.setMaximumSize(new Dimension(200,50));
					//	addImagesJPanel.setMaximumSize(new Dimension(400,40));

						addImageJButton = new JButton("ADD IMAGE");
						addImageJButton.setPreferredSize(new Dimension(150,30));
						addImageJButton.setBackground(Color_schema.dark_20);
						addImageJButton.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								selectAndAddImages();
							}
						});
						addImageJButton.setFocusable(false);
						addMouseListenerToButtons(addImageJButton, ID.BUTTON_NORMAL);
						imageButtonJPanel.add(addImageJButton);


						JButton addMarkingsForAll = new JButton("Import Markings For All ImageLayers");
						addMarkingsForAll.setPreferredSize(new Dimension(350,30));
						addMarkingsForAll.setBackground(Color_schema.dark_20);
						addMarkingsForAll.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								if(dialogImageLayerList != null && dialogImageLayerList.size()>0)
									selectAndAddMarkings(dialogImageLayerList.get(0).getImageFilePath(),true);
								else{
									showMessage("No ImageLayers", "Not found ImageLayers where to add markings");

								}
							}
						});
						addMarkingsForAll.setFocusable(false);
						addMouseListenerToButtons(addMarkingsForAll, ID.BUTTON_NORMAL);
						imageButtonJPanel.add(addMarkingsForAll);


						scrollBiggerPanel.add(imageScrollingPane, BorderLayout.CENTER);
						centerPanel.add(scrollBiggerPanel, BorderLayout.CENTER);
						centerPanel.add(imageButtonJPanel, BorderLayout.PAGE_END);


						iBackPanel.add(centerPanel, BorderLayout.CENTER);
					//	iBackPanel.add(centerPanel);

						return iBackPanel;
					} catch (Exception e) {
						LOGGER.severe("Error in creating imageViewPanel:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
						return null;
					}

	}

	//
	/**
	 *
	 * Opens a file dialog, which type depends on which file type will be opened.
	 * @param typeOfFile int ID which has two options: ID.OPEN_IMAGE_FILE or ID.OPEN_MARKING_FILE
	 * @param pathIfAny String path for image where markings are added. If selecting image file this parameter is null.
	 */
	private void selectAndAddImages(){
		JFrame openfileFrame = new JFrame();
		OpenImageFilesDialog od=new OpenImageFilesDialog(openfileFrame, this.getBounds(), this.backPanel.getBounds(), gui.getPresentFolder());
		od.setVisible(true);
		gui.setPresentFolder(od.getPresentFolder());
		File[] imagefiles = od.getSelectedFiles();
		if(imagefiles != null && imagefiles.length>0)
		addImagesToImageLayerList(imagefiles);
	}

	private void showMessage(String title, String message){
		ShadyMessageDialog dialog = new ShadyMessageDialog(new JFrame(), title, message, ID.OK, this);
		dialog.showDialog();
		dialog=null;
	}

	/**
	 * @param imageLayerPath Path for selected imageLayer or first ImageLayer of list
	 * @param isMarkingsForAll Boolean should add MarkingLayers to all ImageLayers if found.
	 */
	private void selectAndAddMarkings(String imageLayerPath, boolean isMarkingsForAll){
		JFrame openfileFrame = new JFrame();

		OpenMarkingFileDialog od=new OpenMarkingFileDialog(openfileFrame, this.getBounds(), this.backPanel.getBounds(),imageLayerPath);
		od.setVisible(true);

		File[] markingFile = od.getSelectedFiles();
		if(markingFile != null && markingFile.length==1) {// onlyy 1 marking file is allowed
			if(isMarkingsForAll)
				addMarkingsToSelectedImageLayer(markingFile[0], null); // setting markins to all ImageLayers
			else
				addMarkingsToSelectedImageLayer(markingFile[0], imageLayerPath);
		}
	}



	/**
	 *  Refreshes the imageLayerList by going throw array of ImageLayers (+ MarkingLayer) -> creating JPanels and adding
	 *  them on imageScrollPanel (IMAGE LIST)
	 */
	private void updateImageList(){
		try {
			//boolean foundMarkingLayer=false;
			// remove all components
			if(imageScrollPanel.getComponentCount()>0)
				imageScrollPanel.removeAll();
			// add new components to list
			if(dialogImageLayerList.size()>0){
				// set buttons able to use if at least one image path is selected
				this.createImageLayersJButton.setEnabled(true);
				this.createImageLayersJButton.setSelected(true);
				


				String longestPathString="";
				String longestMarkingNameString="";
				Iterator<ImageLayer> iIterator = dialogImageLayerList.iterator();
				while(iIterator.hasNext()){
					ImageLayer im = (ImageLayer)iIterator.next();
					if(im.getImageFilePath().length() > longestPathString.length()){
						longestPathString= im.getImageFilePath();
					}
					if(im.getMarkingLayers() != null && im.getMarkingLayers().size()>0){
					//	foundMarkingLayer=true;
						Iterator<MarkingLayer> mIterator = im.getMarkingLayers().iterator();
						while(mIterator.hasNext()){
							MarkingLayer ma= (MarkingLayer)mIterator.next();
							if(ma.getLayerName().length() > longestMarkingNameString.length()){
								longestMarkingNameString = ma.getLayerName();
							}
						}
					}

				}

				JLabel lab = new JLabel(longestPathString); // test JLabel for counting width of string of different fonts
				int maxStringWidth=0;
				int maxMarkingStringWidth=0;
				maxStringWidth = lab.getFontMetrics(Fonts.b16).stringWidth(longestPathString);
				lab.setText(longestMarkingNameString);
				maxMarkingStringWidth=lab.getFontMetrics(Fonts.p15).stringWidth(longestMarkingNameString);
				if(maxMarkingStringWidth > maxStringWidth)
					maxStringWidth = maxMarkingStringWidth+100;

				imageScrollPanel.setPreferredSize(new Dimension(maxStringWidth+100,getHeightOfAllImageAndMarkingPanels()+10));
				Iterator<ImageLayer> iIterator2 = dialogImageLayerList.iterator();
				while(iIterator2.hasNext()){
					ImageLayer im = (ImageLayer)iIterator2.next();
					imageScrollPanel.add(createImagePanel(im));
				}
			}
			else{
				// set button unable to use if no no image paths selected
				this.createImageLayersJButton.setEnabled(false);
				this.createImageLayersJButton.setSelected(false);
				setImportAllowedImageDimension(null);


				// adjust preferable size of ScrollPane
				//scrollPanelImage.setPreferredSize(scrollPanelImage.getMinimumSize());
			}

			imageScrollPanel.revalidate();
			imageScrollingPane.revalidate();
			imageScrollingPane.repaint();

		} catch (Exception e) {
			LOGGER.severe("Error in updating IMAGE LIST " +e.getClass().toString() + " :" +e.getMessage());

		}

	}


	public void setImportAllowedImageDimension(Dimension importAllowedImageDimension) {
		this.importAllowedImageDimension = importAllowedImageDimension;
	}




	private int getHeightOfAllImageAndMarkingPanels(){
		try {
			int heightValue=0;
			Iterator<ImageLayer> iIterator = dialogImageLayerList.iterator();
			while(iIterator.hasNext()){
				ImageLayer im = (ImageLayer)iIterator.next();
				if(im != null && im.getImageFileName().length()>0){
					heightValue+=45;
				}
				if(im.getMarkingLayers() != null && im.getMarkingLayers().size()>0){
				//	foundMarkingLayer=true;
					Iterator<MarkingLayer> mIterator = im.getMarkingLayers().iterator();
					while(mIterator.hasNext()){
						MarkingLayer ma= (MarkingLayer)mIterator.next();
						if(ma != null && ma.getLayerName().length() > 0){
							heightValue+=40;
						}
					}
				}

			}
			return heightValue;
		} catch (Exception e) {
			LOGGER.severe("Error in calculating height of ImageAndMarkingPanel " +e.getClass().toString() + " :" +e.getMessage());
			return -1;
		}
	}

	/**
	 * MarkingPanel is a JPanel which contains title of ImageLayer and list of titles of MarkingLayers. This panel is added to MarkingPanelList.
	 * @author Antti Kurronen
	 *
	 */
	private class ImageAndMarkingPanel extends JPanel{
		private String path; // image path
		private ArrayList<MarkingLayer> markingLayerList;
		private JButton closeJButton;
		private JButton browseJButton;
		private int oneImageTitleHeight=45;
		private int oneMarkingHeight=40;

		private ImageAndMarkingPanel(ImageLayer imageLayer){
			this.path = imageLayer.getImageFilePath();
			this.markingLayerList=imageLayer.getMarkingLayers();
			try {
	//			LOGGER.fine("markinglayerlist: " +this.markingLayerList.size());
				int markingListHeight =0;
				if(this.markingLayerList !=null && this.markingLayerList.size()>0)
					markingListHeight = oneMarkingHeight*this.markingLayerList.size();
				this.setMaximumSize(new Dimension(3000,oneImageTitleHeight+markingListHeight));
				this.setPreferredSize(new Dimension(400,oneImageTitleHeight+markingListHeight));
				this.setBackground(Color_schema.dark_30);
				this.setLayout(new BorderLayout());
				this.setBorder(BorderFactory.createLineBorder(Color_schema.dark_100, 1));

				// Set image title panel
				JPanel titlePanel = new JPanel();
				titlePanel.setMaximumSize(new Dimension(2000,oneImageTitleHeight));
				titlePanel.setPreferredSize(new Dimension(400,oneImageTitleHeight));
				titlePanel.setBackground(Color_schema.dark_40);
				titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.LINE_AXIS));
				// title part (path)
				JLabel iPathLabel = new JLabel(this.path);
				Font consolas16= new Font("Consolas", Font.BOLD,16);
				JPanel iPathLabelPanel = new JPanel();
				iPathLabelPanel.setLayout(new BorderLayout());
				iPathLabelPanel.setMaximumSize(new Dimension(2000, oneImageTitleHeight));
				iPathLabel.setFont(consolas16);
				iPathLabel.setForeground(Color_schema.white_230);
				int labelwidth=iPathLabel.getFontMetrics(consolas16).stringWidth(iPathLabel.getText());
			//	LOGGER.fine("font metricz" + labelwidth );
				titlePanel.setPreferredSize(new Dimension(labelwidth+100,oneImageTitleHeight));
				iPathLabelPanel.setPreferredSize(new Dimension(labelwidth+20,oneImageTitleHeight));

				this.setPreferredSize(new Dimension(labelwidth+100,oneImageTitleHeight+markingListHeight));
				iPathLabelPanel.add(iPathLabel);
				iPathLabelPanel.setBackground(Color_schema.dark_40);


				titlePanel.add(Box.createRigidArea(new Dimension(10,0)));
				titlePanel.add(iPathLabelPanel);
				titlePanel.add(Box.createHorizontalGlue()); // when panel gets bigger the horizontal extra space comes between title and deleting button


				// setup button for deleting this Image Layer
				JPanel buttonsImagePanel = new JPanel();
				buttonsImagePanel.setLayout(new BoxLayout(buttonsImagePanel, BoxLayout.LINE_AXIS));
				buttonsImagePanel.setMaximumSize(new Dimension(60,40));
				buttonsImagePanel.setPreferredSize(new Dimension(60,40));
				buttonsImagePanel.setMinimumSize(new Dimension(60,40));
				buttonsImagePanel.setBackground(Color_schema.dark_40);
		//		closeJButton = new JButton(getImageIcon("/images/close_25.png"));
				closeJButton = new JButton(new BigCloseIcon(false));
				closeJButton.setAlignmentY(Component.CENTER_ALIGNMENT);
				closeJButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
				closeJButton.setPreferredSize(new Dimension(25,25));
				closeJButton.setMaximumSize(new Dimension(25,25));
				closeJButton.setMargin(new Insets(0, 0,0, 0));
				closeJButton.setBorder(BorderFactory.createLineBorder(Color_schema.dark_40, 1));
				closeJButton.setContentAreaFilled(false);
				closeJButton.setFocusable(false);
				closeJButton.setToolTipText("Remove image: " +imageLayer.getImageFileName());

				// set up listener for closeButton
				addMouseListenerToButtons(closeJButton, ID.BUTTON_CLOSE_25);
				initActionsToButtons(closeJButton, ID.DELETE_IMAGELAYER);

				browseJButton = new JButton(new BigMarkingIcon(false));
				browseJButton.setAlignmentY(Component.CENTER_ALIGNMENT);
				browseJButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
				browseJButton.setPreferredSize(new Dimension(25,25));
				browseJButton.setMaximumSize(new Dimension(25,25));
				browseJButton.setMargin(new Insets(0, 0,0, 0));
				browseJButton.setBorder(BorderFactory.createLineBorder(Color_schema.dark_40, 1));
			//	browseJButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color_schema.color_grey_100, Color_schema.color_dark_50_bg));
				browseJButton.setContentAreaFilled(false);
				browseJButton.setFocusable(false);
				browseJButton.setToolTipText("Import Markings for image "+imageLayer.getImageFileName());
				addMouseListenerToButtons(browseJButton, ID.BUTTON_IMPORT_MARKINGS);
				initActionsToButtons(browseJButton, ID.OPEN_MARKING_FILE);


				buttonsImagePanel.add(browseJButton);
				buttonsImagePanel.add(Box.createRigidArea(new Dimension(5,0)));
				buttonsImagePanel.add(closeJButton);
				iPathLabel.validate();
				titlePanel.add(buttonsImagePanel);


				// add panels containing marking titles

				JPanel markingTableJPanel = new JPanel();
				markingTableJPanel.setLayout(new BoxLayout(markingTableJPanel, BoxLayout.PAGE_AXIS));
				markingTableJPanel.setMaximumSize(new Dimension(2000,oneMarkingHeight*markingLayerList.size()));
				markingTableJPanel.setPreferredSize(new Dimension(400,oneMarkingHeight*markingLayerList.size()));
				markingTableJPanel.setMinimumSize(new Dimension(400,oneMarkingHeight*markingLayerList.size()));
				markingTableJPanel.setBackground(Color_schema.dark_40);

				// Create and add markingpanels
				if(markingLayerList != null && markingLayerList.size()>0){
					Iterator<MarkingLayer> iIterator = markingLayerList.iterator();
					while(iIterator.hasNext()){
						MarkingLayer ml = (MarkingLayer)iIterator.next();
						if(ml.getLayerName().length()>0){ // MarkingLayer name
							// add markingTitles to markingTable
							markingTableJPanel.add(new SingleMarking(this.path, ml.getLayerName()));
						}

					}
				}


				this.add(titlePanel,BorderLayout.PAGE_START);
				this.add(markingTableJPanel,BorderLayout.CENTER);
				this.validate();


			} catch (Exception e) {
				LOGGER.severe("Error in construction of Marking list " +e.getClass().toString() + " :" +e.getMessage());
				
			}
		}

		public String getPath() {
			return path;
		}



	}


	private class SingleMarking extends JPanel{
		private String markingName;
		private String imageLayerPath;

		private SingleMarking(String imageLayerPath, String markingName){
			this.setMarkingName(markingName);
			this.setImageLayerPath(imageLayerPath);

			try {

				this.setMaximumSize(new Dimension(2000,45));
				this.setPreferredSize(new Dimension(400,45));
				this.setBackground(Color_schema.dark_40);
				this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
				// marking title
				JLabel markingLabel = new JLabel(markingName);
				Font consolas15= new Font("Consolas", Font.PLAIN,15);
				JPanel markingLabelPanel = new JPanel();
				markingLabelPanel.setLayout(new BorderLayout());
				markingLabelPanel.setMaximumSize(new Dimension(2000, 40));
				markingLabel.setFont(consolas15);
				markingLabel.setForeground(Color_schema.white_230);
				int markinglabelwidth=markingLabel.getFontMetrics(consolas15).stringWidth(markingLabel.getText());

				markingLabelPanel.setPreferredSize(new Dimension(markinglabelwidth,40));
				markingLabelPanel.add(markingLabel);
				markingLabelPanel.setBackground(Color_schema.dark_40);


				this.add(Box.createRigidArea(new Dimension(10,0)));
				this.add(markingLabelPanel, BorderLayout.CENTER);
				this.add(Box.createHorizontalGlue()); // when panel gets bigger the horizontal extra space comes between title and deleting button


				// setup button for deleting this Image Layer
				JPanel removeMarkingPanel = new JPanel();
				removeMarkingPanel.setLayout(new BoxLayout(removeMarkingPanel, BoxLayout.LINE_AXIS));
				removeMarkingPanel.setMaximumSize(new Dimension(25,30));
				removeMarkingPanel.setPreferredSize(new Dimension(25,30));
				removeMarkingPanel.setMinimumSize(new Dimension(25,30));
				removeMarkingPanel.setBackground(Color_schema.dark_40);
				JButton deleteMarkingJButton = new JButton(new MediumCloseIcon(false));
				deleteMarkingJButton .setAlignmentY(Component.CENTER_ALIGNMENT);
				deleteMarkingJButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
				deleteMarkingJButton.setPreferredSize(new Dimension(20,20));
				deleteMarkingJButton.setMaximumSize(new Dimension(20,20));
				deleteMarkingJButton.setMargin(new Insets(0, 0,0, 0));
				deleteMarkingJButton.setBorder(BorderFactory.createLineBorder(Color_schema.button_grey_border, 1));
				deleteMarkingJButton.setContentAreaFilled(false);
				deleteMarkingJButton.setFocusable(false);
				deleteMarkingJButton.setToolTipText("Remove MarkingLayer: " +markingLabel.getText());

				// set up listener for closeButton
			//	addMouseListenerToButtons(deleteMarkingJButton, ID.BUTTON_CLOSE_BIG);
				MouseListenerCreator.addMouseListenerToMediumCloseButtons(deleteMarkingJButton);
				initActionsToButtons(deleteMarkingJButton, ID.DELETE_MARKINGLAYER);

				this.add(deleteMarkingJButton);


			} catch (Exception e) {
				// TODO Auto-generated catch block
				LOGGER.severe("Error in construction of SingleMarking " +e.getClass().toString() + " :" +e.getMessage());
		}

	}

		public String getImageLayerPath() {
			return imageLayerPath;
		}

		public String getMarkingName() {
			return markingName;
		}

		public void setImageLayerPath(String imageLayerPath) {
			this.imageLayerPath = imageLayerPath;
		}

		public void setMarkingName(String markingName) {
			this.markingName = markingName;
		}


	}



}
