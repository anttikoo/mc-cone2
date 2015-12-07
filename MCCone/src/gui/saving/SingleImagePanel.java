package gui.saving;

import gui.Color_schema;
import gui.MouseListenerCreator;
import gui.ShadyMessageDialog;
import gui.file.FileManager;
import information.Fonts;
import information.ID;
import information.ImageLayer;
import information.MarkingLayer;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import operators.CheckBoxIcon;

public class SingleImagePanel extends JPanel{
	final static Logger LOGGER = Logger.getLogger("MCCLogger");
	private String imagePath;
	private ArrayList<MarkingLayer> markingLayerList;
	private JButton saveSingleJButton;
	private JButton browseJButton;
	private int oneImageTitleHeight;
	private int oneImagePathHeight;
	private int oneMarkingHeight;
	private String properFilePath;
	protected JPanel markingTableJPanel;
	private JLabel filePathLabelValue;
	private int fileValidity;
	protected ImageLayer imageLayer;
	private SaverDialog saverDialog;
	private JCheckBox saveImageCheckBox;



	

	public SingleImagePanel(ImageLayer imageLayer, SaverDialog saverDialog){
		try {
			this.saverDialog=saverDialog;
			initIAMPcomponents(imageLayer);


		} catch (Exception e) {
			LOGGER.severe("Error in construction of Marking list " +e.getClass().toString() + " :" +e.getMessage());
			e.printStackTrace();

		}
	}
	
	public boolean isSelected(){
		return this.saveImageCheckBox.isSelected();
	}

	public void setSelected(boolean selected){
		this.saveImageCheckBox.setSelected(selected);
		this.repaint();
	}

	private void setHeights(){
		this.oneImageTitleHeight=this.saverDialog.oneImageTitleHeight;
		this.oneImagePathHeight=this.saverDialog.oneImagePathHeight;
		this.oneMarkingHeight=this.saverDialog.oneMarkingHeight;
	}

	private void  initIAMPcomponents(ImageLayer imageLayer) throws Exception{
		setHeights(); // set the heights for panels
		this.imageLayer=imageLayer;
		this.imagePath = imageLayer.getImageFilePath();
		this.markingLayerList=imageLayer.getMarkingLayers();
		this.properFilePath=initProperFilePathForSaving(imageLayer);
		this.fileValidity=ID.FILE_OK;

		int markingListHeight =0;
		if(this.markingLayerList !=null && this.markingLayerList.size()>0)
			markingListHeight = oneMarkingHeight*this.markingLayerList.size();
		this.setMaximumSize(new Dimension(3000,oneImageTitleHeight+oneImagePathHeight+markingListHeight));
		this.setPreferredSize(new Dimension(400,oneImageTitleHeight+oneImagePathHeight+markingListHeight));
		this.setBackground(Color_schema.dark_35);
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createLineBorder(Color_schema.dark_100, 1));

		// panel for image information
		JPanel imageInformationJPanel= new JPanel();
		imageInformationJPanel.setMaximumSize(new Dimension(2000,oneImagePathHeight+oneImageTitleHeight));
		imageInformationJPanel.setPreferredSize(new Dimension(400,oneImagePathHeight+oneImageTitleHeight));
		imageInformationJPanel.setBackground(Color_schema.dark_35);
		imageInformationJPanel.setLayout(new BorderLayout());
		imageInformationJPanel.setBorder(BorderFactory.createLineBorder(Color_schema.dark_50));
		
		
		// CheckBox
		Icon checkBoxIcon=new CheckBoxIcon();
		// checkBox for selecting
		saveImageCheckBox=new JCheckBox(checkBoxIcon);
	
