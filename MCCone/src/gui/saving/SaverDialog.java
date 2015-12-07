package gui.saving;

import gui.Color_schema;
import gui.ContentPane;
import gui.GUI;
import gui.MouseListenerCreator;
import gui.ShadyMessageDialog;
import gui.WindowLocator;
import gui.file.FileManager;
import information.Fonts;
import information.ID;
import information.ImageLayer;
import information.MarkingLayer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

public class SaverDialog extends JDialog{


protected GUI gui;
protected JPanel backPanel;
private JButton cancelJButton;
protected JPanel imageScrollPanel;
protected ArrayList<ImageLayer> imageLayerList;
private JScrollPane imageScrollingPane;
protected JButton selectFileJButton;
private boolean allSelected=true;
final static Logger LOGGER = Logger.getLogger("MCCLogger");
private JButton selectAllMarkingsJButton;
protected boolean notInformedSuccessfullSaving;
public String exportingPath="";
private JLabel titleLabel;
protected int savingType; // ID.SAVE_MARKINGS, ID.EXPORT_IMAGE, ID.FILE_TYPE_TEXT_FILE, ID.FILE_TYPE_CSV or ID.CLIPBOARD
private int fileWritingType; // ID.FILE_NEW_FILE, ID.OVERWRITE, ID.APPEND
protected int oneImageTitleHeight;
protected int oneImagePathHeight;
protected int oneMarkingHeight;
protected SelectFileDialog selectFileDialog;
protected JButton saveJButton;
private Component parentComponent=null;



	public SaverDialog(JFrame frame, GUI gui, ArrayList<ImageLayer> iList, int savingTypeID){

		super(frame, true);
		try{
			this.savingType=savingTypeID;
			this.parentComponent=frame;
			setPanelHeights();
			notInformedSuccessfullSaving=true;
			this.imageLayerList =  iList;
			this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			this.gui=gui;
			initComponents();
			initKeyListenerToDialog();
		//	initSelectFileDialog();
			setImageList();
			this.revalidate();

			this.setVisible(true);



		} catch (Exception e) {

			LOGGER.severe("Error in saving Markings:  " +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
			e.printStackTrace();
		}
	}
	
	public SaverDialog(JDialog d, GUI gui, ArrayList<ImageLayer> iList, int savingTypeID){

		super(d, true);
		try{
			this.savingType=savingTypeID;
			this.parentComponent=d;
			setPanelHeights();
			notInformedSuccessfullSaving=true;
			this.imageLayerList =  iList;
			this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			this.gui=gui;
			initComponents();
			initKeyListenerToDialog();
		//	initSelectFileDialog();
			setImageList();
			this.revalidate();

			this.setVisible(true);

		} catch (Exception e) {

			LOGGER.severe("Error in saving Markings:  " +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
			e.printStackTrace();
		}
	}

	private void initComponents() throws Exception{
		
		this.setBounds(WindowLocator.getVisibleWindowBounds(this.parentComponent));
	//	LOGGER.fine("this class: "+this.getClass().toString()+ "this bounds: " +this.getBounds());
		this.setUndecorated(true);
		this.setBackground(new Color(0,0,0,0));
		this.setContentPane(new ContentPane());
		this.getContentPane().setBackground(Color_schema.dark_30);
		this.getContentPane().setLayout(new GridBagLayout());

		backPanel = new JPanel();
		backPanel.setBackground(Color_schema.dark_30);
		backPanel.setLayout(new BorderLayout());

		backPanel.setBorder(BorderFactory.createLineBorder(Color_schema.button_light_border, 5));
		backPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		backPanel.setAlignmentY(JComponent.CENTER_ALIGNMENT);
		backPanel.setMaximumSize(new Dimension((int)(this.getBounds().getWidth()*0.95), (int)(this.getBounds().getHeight()*0.95)));
		backPanel.setMinimumSize(new Dimension((int)(this.getBounds().getWidth()*0.5), (int)(this.getBounds().getHeight()*0.5)));
		backPanel.setPreferredSize(new Dimension((int)(this.getBounds().getWidth()*0.6), (int)(this.getBounds().getHeight()*0.6)));
		// too small size -> make bigger
		if(backPanel.getPreferredSize().getWidth()<800 || backPanel.getPreferredSize().getHeight() <500)
			backPanel.setPreferredSize(new Dimension((int)(this.getBounds().getWidth()*0.95), (int)(this.getBounds().getHeight()*0.95)));

		backPanel.add(initImageViewPanelWithTitle(), BorderLayout.PAGE_START);
		JPanel browsingPanel= initBrowsingPanelWithLabel();
		if(browsingPanel != null)
		backPanel.add(browsingPanel, BorderLayout.CENTER);
		backPanel.add(initDownPanel(),BorderLayout.PAGE_END);

		this.add(backPanel);
		this.validate();
		this.repaint();
	}

	protected JPanel initImageViewPanelWithTitle(){
		return initImageViewPanel("Title");
	}

	protected JPanel initBrowsingPanelWithLabel() throws Exception{
		return initBrowsingPanel(this.savingType, "Browse");
	}

	public boolean hasImageLayersFound(File file){
		return true; // write code in extended class
	}


	public String getPresentFolder(){
		return gui.getPresentFolder();
	}

	public void setPresentFolder(String folder){
		gui.setPresentFolder(folder);
	}

	private void setPanelHeights(){
		oneImageTitleHeight=45;
		oneMarkingHeight=40;
		if(isFilePathPanelShown())
			oneImagePathHeight=30;
		else
			oneImagePathHeight=0;

	}

	protected void initSelectFileDialog() throws Exception{
		String path = gui.getPresentFolder();
		if(path== null)
			path = System.getProperty("user.home");
		this.selectFileDialog = new SelectFileDialog(this.gui, path, this.backPanel, this.savingType);
	}

	private void initKeyListenerToDialog(){

		InputMap inputMap= (this.backPanel).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(KeyStroke.getKeyStroke("pressed ENTER"), "enter_pressed");
		ActionMap actionMap = 	(this.backPanel).getActionMap();
		actionMap.put("enter_pressed", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				startSavingProcess(savingType, fileWritingType);
			}
		});
	}