		saveImageCheckBox.setSelected(true);
	//	saveCheckBox.setForeground(Color_schema.color_white_230);
		saveImageCheckBox.setBackground(Color_schema.dark_35);
		saveImageCheckBox.setMaximumSize(new Dimension(25,25));
		saveImageCheckBox.setPreferredSize(new Dimension(25,25));
		saveImageCheckBox.setMinimumSize(new Dimension(25,25));
		setListenerToImageCheckBox(); // listener to change of selections
		// Set image title panel
		JPanel titlePanel = new JPanel();
		titlePanel.setMaximumSize(new Dimension(2000,oneImageTitleHeight));
		titlePanel.setPreferredSize(new Dimension(400,oneImageTitleHeight));
		titlePanel.setBackground(Color_schema.dark_35);
		titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.LINE_AXIS));
		// title part (path)
		JLabel imageTitle = new JLabel(this.imagePath);
		imageTitle.setFont(Fonts.b18);
		imageTitle.setForeground(Color_schema.white_230);
		int labelwidth=imageTitle.getFontMetrics(Fonts.b18).stringWidth(imageTitle.getText());

		titlePanel.setPreferredSize(new Dimension(labelwidth+100,oneImageTitleHeight));
		titlePanel.add(Box.createRigidArea(new Dimension(10,0)));
		titlePanel.add(this.saveImageCheckBox);
		titlePanel.add(imageTitle);

	//	imageTitle.validate();
		imageInformationJPanel.add(titlePanel,BorderLayout.PAGE_START);

		JPanel filePathPanel = initFilePathPanelWithLabel();

		if(filePathPanel != null)
		imageInformationJPanel.add(initFilePathPanelWithLabel(), BorderLayout.CENTER);


		// add panels containing marking titles

		markingTableJPanel = new JPanel();
		markingTableJPanel.setLayout(new BoxLayout(markingTableJPanel, BoxLayout.PAGE_AXIS));
		markingTableJPanel.setMaximumSize(new Dimension(2000,oneMarkingHeight*markingLayerList.size()));
		markingTableJPanel.setPreferredSize(new Dimension(400,oneMarkingHeight*markingLayerList.size()));
		markingTableJPanel.setMinimumSize(new Dimension(400,oneMarkingHeight*markingLayerList.size()));
		markingTableJPanel.setBackground(Color_schema.dark_35);

		// Create and add markingpanels
		if(markingLayerList != null && markingLayerList.size()>0){
			Iterator<MarkingLayer> iIterator = markingLayerList.iterator();
			while(iIterator.hasNext()){
				MarkingLayer ml = (MarkingLayer)iIterator.next();
				if(ml.getLayerName().length()>0){ // MarkingLayer name
					// add markingTitles to markingTable
					markingTableJPanel.add(createSingleMarkingPanel(ml));
				}

			}
		}
		
	

		this.setPreferredSize(new Dimension(labelwidth+100,oneImageTitleHeight+oneImagePathHeight+markingListHeight));

		this.add(imageInformationJPanel,BorderLayout.PAGE_START);
		this.add(markingTableJPanel,BorderLayout.CENTER);
		this.validate();

	//	if(savingType != ID.EXPORT_RESULTS)
		setProperFileForMarkings(this.properFilePath, false, ID.UNDEFINED);
	}
	
	private void setListenerToImageCheckBox(){
		this.saveImageCheckBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				
				setAllMarkingLayerSelections(((JCheckBox)e.getSource()).isSelected());
				
			}
		});
		
	}

	protected JPanel initFilePathPanelWithLabel() throws Exception{
		return initFilePathPanel("Browse");
	}



	protected SingleMarkingPanel createSingleMarkingPanel(MarkingLayer markingLayer) throws Exception{
		return new SingleMarkingPanel(markingLayer);
	}



	protected JPanel initFilePathPanel(String buttonTitle) throws Exception{
		// setup Panel for showing and selecting path of file
		JPanel filePathJPanel = new JPanel();
		filePathJPanel.setLayout(new BoxLayout(filePathJPanel, BoxLayout.LINE_AXIS));
		filePathJPanel.setMaximumSize(new Dimension(2000,oneImagePathHeight));
		filePathJPanel.setPreferredSize(new Dimension(400,oneImagePathHeight));

		filePathJPanel.setBackground(Color_schema.dark_35);
		filePathJPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color_schema.dark_50));


		// create JLabels for path
		JLabel filePathLabel = new JLabel("Save to file:");
	//	Font consolas16= new Font("Consolas", Font.BOLD,16);
		filePathLabel.setFont(Fonts.p16);
		filePathLabel.setForeground(Color_schema.white_230);

	//	filePathLabelValue = new JLabel(properFileForMarkings);
		filePathLabelValue = new JLabel();

		filePathLabelValue.setFont(Fonts.p16);
		filePathLabelValue.setForeground(Color_schema.white_230);
		int labelwidthXML=filePathLabelValue.getFontMetrics(Fonts.b16).stringWidth(filePathLabelValue.getText());

		filePathJPanel.setPreferredSize(new Dimension(labelwidthXML+40,oneImagePathHeight));

		filePathJPanel.add(Box.createRigidArea(new Dimension(10,0)));
		filePathJPanel.add(filePathLabel);
		filePathJPanel.add(Box.createRigidArea(new Dimension(10,0)));
		filePathJPanel.add(filePathLabelValue);
		filePathJPanel.add(Box.createHorizontalGlue());

		browseJButton = new JButton(buttonTitle);
		browseJButton.setFont(Fonts.b15);
		int maxStringWidth = browseJButton.getFontMetrics(Fonts.b15).stringWidth(buttonTitle);
		browseJButton.setPreferredSize(new Dimension(maxStringWidth+20,25));
		browseJButton.setMaximumSize(new Dimension(maxStringWidth+20,25));
		browseJButton.setBorder(BorderFactory.createLineBorder(Color_schema.button_grey_border, 1));
		browseJButton.setContentAreaFilled(false);
		browseJButton.setFocusable(false);
		browseJButton.setToolTipText("Select file");


		browseJButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)  {
				try {
					saverDialog.selectPathForSingleImagePanel(properFilePath, imageLayer.getLayerID());
				} catch (Exception e1) {
				
					e1.printStackTrace();
				}

			}
		});
		MouseListenerCreator.addMouseListenerToNormalButtons(browseJButton);

		filePathJPanel.add(browseJButton);
		return filePathJPanel;
	}

	public int getLayerID(){
		return this.imageLayer.getLayerID();
	}


	public String getImagePath() {
		return imagePath;
	}



	private void checkFileValidity(){
		fileValidity =  saverDialog.checkFileValidity(new File(this.properFilePath));
	}

	private void informFileValidity(boolean showMessage){
		this.saverDialog.informUserFromFileValidity(this.fileValidity, this.filePathLabelValue, showMessage);
	}

	

	public void setButtonsEnabled(boolean enable){
		saveSingleJButton.setEnabled(enable);
		browseJButton.setEnabled(enable);
	}
	public void setPath(String path) {
		this.imagePath = path;
	}

	public void setAllMarkingLayerSelections(boolean selected){
		Component[] ssMarkingList= markingTableJPanel.getComponents();
		if(ssMarkingList != null && ssMarkingList.length>0){
			for(int i=0;i<ssMarkingList.length;i++){
				((SingleMarkingPanel)ssMarkingList[i]).setSelected(selected);
				((SingleMarkingPanel)ssMarkingList[i]).setCheckBoxEnableState(selected);


			}

		}

	}
	
	

	protected String initProperFilePathForSaving(ImageLayer iLayer){
		if(iLayer != null){
			if(iLayer.getMarkingsFilePath() != null && iLayer.getMarkingsFilePath().length()>0)
					return iLayer.getMarkingsFilePath(); // give the markingsFile which has been used to import markings

			else
				if(iLayer.getImageFilePath() != null && iLayer.getImageFilePath().length()>0){
					return  iLayer.getFolderOfImage(); // just give folder

				}
		}
			return System.getProperty("user.home");

	}

	public void setSuccessfullSavingColor(ArrayList<Integer> successfullySavedList){

		Component[] ssMarkingList= markingTableJPanel.getComponents();
		if(ssMarkingList != null && ssMarkingList.length>0){
			for(int i=0;i<ssMarkingList.length;i++){
				int smpID= ((SingleMarkingPanel)ssMarkingList[i]).getMarkingLayerID();
				if(successfullySavedList.contains(new Integer(smpID))){
					((SingleMarkingPanel)ssMarkingList[i]).setBGColorBySuccessfullSaving(true);
				}
			/*	else{
					((SingleMarkingPanel)ssMarkingList[i]).setBGColorBySuccessfullSaving(false);
				}
				*/
			}
		}
	}

	public void setAllSingleMarkingPanelBGstoDefault(){
		Component[] ssMarkingList= markingTableJPanel.getComponents();
		if(ssMarkingList != null && ssMarkingList.length>0){
			for(int i=0;i<ssMarkingList.length;i++){
				((SingleMarkingPanel)ssMarkingList[i]).setBGColorBySuccessfullSaving(false);
			}
		}
	}

	public void setSingleMarkingPanelBGstoDefault(int mLayerID){
		Component[] ssMarkingList= markingTableJPanel.getComponents();
		if(ssMarkingList != null && ssMarkingList.length>0){
			for(int i=0;i<ssMarkingList.length;i++){
				SingleMarkingPanel smp= (SingleMarkingPanel)ssMarkingList[i];
				if(smp.getMarkingLayerID() == mLayerID){
					smp.setBGColorBySuccessfullSaving(false);
					return;
				}
			}
		}
	}


	public ArrayList<MarkingLayer> getAllSelectedMarkingLayers(){
		ArrayList<MarkingLayer> selectedMarkingLayers=new ArrayList<MarkingLayer>();
		Component[] sMarkingList= markingTableJPanel.getComponents();
		if(sMarkingList != null && sMarkingList.length>0){
			for(int i=0;i<sMarkingList.length;i++){
				SingleMarkingPanel smp= (SingleMarkingPanel)sMarkingList[i];
				if(smp.isSelected()){
					selectedMarkingLayers.add(smp.getMarkingLayer());
				}
			}

		}
		return selectedMarkingLayers;

	}
	
	public void setSelectionOfAllMarkingLayers(boolean state){
		
		Component[] sMarkingList= markingTableJPanel.getComponents();
		if(sMarkingList != null && sMarkingList.length>0){
			for(int i=0;i<sMarkingList.length;i++){
				SingleMarkingPanel smp= (SingleMarkingPanel)sMarkingList[i];
				smp.setSelected(state);
			}

		}
	

	}

	public ArrayList<Integer> getAllSelectedMarkingLayerIDs(){
		ArrayList<Integer> selectedMarkingLayerIDs=new ArrayList<Integer>();
		Component[] sMarkingList= markingTableJPanel.getComponents();
		if(sMarkingList != null && sMarkingList.length>0){
			for(int i=0;i<sMarkingList.length;i++){
				SingleMarkingPanel smp= (SingleMarkingPanel)sMarkingList[i];
				if(smp.isSelected()){
					selectedMarkingLayerIDs.add(smp.getMarkingLayer().getLayerID());
				}


			}

		}
		return selectedMarkingLayerIDs;

	}



	public boolean hasSelectedMarkingLayer(){
		if(getAllSelectedMarkingLayers() != null && getAllSelectedMarkingLayers().size()>0)
			return true;
		return false;
	}

	public ImageLayer getImageLayer(){
		return this.imageLayer;
	}

	public String getProperFilePathForSaving(){
		return this.properFilePath;
	}

	public int getFileValidity() {
		return fileValidity;
	}

	public String getImageLayerName() {
		return imageLayer.getImageFileName();
	}

	public void setProperFileForMarkings(String properFile, boolean showMessage, int fileValidityID) {
		this.properFilePath = properFile;
		this.filePathLabelValue.setText(this.properFilePath);
		this.fileValidity=fileValidityID;
		//File file=new File(this.properFileForMarkings);
		if(fileValidityID == ID.UNDEFINED)
			checkFileValidity();

			informFileValidity(showMessage);
		// now the target file changes -> change the markingLayer backgrounds to default
		// the color represents the successfully saved markings into properFileForMarkings -> has to refresh color
		setAllSingleMarkingPanelBGstoDefault();
		this.repaint();
	}


/*
	private class CheckBoxIcon implements Icon {
		  public void paintIcon(Component component, Graphics g, int x, int y) {
		    AbstractButton abstractButton = (AbstractButton)component;
		    ButtonModel buttonModel = abstractButton.getModel();
		    Graphics2D g2d = (Graphics2D)g.create();
		    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 1.0f));
		    if(buttonModel.isSelected())
		        g2d.drawImage(createImage("/images/checkBox_selected.png","info"), x, y, component);
		    else
		        g2d.drawImage(createImage("/images/checkBox_unselected.png","info"), x, y, component);

		    g2d.dispose();
		  }
		  public int getIconWidth() {
		    return 18;
		  }
		  public int getIconHeight() {
		    return 18;
		  }

		  private Image createImage(String path, String description) {
		        URL imageURL = CheckBoxIcon.class.getResource(path);
		        Image icn = null;


		        if (imageURL == null) {
		            if(null==icn){
		                //System.out.println("path: "+path);
		                icn = new ImageIcon (CheckBoxIcon.class.getResource(path.replace("..",""))).getImage();
		                if(null!=icn)
		                    return icn;
		                else{
		                    System.err.println("Resource not found: " + path);
		                    return null;
		                }
		            }
		             return null;
		        } else {
		            return (new ImageIcon(imageURL, description)).getImage();
		        }
		    }

		}
*/