	public boolean isBrowsePanelShown(){
		return true;
	}

	protected boolean isFilePathPanelShown(){
		return true;
	}



	/**
	 * Initializes an action for a JButton.
	 * @param button JButton where the action is added
	 * @param typeOfItem int type of action  @see information.ID
	 * @throws Exception
	 */
	protected void initActionsToButtons(JButton button, int typeOfItem) throws Exception{


						// save markings
						if(typeOfItem == ID.SAVE){
							button.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent arg0) {
									startSavingProcess(savingType, fileWritingType);
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




	public int getSavingID(){
		return this.savingType;
	}

/*
	private void addMarkingLayerIfNotExists(ImageLayer originalIL, ImageLayer copyIL){
		try {
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
							originalIL.removeMarkingLayer(maCopy); // this method is checking only names of MarkingLayers and if same -> removed
							// add new one
							originalIL.addMarkingLayer(maCopy); // add new one
						}

					}
					else{ // not found -> add MarkingLayer to original ImageLayer
						originalIL.addMarkingLayer(maCopy.makeCopy()); // for safety add copy of Markinglayer
					}
				}
			}
		} catch (HeadlessException e) {
			LOGGER.severe("Error in adding Markings to ImageLayer " +e.getClass().toString() + " :" +e.getMessage());
		}
	}

*/



	protected void cancelSelected(){
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				hideThis();

			}
		});


	}

	private void hideThis(){
		this.setVisible(false);
		this.dispose();
	}




protected SingleImagePanel createSingleImagePanel(ImageLayer layer){
	return null;//new SingleImagePanel(layer,this);
}