/*
		protected class SingleMarkingPanel extends JPanel{

		private JCheckBox saveCheckBox;
		private MarkingLayer mLayer;
		private JPanel markingLabelPanel;
		private JCheckBox showGridCheckBox;


		private SingleMarkingPanel(MarkingLayer mLayer){
		//	this.setImageLayerPath(imageLayerPath);
		//	this.setImageLayerID(iLayerID);
			this.mLayer=mLayer;

			try {
				this.setOpaque(true);
				this.setMaximumSize(new Dimension(2000,45));
				this.setPreferredSize(new Dimension(400,45));
				this.setBackground(Color_schema.dark_40_bg);
				this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
				Icon checkBoxIcon=new CheckBoxIcon();
				// checkBox for selecting
				saveCheckBox=new JCheckBox(checkBoxIcon);
				Font consolas15= new Font("Consolas", Font.PLAIN,15);
				saveCheckBox.setSelected(true);
			//	saveCheckBox.setForeground(Color_schema.color_white_230);
				saveCheckBox.setBackground(Color_schema.dark_40_bg);
				saveCheckBox.setMaximumSize(new Dimension(25,25));
				saveCheckBox.setPreferredSize(new Dimension(25,25));
				saveCheckBox.setMinimumSize(new Dimension(25,25));
				saveCheckBox.setMargin(new Insets(0, 0, 0, 0));
			//	ImageIcon icon= gui.getImageIcon("/images/checkBox_selected.png");
		//		saveCheckBox.setIcon(icon);


				// marking title
				JLabel markingLabel = new JLabel(getMarkingName());
				consolas15= new Font("Consolas", Font.BOLD,15);
				markingLabelPanel = new JPanel();
				markingLabelPanel.setLayout(new BorderLayout());
				markingLabelPanel.setMaximumSize(new Dimension(2000, 40));
				markingLabel.setFont(consolas15);
				markingLabel.setForeground(Color_schema.white_230);
				int markinglabelwidth=markingLabel.getFontMetrics(consolas15).stringWidth(markingLabel.getText());

				markingLabelPanel.setPreferredSize(new Dimension(markinglabelwidth+50,40));
				markingLabelPanel.add(markingLabel);
				markingLabelPanel.setBackground(Color_schema.dark_40_bg);



				// checkBox for selecting
				showGridCheckBox = new JCheckBox(new CheckBoxIcon());
				showGridCheckBox.setSelected(true);
				showGridCheckBox.setBackground(Color_schema.dark_40_bg);
				showGridCheckBox.setMaximumSize(new Dimension(25,25));
				showGridCheckBox.setPreferredSize(new Dimension(25,25));
				showGridCheckBox.setMinimumSize(new Dimension(25,25));
				showGridCheckBox.setMargin(new Insets(0, 0, 0, 0));


				// marking title
				JLabel showGridLabel = new JLabel("Draw Grid");
				showGridLabel.setFont(Fonts.b15);
				showGridLabel.setForeground(Color_schema.white_230);


				this.add(Box.createRigidArea(new Dimension(10,0)));
				this.add(saveCheckBox);
				this.add(Box.createRigidArea(new Dimension(20,0)));
				this.add(markingLabelPanel);
				if(this.mLayer.getGridProperties() != null && this.mLayer.getGridProperties().isGridON()){
					this.add(Box.createHorizontalGlue());
					this.add(showGridCheckBox);
					this.add(Box.createRigidArea(new Dimension(10,0)));
					this.add(showGridLabel);
					this.add(Box.createRigidArea(new Dimension(20,0)));
				}
			//	this.add(Box.createHorizontalGlue()); // when panel gets bigger the horizontal extra space comes between title and deleting button

			} catch (Exception e) {

				LOGGER.severe("Error in construction of SingleMarking " +e.getClass().toString() + " :" +e.getMessage());
		}

		}

			public boolean isSelected(){
				return this.saveCheckBox.isSelected();
			}

			public void setSelected(boolean selected){
				this.saveCheckBox.setSelected(selected);
				this.repaint();
			}

			public int getMarkingLayerID(){
				return this.mLayer.getLayerID();
			}



			public String getMarkingName() {
				return this.mLayer.getLayerName();
			}

			public MarkingLayer getMarkingLayer() {
				return mLayer;
			}



			public void setBGColorBySuccessfullSaving(boolean savedSuccessfully){
				if(savedSuccessfully){
					this.setBackground(Color_schema.darkest_green);
					this.saveCheckBox.setBackground(Color_schema.darkest_green);
					this.markingLabelPanel.setBackground(Color_schema.darkest_green);

				}
				else{
				this.setBackground(Color_schema.dark_40_bg);
				this.saveCheckBox.setBackground(Color_schema.dark_40_bg);
				this.markingLabelPanel.setBackground(Color_schema.dark_40_bg);
				}
			}
		/*
			public void setMarkingName(String markingName) {
				this.markingName = markingName;
			}

			public int getImageLayerID() {
				return iLayerID;
			}

			public void setImageLayerID(int iLayerID) {
				this.iLayerID = iLayerID;
			}


		}
*/
}