private JPanel initDownPanel(){

	try {
		// SETUP buttons of lowest part of tab
		JPanel downButtonPanel =new JPanel();
		downButtonPanel.setLayout(new BoxLayout(downButtonPanel, BoxLayout.LINE_AXIS));
		downButtonPanel.setBackground(Color_schema.dark_30);
		downButtonPanel.setMinimumSize(new Dimension((int)backPanel.getMinimumSize().getWidth(), 50));
		downButtonPanel.setPreferredSize(new Dimension(100,50));
		downButtonPanel.setMaximumSize(new Dimension(100,50));
		JPanel cancelJpanel = new JPanel();
		cancelJpanel.setLayout(new BorderLayout());
		cancelJpanel.setPreferredSize(new Dimension(150,50));
		cancelJpanel.setMaximumSize(new Dimension(100,30));
		cancelJButton = new JButton("CLOSE");
//		cancelJButton.setContentAreaFilled(false);
		cancelJButton.setFocusable(false);
		cancelJButton.setForeground(Color_schema.orange_dark);
		initActionsToButtons(cancelJButton, ID.CANCEL);	// the action
		MouseListenerCreator.addMouseListenerToCancelButtons(cancelJButton); // changes the colors of button when pressed

		cancelJButton.setPreferredSize(new Dimension(100,50));
		cancelJpanel.add(cancelJButton);
		downButtonPanel.add(Box.createRigidArea(new Dimension(20,0)));
		downButtonPanel.add(initSaveButtonWithTitle());
		downButtonPanel.add(Box.createHorizontalGlue());
		downButtonPanel.add(cancelJpanel);
		downButtonPanel.add(Box.createRigidArea(new Dimension(20,0)));
		return downButtonPanel;

	} catch (Exception e) {
		LOGGER.severe("Error in creating buttons of open images windown:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
		return null;
	}

}

protected JPanel initSaveButtonWithTitle() throws Exception{
	return initSaveButton("Title");
}

public JPanel initSaveButton(String saveTitle) throws Exception{
	JPanel saveDownLayerJPanel = new JPanel();
	saveDownLayerJPanel.setLayout(new BorderLayout());
	saveDownLayerJPanel.setPreferredSize(new Dimension(220,30));
	saveDownLayerJPanel.setMaximumSize(new Dimension(220,30));
	saveJButton = new JButton(saveTitle);
	if(this.savingType==ID.CLIPBOARD || this.savingType==ID.EXPORT_PREVIEW_IMAGES)
		saveJButton.setEnabled(true);
	else
		saveJButton.setEnabled(false);

	saveJButton.setPreferredSize(new Dimension(220,30));
	saveJButton.setFocusable(false);

	initActionsToButtons(saveJButton, ID.SAVE);
	MouseListenerCreator.addMouseListenerToNormalButtons(saveJButton); // changes the colors of button when pressed
	saveDownLayerJPanel.add(saveJButton, BorderLayout.CENTER);
	return saveDownLayerJPanel;
}



	public JPanel initImageViewPanel(String title){
		// SETUP THE Panel which shows the list of images and markings
		try {
			// Panel where other panels are added
			JPanel msBackPanel = new JPanel();
		//	iBackPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
			msBackPanel.setLayout(new BorderLayout());
			msBackPanel.setBackground(Color_schema.dark_30);
			msBackPanel.setLayout(new BorderLayout());
			msBackPanel.setMaximumSize(this.getMaximumSize());
			msBackPanel.setBorder(BorderFactory.createLineBorder(Color_schema.button_light_border, 1));

			// TitlePanel
			JPanel upTitleJPanel = new JPanel();
			upTitleJPanel.setBackground(Color_schema.dark_30);
			upTitleJPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			upTitleJPanel.setMaximumSize(new Dimension(200,40));
			upTitleJPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
			upTitleJPanel.setBorder(BorderFactory.createEmptyBorder());
			titleLabel = new JLabel(title);

			titleLabel.setFont(new Font("Consolas", Font.BOLD,20));
			titleLabel.setForeground(Color_schema.white_230);
			upTitleJPanel.add(titleLabel);
			msBackPanel.add(upTitleJPanel, BorderLayout.PAGE_START);

			// CENTER PANEL
			JPanel centerPanel=new JPanel();
			centerPanel.setBackground(Color_schema.dark_30);
			centerPanel.setLayout(new BorderLayout());
			centerPanel.setBorder(BorderFactory.createEmptyBorder());
			JPanel scrollBiggerPanel = new JPanel();
			scrollBiggerPanel.setLayout(new BorderLayout());
			scrollBiggerPanel.setBorder(BorderFactory.createEmptyBorder());
			scrollBiggerPanel.setBackground(Color_schema.dark_35);

			imageScrollPanel = new JPanel();
			imageScrollPanel.setBackground(Color_schema.dark_35);
			imageScrollPanel.setMinimumSize(new Dimension(((int)(backPanel.getMinimumSize().getWidth()*0.4)), ((int)(backPanel.getMinimumSize().getHeight()*0.7)-40)));
			imageScrollPanel.setLayout(new BoxLayout(imageScrollPanel, BoxLayout.PAGE_AXIS));
			imageScrollingPane = new JScrollPane(imageScrollPanel);
			imageScrollingPane.setBorder(BorderFactory.createLineBorder(Color_schema.button_grey_border, 2));
			imageScrollingPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			imageScrollingPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			imageScrollingPane.setPreferredSize(new Dimension((int)backPanel.getPreferredSize().getWidth()-20,(int)backPanel.getPreferredSize().getHeight()-140));
			imageScrollingPane.setMaximumSize(new Dimension((int)backPanel.getPreferredSize().getWidth()-20,(int)backPanel.getPreferredSize().getHeight()-140));
			// SETUP Button and it's JPanel for adding selected pahths to list

			scrollBiggerPanel.add(imageScrollingPane, BorderLayout.CENTER);
			centerPanel.add(scrollBiggerPanel, BorderLayout.CENTER);
		//	centerPanel.add(imageButtonJPanel, BorderLayout.PAGE_END);


			msBackPanel.add(centerPanel, BorderLayout.CENTER);
		//	iBackPanel.add(centerPanel);

			return msBackPanel;
		} catch (Exception e) {
			LOGGER.severe("Error in creating imageViewPanel:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());

			return null;
		}

	}

	protected JPanel initBrowsingPanel(int savingID, String label) throws Exception{
		try {
			JPanel imageButtonJPanel = new JPanel();
			imageButtonJPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			imageButtonJPanel.setBackground(Color_schema.dark_35);
			imageButtonJPanel.setMinimumSize(new Dimension(200,50));
			imageButtonJPanel.setMaximumSize(new Dimension(200,50));

			// Select file path for all ImageLayers
			selectFileJButton = new JButton(label);
			selectFileJButton.setPreferredSize(new Dimension(250,30));
			selectFileJButton.setBackground(Color_schema.dark_20);
			initActionsToButtons(selectFileJButton, ID.OPEN_MARKING_FILE);
			selectFileJButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						selectPathForAllSingleImagePanels();
					} catch (Exception e1) {
						
						e1.printStackTrace();
					}
				}
			});
			selectFileJButton.setFocusable(false);
			MouseListenerCreator.addMouseListenerToNormalButtons(selectFileJButton);
			imageButtonJPanel.add(selectFileJButton);
			return imageButtonJPanel;
		} catch (Exception e) {
			
			LOGGER.fine("Error initializing BrowsingPanel: "+ e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	//
	/**
	 *
	 * Opens a file dialog for selecting xml file where to save Markings
	 * @param typeOfFile int ID which has two options: ID.OPEN_IMAGE_FILE or ID.OPEN_MARKING_FILE
	 * @param pathIfAny String path for image where markings are added. If selecting image file this parameter is null.
	 * @throws Exception
	 */
	/*
	private void openFileDialog(String path, int savingType, int fileType){

			JFrame openfileFrame = new JFrame();
			// create the dialog for

			SelectFileDialog open_dialog = new SelectFileDialog(openfileFrame, path, savingType, fileType, this);
			open_dialog.setVisible(true);

	}

*/

	protected void selectPathForAllSingleImagePanels() throws Exception{
		// write code in extended class
	}

	protected void selectPathForSingleImagePanel(String properPath, int panelID) throws Exception{
		// write code in extended class

		/*	if(this.selectFileDialog == null)
			initSelectFileDialog();
		this.selectFileDialog.setProperFilePathForSaving(properPath);
		this.selectFileDialog.setVisible(true);

	//	if(fileWritingType != ID.CANCEL && fileWritingType != ID.ERROR){

			setFilePathForSingleImagePanel(this.selectFileDialog.getSelectedFilePath(), panelID);

	//	}
			this.selectFileDialog.setVisible(false);
		this.selectFileDialog.dispose();
*/
	}
											// filefilters are null!
	protected int checkFileValidity(File file){
		return FileManager.checkFile(file, SelectFileDialog.createFileFilterList(this.savingType), this.savingType==ID.SAVE_MARKINGS);
	}



	/**
	 *  Refreshes the imageLayerList by going throw array of ImageLayers (+ MarkingLayer) -> creating JPanels and adding
	 *  them on imageScrollPanel (IMAGE LIST)
	 */
	private void setImageList(){
		try {
			//boolean foundMarkingLayer=false;
			// remove all components
			if(imageScrollPanel.getComponentCount()>0)
				imageScrollPanel.removeAll();
			// add new components to list
			if(imageLayerList.size()>0){
				// set buttons able to use if at least one image path is selected



				String longestPathString="";
				String longestMarkingNameString="";
				Iterator<ImageLayer> iIterator = imageLayerList.iterator();
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
				Iterator<ImageLayer> iIterator2 = imageLayerList.iterator();
				while(iIterator2.hasNext()){
					ImageLayer im = (ImageLayer)iIterator2.next();
					imageScrollPanel.add(createSingleImagePanel(im));
				}
			}


		//	imageScrollPanel.revalidate();
		//	imageScrollingPane.revalidate();
		//	imageScrollingPane.repaint();
			this.repaint();

		} catch (Exception e) {
			LOGGER.severe("Error in updating IMAGE LIST " +e.getClass().toString() + " :" +e.getMessage());

		}

	}
	private int getHeightOfAllImageAndMarkingPanels(){
		try {
			int heightValue=0;
			Iterator<ImageLayer> iIterator = imageLayerList.iterator();
			while(iIterator.hasNext()){
				ImageLayer im = (ImageLayer)iIterator.next();
				if(im != null && im.getImageFileName().length()>0){
					heightValue+=(this.oneImageTitleHeight+this.oneImagePathHeight);
				}
				if(im.getMarkingLayers() != null && im.getMarkingLayers().size()>0){
				//	foundMarkingLayer=true;
					Iterator<MarkingLayer> mIterator = im.getMarkingLayers().iterator();
					while(mIterator.hasNext()){
						MarkingLayer ma= (MarkingLayer)mIterator.next();
						if(ma != null && ma.getLayerName().length() > 0){
							heightValue+=this.oneMarkingHeight;
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


	protected void setFilePathsForAll(String path){
		//go through all panels and set path
		int fileValidity = checkFileValidity(new File(path));
		Component[] imPanelList= imageScrollPanel.getComponents();
		boolean notShowedMessage=true;
		if(imPanelList != null && imPanelList.length>0 && path != null && path.length()>0){

			 for (int i = 0; i < imPanelList.length; i++) {
				((SingleImagePanel)imPanelList[i]).setProperFileForMarkings(path,notShowedMessage, fileValidity);
				((SingleImagePanel)imPanelList[i]).repaint();
				if(notShowedMessage)
					notShowedMessage=false;
			}
		}

	}

	protected void setFilePathForSingleImagePanel(String path, int panelID){

		int fileValidity = checkFileValidity(new File(path));

		//go through all panels and set path
		Component[] imPanelList= imageScrollPanel.getComponents();

		if(imPanelList != null && imPanelList.length>0 && path != null && path.length()>0){

			 for (int i = 0; i < imPanelList.length; i++) {
				if( ((SingleImagePanel)imPanelList[i]).getLayerID()==panelID){
					((SingleImagePanel)imPanelList[i]).setProperFileForMarkings(path,true, fileValidity);
					((SingleImagePanel)imPanelList[i]).repaint();
					return;
				}

			}
		}

	}

	protected void setSaveButtonEnabledByFileValidity(int validityID){
		// implement in extended class
	}

	protected String getfirstProperPathOfSingleImagePanels(){
		//go through all panels and set path
		Component[] imPanelList= imageScrollPanel.getComponents();

		if(imPanelList != null && imPanelList.length>0){

			 for (int i = 0; i < imPanelList.length; i++) {
				if(((SingleImagePanel)imPanelList[i]).hasSelectedMarkingLayer() && ((SingleImagePanel)imPanelList[i]).getProperFilePathForSaving() != null){
					return ((SingleImagePanel)imPanelList[i]).getProperFilePathForSaving();
				}


			}
		}
		return System.getProperty("user.home");

	}

	public void informUserFromFileValidity(int fileValidity, JLabel pathLabel, boolean showMessage){
		setSaveButtonEnabledByFileValidity(fileValidity);
		if(fileValidity == ID.FILE_OK || fileValidity == ID.FILE_NEW_FILE ){ // can update file or it is new file
			pathLabel.setForeground(Color_schema.white_230);
			pathLabel.setToolTipText("File path OK.");

		}
		else{
			pathLabel.setForeground(Color_schema.orange_dark);
			pathLabel.setToolTipText("Can't save to this file name. Select or create new file by pressing Browse-button.");

			if(showMessage)		{
				String type="";
				switch (fileValidity) {
				case ID.FILE_CANT_READ:
					type="Can't read file! Select other file for saving.";
					break;
				case ID.FILE_NOT_VALID:
					type="File is not valid XML file! Select other file for saving.";
					break;
				case ID.FILE_CANT_WRITE:
					type="Can't write to file! Select other file for saving.";
					break;
				case ID.FILE_NOT_RIGHT_FORMAT:
					type="FILE is not right format! Select other file for saving.";
					break;
				case ID.FILE_NOT_EXISTS:
					type="Can't create the file! Change the filename or folder.";
					break;
				case ID.FILE_IS_NOT_FILE:
					type="Only Folder path is given. Give file name";
					break;

				default:
					type="Can't read or write to file! Change the file for saving."; // this should never be shown.
					break;
				}


					ShadyMessageDialog dialog = new ShadyMessageDialog(new JFrame(), "Invalid file", ""+type, ID.OK, this);
					dialog.showDialog();
					dialog=null;
				}
		}
	}

	public void setSuccesfullSavingBackgrounds(ArrayList<Integer> successfullySavedMarkingPanelIDs){
		//go through all panels and set path
		Component[] imPanelList= imageScrollPanel.getComponents();
		if(imPanelList != null && imPanelList.length>0 && successfullySavedMarkingPanelIDs != null && successfullySavedMarkingPanelIDs.size()>0){

			 for (int i = 0; i < imPanelList.length; i++) {
				((SingleImagePanel)imPanelList[i]).setSuccessfullSavingColor(successfullySavedMarkingPanelIDs);
				((SingleImagePanel)imPanelList[i]).repaint();
			}
		}

	}





	protected void setMarkingLayerBackgroundsToDefault(int iLayerID){
		//go through all panels and set path
		Component[] imPanelList= imageScrollPanel.getComponents();
		if(imPanelList != null && imPanelList.length>0){

			 for (int i = 0; i < imPanelList.length; i++) {
				 SingleImagePanel sip =(SingleImagePanel)imPanelList[i];
				 if(iLayerID== ID.IMAGELAYER_UNDEFINED || iLayerID==sip.getLayerID()){
					 sip.setAllSingleMarkingPanelBGstoDefault();
					 sip.repaint();
				 }
			}
		}

	}

	protected void setAllMarkingLayerBackgroundsToDefault(){
		//go through all panels and set path
		Component[] imPanelList= imageScrollPanel.getComponents();
		if(imPanelList != null && imPanelList.length>0){

			 for (int i = 0; i < imPanelList.length; i++) {
				 SingleImagePanel sip =(SingleImagePanel)imPanelList[i];

					 sip.setAllSingleMarkingPanelBGstoDefault();
					 sip.repaint();

			}
		}

	}






	protected void startSavingProcess(int savingID, int exportType){
		// overwrite this method in extended class
	}


	public Rectangle getMarkingSaverBounds(){
		return this.getBounds();
	}

	protected void setFileWritingType(int t){
		this.fileWritingType=t;
	}
















}


