package gui.saving.ImageSet;

import gui.Color_schema;
import gui.ContentPane;
import gui.GUI;
import gui.MouseListenerCreator;
import gui.ProgressBallsDialog;
import gui.ShadyMessageDialog;
import gui.file.FileManager;
import gui.file.OpenImageFilesDialog;
import gui.graphics.BigCloseIcon;
import gui.graphics.MediumCloseIcon;
import gui.saving.SelectFileDialog;
import gui.saving.SingleImagePanel;
import information.Fonts;
import information.ID;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.ImageGraphicAttribute;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.text.PlainDocument;

import operators.GridComparator;
import operators.ImageCreator;
import managers.TaskManager;

public class ImageSetCreator extends JDialog implements MouseListener, Runnable{
private TaskManager taskManager;
private GUI gui;
private JPanel backPanel;
private ArrayList<SingleDrawImagePanel> drawImagePanels;
private int presentRowNumber;
private int presentColumnNumber;
private final static Logger LOGGER = Logger.getLogger("MCCLogger");
private JPanel gridPanel;
private GridLayout gridLayout;
private int gap;
public JComboBox<String>fontsBox;
public JComboBox<Integer>fontSizeBox;
private JComboBox<Integer> rowBox;
private JComboBox<Integer> columnBox;


//private BufferedImage image;
private JPanel gridBackPanel;
private JPanel whiteGridBackPanel;
private JMenuBar menuBar;
private int savingImageWidth=2000;
private int savingImageHeight=1500;
private ArrowMouseListener arrowMouseListener;
private JTextField widthField;
private JTextField heigthField;
private JPanel browsingBackPanel;
private SelectFileDialog selectFileDialog;
private JLabel savingPathJLabel;
private String presentFolder;
private JButton cancelJButton;
//public static PanelMoveListener panelMoveListener;
private int[] movingPosition=null;
private Thread createImageThread;
private int threadNumber=1;
//private ImageImportCancelerThread cancelImageImport;
//private boolean importing=false;

private ProgressBallsDialog progressBallsDialog;
private ProgressBallsDialog progressWihoutButtons;
private JButton exportJButton;



	/**
	 * Class constructor. Opens window, where use can download Images from ImageLayers and image-files and export them to file.
	 * @param frame JFrame parent frame
	 * @param taskManager TaskManager-object used to connect core functions.
	 * @param gui GUI-object to connect Graphical interface
	 */
	public ImageSetCreator(JFrame frame, TaskManager taskManager, GUI gui){
		//super(frame, true);

		try {
			this.taskManager=taskManager;
			this.gui=gui;
			this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			this.drawImagePanels=new ArrayList<SingleDrawImagePanel>();
			this.presentColumnNumber=1;
			this.presentRowNumber=1;
			this.gap=15;
			this.presentFolder=gui.getPresentFolder();
		//	this.panelMoveListener=new PanelMoveListener(this);
		//	this.image=this.taskManager.createImageFile(new File("/home/antti/4kuvaa/kolmas.jpg"));
			this.createImageThread=new Thread(this, "CreateImage_"+threadNumber++);
			initComponents();
			this.progressBallsDialog = new ProgressBallsDialog(new JFrame(), "Creating set of Images", "", ID.CANCEL, this);
			this.progressWihoutButtons = new ProgressBallsDialog(new JFrame(), "Opening images", "", ID.CANCEL, this);
			this.addMouseListener(this);
			
			

		} catch (Exception e) {
			LOGGER.severe("Error in initializing ImageSetCreator: "+e.getMessage());
			e.printStackTrace();
			this.setVisible(false);
			this.dispose();
		}

		this.setVisible(true);
		if(this.taskManager != null && this.taskManager.getImageLayerList() != null && this.taskManager.getImageLayerList().size()>0){
			createImagesFromPresentImageLayers();
			setImagesToGrid();
		}
	}

	/**
	 * Initializes JPanels and other graphical components of this ImageSetCreator-object.
	 */
	private void initComponents() throws Exception{

	
			this.setBounds(gui.getBounds());
			this.setUndecorated(true);
			this.setBackground(new Color(0,0,0,0));
			this.setContentPane(new ContentPane());
			this.getContentPane().setBackground(Color_schema.dark_30);
			this.getContentPane().setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();

			backPanel = new JPanel();
			backPanel.setOpaque(true);
			backPanel.setBackground(Color_schema.grey_100);
			backPanel.setLayout(new BorderLayout());

			backPanel.setBorder(BorderFactory.createLineBorder(Color_schema.button_light_border, 5));
			backPanel.setMaximumSize(new Dimension((int)(this.getBounds().getWidth()*0.90), (int)(this.getBounds().getHeight()*0.90)));
			backPanel.setMinimumSize(new Dimension((int)(this.getBounds().getWidth()*0.90), (int)(this.getBounds().getHeight()*0.90)));
			backPanel.setPreferredSize(new Dimension((int)(this.getBounds().getWidth()*0.90), (int)(this.getBounds().getHeight()*0.90)));

			JPanel titlePanel = new JPanel();
			titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			titlePanel.setBackground(new Color(0,0,0,0));
			JLabel titleLabel=new JLabel("Create Set of Images");
			titleLabel.setFont(Fonts.b22);
			titlePanel.add(titleLabel);
			c.gridx=0;
			c.gridy=0;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 1;
			this.add(titlePanel, c);

			menuBar = new JMenuBar();
			JMenu file_menu = new JMenu("File");
			file_menu.setMnemonic(KeyEvent.VK_ALT);
			JMenuItem addImages=new JMenuItem("Add Images from file");
			addImages.setMnemonic(KeyEvent.VK_A);
			addActionsToMenuItems(addImages, ID.ADD_IMAGES_FROM_FILES);

			JMenuItem addImagesFromImageLayers=new JMenuItem("Add Images from ImageLayers");
			addImagesFromImageLayers.setMnemonic(KeyEvent.VK_L);
			addActionsToMenuItems(addImagesFromImageLayers, ID.ADD_IMAGES_FROM_IMAGE_LAYERS);

			JMenuItem exportImageSetMenuItem = new JMenuItem("Export Set of Images");
			exportImageSetMenuItem.setMnemonic(KeyEvent.VK_E);
			addActionsToMenuItems(exportImageSetMenuItem, ID.EXPORT_IMAGE_SET);


			JMenuItem close_menu_item=new JMenuItem("Close");
			close_menu_item.setMnemonic(KeyEvent.VK_C);
			file_menu.add(addImages);
			file_menu.add(addImagesFromImageLayers);
			file_menu.add(exportImageSetMenuItem);
			file_menu.add(close_menu_item);
			addActionsToMenuItems(close_menu_item, ID.MENU_ITEM_FILE_QUIT);
			menuBar.add(file_menu);
			menuBar.add(Box.createRigidArea(new Dimension(10,0)));
			setUpRowAndColumnBoxes();
			JLabel rowLabel=new JLabel("Rows:");
			rowLabel.setFont(Fonts.b16);
			rowLabel.setForeground(Color_schema.orange_medium);
			menuBar.add(rowLabel);
			menuBar.add(Box.createRigidArea(new Dimension(10,0)));
			menuBar.add(this.rowBox);
			menuBar.add(Box.createRigidArea(new Dimension(10,0)));
			JLabel columnLabel=new JLabel("Columns:");
			columnLabel.setFont(Fonts.b16);
			columnLabel.setForeground(Color_schema.orange_medium);
			menuBar.add(columnLabel);
			menuBar.add(Box.createRigidArea(new Dimension(10,0)));
			menuBar.add(this.columnBox);
			menuBar.add(Box.createRigidArea(new Dimension(10,0)));

			setUpFontBox();
			JLabel fontLabel=new JLabel("Font:");
			fontLabel.setFont(Fonts.b16);
			fontLabel.setForeground(Color_schema.orange_medium);
			menuBar.add(fontLabel);
			menuBar.add(Box.createRigidArea(new Dimension(10,0)));
			menuBar.add(fontsBox);
			menuBar.add(Box.createRigidArea(new Dimension(10,0)));
			JLabel fontSizeLabel=new JLabel("Font size:");
			fontSizeLabel.setFont(Fonts.b16);
			fontSizeLabel.setForeground(Color_schema.orange_medium);
			menuBar.add(fontSizeLabel);
			menuBar.add(Box.createRigidArea(new Dimension(10,0)));
			setUpFontSizeBox();
			menuBar.add(fontSizeBox);
			menuBar.add(Box.createHorizontalGlue());
			JPanel closeImageSetCreator = new JPanel();
			closeImageSetCreator.setLayout(new FlowLayout(FlowLayout.RIGHT));
			closeImageSetCreator.setMaximumSize(new Dimension(25,40));
			closeImageSetCreator.setPreferredSize(new Dimension(25,40));
			closeImageSetCreator.setMinimumSize(new Dimension(25,40));
			closeImageSetCreator.setBackground(Color_schema.dark_40);
			JButton closeJButton = new JButton(new MediumCloseIcon(false));
			closeJButton.setAlignmentY(Component.CENTER_ALIGNMENT);
			closeJButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
			closeJButton.setPreferredSize(new Dimension(20,20));
			closeJButton.setMaximumSize(new Dimension(20,20));
			closeJButton.setMargin(new Insets(0, 0,0, 0));
			closeJButton.setBorder(BorderFactory.createLineBorder(Color_schema.button_grey_border, 1));
			closeJButton.setContentAreaFilled(false);
			closeJButton.setFocusable(false);
			closeJButton.setToolTipText("Close ImageSet window");

			// set up listener for closeButton
			MouseListenerCreator.addMouseListenerToMediumCloseButtons(closeJButton);

			closeJButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					closeWindow();

				}
			});
			menuBar.add(closeJButton);



			backPanel.add(menuBar, BorderLayout.PAGE_START);

			gridBackPanel = new JPanel();
			gridBackPanel.setLayout(new GridBagLayout());
			gridBackPanel.setBackground(Color_schema.dark_40);

			whiteGridBackPanel = new JPanel();
			whiteGridBackPanel.setLayout(new GridBagLayout());
			whiteGridBackPanel.setBackground(Color_schema.white_230);

			gridPanel = new JPanel();
			gridLayout = new GridLayout(1, 1);
			gridPanel.setLayout(gridLayout);
			gridPanel.setBackground(Color_schema.white_230);
			gridPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);



			whiteGridBackPanel.add(gridPanel);
			gridBackPanel.add(whiteGridBackPanel);

			backPanel.add(gridBackPanel, BorderLayout.CENTER);
			backPanel.add(initBrowsingPanel(), BorderLayout.PAGE_END);

			c.gridx=0;
			c.gridy=1; 
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 1;
			this.add(backPanel,c);
			this.addMouseListener(this);
			this.repaint();
		
	}



	/**
	 * Initializes the Browsing Panel at below of window.
	 * @return JPanel BrowsingPanel
	 */
	public JPanel initBrowsingPanel() throws Exception{
		

			browsingBackPanel = new JPanel();
	//		browsingBackPanel.setLayout(new BoxLayout(browsingBackPanel, BoxLayout.PAGE_AXIS));
			browsingBackPanel.setLayout(new BorderLayout());
			browsingBackPanel.setBackground(Color_schema.dark_30);
			browsingBackPanel.setMaximumSize(new Dimension(5000, 40));
			browsingBackPanel.setPreferredSize(new Dimension(800, 40));
			browsingBackPanel.setMinimumSize(new Dimension(200, 40));
			browsingBackPanel.setBorder(BorderFactory.createLineBorder(Color_schema.grey_100, 1));

			JPanel browsingPanel = new JPanel();
			browsingPanel.setLayout(new BoxLayout(browsingPanel, BoxLayout.LINE_AXIS));
			browsingPanel.setBackground(Color_schema.dark_30);
			browsingPanel.setMaximumSize(new Dimension(5000, 40));
			browsingPanel.setPreferredSize(new Dimension(800, 40));
			browsingPanel.setMinimumSize(new Dimension(200, 40));

			JLabel saveToLabel = new JLabel("Save to file: ");
			saveToLabel.setFont(Fonts.b18);

			this.savingPathJLabel = new JLabel(""); 
			savingPathJLabel.setFont(Fonts.p16);
			savingPathJLabel.setMinimumSize(new Dimension(200, 30));

			// Select file path for all ImageLayers
			JButton selectFileJButton = new JButton("Browse");
			selectFileJButton.setPreferredSize(new Dimension(100,30));
			selectFileJButton.setMaximumSize(new Dimension(150,30));
			selectFileJButton.setContentAreaFilled(false);
			selectFileJButton.setBackground(Color_schema.dark_20);
			selectFileJButton.setFont(Fonts.b18);
		//	initActionsToButtons(selectFileJButton, ID.OPEN_MARKING_FILE);
			selectFileJButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						selectFile();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
			selectFileJButton.setFocusable(false);
			MouseListenerCreator.addMouseListenerToNormalButtons(selectFileJButton);

			JPanel resolutionPanel = new JPanel();
			resolutionPanel.setLayout(new BoxLayout(resolutionPanel, BoxLayout.LINE_AXIS));
			resolutionPanel.setBackground(Color_schema.dark_30);
			resolutionPanel.setMaximumSize(new Dimension(5000, 40));
			resolutionPanel.setPreferredSize(new Dimension(200, 40));
			resolutionPanel.setMinimumSize(new Dimension(200, 40));

			JLabel widthResolutionLabel = new JLabel("Image Width:");
			widthResolutionLabel.setFont(Fonts.b16);
			resolutionPanel.add(Box.createRigidArea(new Dimension(20,0)));
			resolutionPanel.add(widthResolutionLabel);
			resolutionPanel.add(Box.createRigidArea(new Dimension(10,0)));

			widthField = new JTextField(this.savingImageWidth);
			widthField.setFont(Fonts.b16);
			widthField.setText(""+this.savingImageWidth);
			widthField.setHorizontalAlignment(JLabel.CENTER);
			widthField.setBackground(Color_schema.dark_30);
			widthField.setForeground(Color_schema.white_230);
			widthField.setMinimumSize(new Dimension(70,30));
			widthField.setMaximumSize(new Dimension(70,30));
			widthField.setPreferredSize(new Dimension(70,30));
			widthField.setColumns(4);
			widthField.setBorder(BorderFactory.createLineBorder(Color_schema.grey_100,  1));
			//set DocumentFileter to widthField
			PlainDocument w_doc = (PlainDocument) widthField.getDocument();
		    w_doc.setDocumentFilter(new ResolutionIntFilter());

		    widthField.setToolTipText("Set imagewidth between 1-5000 pixels");

		    JLabel pixelLabel1 = new JLabel("px");
		    pixelLabel1.setFont(Fonts.b16);
		    JLabel pixelLabel2 = new JLabel("px");
		    pixelLabel2.setFont(Fonts.b16);

			heigthField = new JTextField(this.savingImageWidth);
			heigthField.setFont(Fonts.b16);
			heigthField.setText(""+this.savingImageWidth);
			heigthField.setHorizontalAlignment(JLabel.CENTER);
			heigthField.setBackground(Color_schema.dark_30);
			heigthField.setForeground(Color_schema.white_230);
			heigthField.setMinimumSize(new Dimension(70,30));
			heigthField.setMaximumSize(new Dimension(70,30));
			heigthField.setPreferredSize(new Dimension(70,30));
			heigthField.setColumns(4);
			heigthField.setBorder(BorderFactory.createLineBorder(Color_schema.grey_100,  1));
			//set DocumentFileter to heightField
			PlainDocument h_doc = (PlainDocument) heigthField.getDocument();
		      h_doc.setDocumentFilter(new ResolutionIntFilter());
		      heigthField.setToolTipText("Set image height between 1-5000 pixels.");

			resolutionPanel.add(widthField);
			resolutionPanel.add(createBasicArrowButtons(widthField, ID.TEXTFIELD_WIDTH));
			resolutionPanel.add(Box.createRigidArea(new Dimension(5,0)));
			 resolutionPanel.add(pixelLabel1);
			 resolutionPanel.add(Box.createRigidArea(new Dimension(30,0)));

			JLabel heightResolutionLabel = new JLabel("Image Height:");
			heightResolutionLabel.setFont(Fonts.b16);

			resolutionPanel.add(heightResolutionLabel);
			resolutionPanel.add(Box.createRigidArea(new Dimension(10,0)));

			resolutionPanel.add(heigthField);
			resolutionPanel.add(createBasicArrowButtons(heigthField, ID.TEXTFIELD_HEIGHT));
			resolutionPanel.add(Box.createRigidArea(new Dimension(5,0)));
			resolutionPanel.add(pixelLabel2);

			exportJButton = new JButton("Export");
			exportJButton.setPreferredSize(new Dimension(100,30));
			exportJButton.setMaximumSize(new Dimension(150,30));
			exportJButton.setContentAreaFilled(false);
			exportJButton.setBackground(Color_schema.dark_20);
			exportJButton.setFont(Fonts.b18);
			exportJButton.setEnabled(false);
		
			exportJButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						exportImageSet();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
			exportJButton.setFocusable(false);
			MouseListenerCreator.addMouseListenerToNormalButtons(exportJButton);
			browsingPanel.add(resolutionPanel);
			browsingPanel.add(saveToLabel);
			browsingPanel.add(Box.createRigidArea(new Dimension(20,0)));
			browsingPanel.add(savingPathJLabel);	
			browsingPanel.add(Box.createRigidArea(new Dimension(20,0)));
			browsingPanel.add(selectFileJButton);
			browsingPanel.add(Box.createRigidArea(new Dimension(20,0)));
			browsingPanel.add(exportJButton);
			browsingPanel.add(Box.createRigidArea(new Dimension(20,0)));
			browsingBackPanel.add(browsingPanel, BorderLayout.PAGE_START);

			return browsingBackPanel;

		
	}

	/**
	 * Changes positions in GRID of two @see SingleDrawImagePanel-objects.
	 * @param second int[] position of @see SingleDrawImagePanel-object that is moved.
	 */
	public void swithcSingleDrawPanels(int[] second){
		SingleDrawImagePanel firstPanel= getSDPatPosition(this.movingPosition[0],this.movingPosition[1]);
		SingleDrawImagePanel secondPanel = getSDPatPosition(second[0], second[1]);
		if(firstPanel != null && secondPanel != null){
			firstPanel.setGridPosition(second);
			secondPanel.setGridPosition(this.movingPosition);

		}


		this.movingPosition=null;
		if(firstPanel.getPanelSize().width == secondPanel.getPanelSize().width && firstPanel.getPanelSize().height == secondPanel.getPanelSize().height){
			onlyAddPanelsToGrid();
			LOGGER.fine("only adding panels");
		}
		else
		refreshGridPanelSizes();

	}


	/**
	 *  Open Dialog to select image files.
	 * @throws Exception
	 */
	private void selectFile() throws Exception{
		if(this.selectFileDialog==null){
		String path = gui.getPresentFolder();
		if(path== null)
			path = System.getProperty("user.home");
		this.selectFileDialog = new SelectFileDialog(this.gui, path, this.backPanel, ID.EXPORT_IMAGE);
		}


		this.selectFileDialog.setVisible(true);

		if(this.selectFileDialog.fileWritingType != ID.CANCEL && this.selectFileDialog.fileWritingType != ID.ERROR){
			// set filepath
			this.savingPathJLabel.setText(this.selectFileDialog.getSelectedFilePath());


		}
			this.selectFileDialog.setVisible(false);
		this.selectFileDialog.dispose();
		if(this.savingPathJLabel.getText() != null && this.savingPathJLabel.getText().length()>0){
			this.exportJButton.setEnabled(true);
		}


	}

	/**
	 * Opens Confirm dialog to close ImageSet window.
	 */
	private void closeWindow(){
		ShadyMessageDialog dialog = new ShadyMessageDialog(new JFrame(), "Closing image set creator window", "Really want to close ImageSet window?", ID.YES_NO, this);
		if(dialog.showDialog() == ID.YES){
			this.setVisible(false);
			this.dispose();
		}

	}

	/**
	 * Exports ImageSet to file. Show progress by @see ShadyMessageDialog.
	 * @throws InterruptedException
	 */
	@SuppressWarnings("static-access")
	private void createImage() throws InterruptedException{
		if(this.savingPathJLabel.getText() != null){
			int imageWidth=Integer.parseInt(this.widthField.getText().trim());
			int imageHeight=Integer.parseInt(this.heigthField.getText().trim());
			Dimension imageDimension= new Dimension(imageWidth,imageHeight);
			int panelwidth=whiteGridBackPanel.getPreferredSize().width;
			double multiplier= ((double)imageWidth)/((double)panelwidth);
			ImageCreator imageCreator=new ImageCreator(multiplier, this.gui.taskManager);
			imageCreator.initImageSetProperties(this.gap, this.presentRowNumber, this.presentColumnNumber,this.drawImagePanels, imageDimension, this.savingPathJLabel.getText());
	
			if(imageCreator.startImageSetCreatorThread());
			
			while(imageCreator.isContinueCreating() && progressBallsDialog.isShowON()){
				createImageThread.sleep(1000);
			
			}

			if(progressBallsDialog.isShowON())
				progressBallsDialog.stopPaintingAndClose();

			if(imageCreator.isContinueCreating())
				imageCreator.setContinueCreating(false);

			ShadyMessageDialog dialog;
			if(imageCreator.isImageSetCreatedSuccessfully())
			{
				dialog = new ShadyMessageDialog(new JFrame(), "Exporting succesfull", "Exported successfully the set of images.  ", ID.OK, this);
				dialog.showDialog();
				dialog=null;
			}
			else{
				dialog = new ShadyMessageDialog(new JFrame(), "Exporting not succesfull", "Could not export set of Images. Read Log for errors or try again.", ID.OK, this);
				dialog.showDialog();
				dialog= null;
			}
		}
	}

	/**
	 * 
	 * Creates Arrowbuttons to bottom of ImageSetCreator-window. Increases and decreases value of export image size.
	 * @param field Textfield where arrowbuttons actions are performed
	 * @param fieldID Textfield ID where arrowbuttons actions are performed
	 * @return JPanel where arrowbuttons are added.
	 * @throws Exception
	 */
	private JPanel createBasicArrowButtons(JTextField field, int fieldID) throws Exception{
		this.arrowMouseListener = new ArrowMouseListener(this.widthField, this.heigthField);
		JPanel arrowPanel = new JPanel();
		arrowPanel.setLayout(new BorderLayout());
		arrowPanel.setMaximumSize(new Dimension(20,30));
		arrowPanel.setPreferredSize(new Dimension(20,30));
		arrowPanel.setMinimumSize(new Dimension(20,30));

		//nortbutton - the upper button 
		BasicArrowButton northButton = new BasicArrowButton(BasicArrowButton.NORTH);
		if(fieldID== ID.TEXTFIELD_WIDTH)
			northButton.setActionCommand(this.arrowMouseListener.widthUp);
		else
			northButton.setActionCommand(this.arrowMouseListener.heighthUp);

		// add listener
		MouseListenerCreator.addMouseListenerToNormalButtons(northButton);


		//southbutton - a button below
		BasicArrowButton southButton = new BasicArrowButton(BasicArrowButton.SOUTH);
		if(fieldID==ID.TEXTFIELD_WIDTH)
			southButton.setActionCommand(this.arrowMouseListener.widthDown);
		else
			southButton.setActionCommand(this.arrowMouseListener.heightDown);

		MouseListenerCreator.addMouseListenerToNormalButtons(southButton);

		southButton.addMouseListener(this.arrowMouseListener);
		northButton.addMouseListener(this.arrowMouseListener);

		arrowPanel.add(northButton, BorderLayout.NORTH);
		arrowPanel.add(southButton, BorderLayout.SOUTH);

		return arrowPanel;

	}



	/**
	 * Initializes JComboboxes, showing the grid dimension: how many vertically and horizontally.
	 */
	private void setUpRowAndColumnBoxes(){
		
		Integer[] iArray=new Integer[10];
		for (int i = 1; i <= 10; i++) {
				iArray[i-1]=i;
		}
		//rows
		rowBox=new JComboBox<Integer>(iArray);
		rowBox.setSelectedItem(1);
		rowBox.setMaximumSize(new Dimension(50,50));
		rowBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (drawImagePanels != null && drawImagePanels.size()>0 && e.getStateChange() == ItemEvent.SELECTED) {

					if(presentRowNumber != (int)rowBox.getSelectedItem()){
						presentRowNumber=(int)rowBox.getSelectedItem();
						// update grid
						setImagesToGrid();
					}
                }

			}
		});
		//columns
		columnBox=new JComboBox<Integer>(iArray);
		columnBox.setSelectedItem(1);
		columnBox.setMaximumSize(new Dimension(50,50));
		columnBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (drawImagePanels != null && drawImagePanels.size()>0  && e.getStateChange() == ItemEvent.SELECTED) {
					if(presentColumnNumber != (int)columnBox.getSelectedItem()){
					presentColumnNumber= (int)columnBox.getSelectedItem();
					// update grid
					setImagesToGrid();
					}
                }

			}
		});
	}



	/**
	 *  Initializes the JCombobox showning available fonts for imageNames.
	 */
	private void  setUpFontBox(){

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontFamilyNames = ge.getAvailableFontFamilyNames();
        fontsBox = new JComboBox<String>(fontFamilyNames);
        fontsBox.setSelectedItem(0);
        fontsBox.setRenderer(new ComboRenderer(fontsBox));
        fontsBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (drawImagePanels != null && drawImagePanels.size()>0  && e.getStateChange() == ItemEvent.SELECTED) {
                    final String fontName = fontsBox.getSelectedItem().toString();
                 //   fontsBox.setFont(new Font(fontName, Font.PLAIN, 16));
                    updatePanelFonts();
                    refreshGridPanelSizes();
                }
            }
        });
        fontsBox.setSelectedItem(0);
        fontsBox.getEditor().selectAll();
        fontsBox.setMaximumSize(new Dimension(300,50));


	}

	/**
	 * Initializes the JCombobox showning available font sizes for imageNames.
	 */
	private void setUpFontSizeBox(){

		Integer[] iArray=new Integer[35];
		int counter=0;
		for (int i = 5; i < 80; i++) {

			if(i>=30){
				iArray[counter]=i;
				i+=4;
			}
			else{
				iArray[counter]=i;
			}

			counter++;
		}
		fontSizeBox=new JComboBox<Integer>(iArray);
		fontSizeBox.setSelectedItem(22);
		fontSizeBox.setMaximumSize(new Dimension(50,25));
		fontSizeBox.setMinimumSize(new Dimension(50,25));
		fontSizeBox.setPreferredSize(new Dimension(50,25));
		fontSizeBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (drawImagePanels != null && drawImagePanels.size()>0  && e.getStateChange() == ItemEvent.SELECTED) {
				//	int selectedFontSize= (int)fontSizeBox.getSelectedItem();

                    updatePanelFonts();
                   refreshGridPanelSizes();
                }

			}
		});
	}




	/**
	 * Sets actions to JMenuItems.
	 * @param item JMenuItem where action is added
	 * @param itemType int type of action
	 */
	private void addActionsToMenuItems(JMenuItem item, int itemType){
		switch (itemType){
			case ID.MENU_ITEM_FILE_QUIT:
				item.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						closeWindow();
					}
				});
				break;

			case ID.ADD_IMAGES_FROM_FILES:
				item.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {

						try {

							selectAndGetImagesFromFiles();

						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					//	importImagesFromFiles();
				//		ProgressBallsDialog pbd = new ProgressBallsDialog(new JFrame(), "ImageSet", "Creating set of Images", ID.NO_BUTTONS, backPanel);
				//		pbd.showDialog();
				//		setImagesToGrid();
				//		pbd.stopPaintingAndClose();

					}
				});
				break;



			case ID.ADD_IMAGES_FROM_IMAGE_LAYERS:
				item.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						createImagesFromPresentImageLayers();
						setImagesToGrid();
					}
				});
				break;

			case ID.EXPORT_IMAGE_SET:
				item.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent arg0) {
						exportImageSet();
					}
				});

				break;
		}

	}

	/**
	 * Starts a Thread for exporting ImageSet if images found.
	 */
	private void exportImageSet(){
		if(drawImagePanels != null && drawImagePanels.size()>0){
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					initThreads();
					progressBallsDialog.showDialog();
				}
			});
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					createImageThread.start();
				}
			});

		}
		else{
			ShadyMessageDialog dialog=new ShadyMessageDialog(new JFrame(), "No Images!", "import images before exporting.", ID.OK, this);
			dialog.showDialog();
			dialog=null;
		}

	}

	/**
	 * Refreshes Threads for saving ImageSet and showing progress.
	 */
	private void initThreads(){
		this.progressBallsDialog.refreshDialog();
		this.createImageThread=new Thread(this, "CreateImage_"+threadNumber++);
	}

	/**
	 * Opens ImagefileDialog and sets selected image files to grid.
	 * 
	 * @throws InterruptedException
	 */
	private void selectAndGetImagesFromFiles() throws InterruptedException{

		OpenImageFilesDialog openDialog=new OpenImageFilesDialog(new JFrame(), this.getBounds(), this.backPanel.getBounds(), this.presentFolder);
		openDialog.setVisible(true);
		File[] imageFiles= openDialog.getSelectedFiles();
		openDialog.dispose();
		openDialog=null;

		importImagesFromFiles(imageFiles);
		setImagesToGrid();
	}



	/**
	 * returns a row width of given row number.
	 * @param row int the row number of grid.
	 * @return int the row width
	 */
	private int getPanelsRowWidth(int row){
		try{
		int rowWidth=0;
		Iterator<SingleDrawImagePanel> sdpIterator=this.drawImagePanels.iterator();
		while(sdpIterator.hasNext()){
			SingleDrawImagePanel sdp=sdpIterator.next();
			if(sdp.getGridPosition().length>0 && sdp.getGridPosition()[0] == row){
				rowWidth+=sdp.getPanelSize().width;

			}

		}
		return rowWidth;
		}catch(Exception e){
			LOGGER.severe("Error ImageSetCreator in getting grid row width: "+e.getMessage());
			return 0;
		}
	}

	/**
	 * returns a column height of given column number.
	 * @param row int the column number of grid.
	 * @return int the column height
	 */
	private int getPanelsColumnHeight(int column){
		try{
		int columnHeight=0;
		Iterator<SingleDrawImagePanel> sdpIterator=this.drawImagePanels.iterator();
		while(sdpIterator.hasNext()){
			SingleDrawImagePanel sdp=sdpIterator.next();
			if(sdp.getGridPosition().length>0 && sdp.getGridPosition()[1] == column){
				columnHeight+=sdp.getPanelSize().height;


			}

		}

		return columnHeight;
		}catch(Exception e){
			LOGGER.severe("Error ImageSetCreator in getting grid column height: "+e.getMessage());
			return 0;
		}
	}

	/**
	 *  Updates the grid dimension by given row and column number. Updates also the default export size dimension shown at downbar.
	 */
	private void updateGridSize(){
		int maxWidth =0 , maxHeigth = 0;
		//get maximum width from panels
		for (int i = 1; i <= presentRowNumber; i++) {
			int width=getPanelsRowWidth(i);
			if(width > maxWidth)
				maxWidth=width;
		}
		//get maximum height from panels
		for (int i = 1; i <= presentColumnNumber; i++) {
			int height=getPanelsColumnHeight(i);
			if(height > maxHeigth)
				maxHeigth=height;
		}
		int gapToWidth= (this.presentColumnNumber-1)*(this.gap);
		int gapToHeight= (this.presentRowNumber-1)*(this.gap);
		Dimension dim=new Dimension(maxWidth+gapToWidth, maxHeigth+gapToHeight);
		this.gridPanel.setMaximumSize(dim);
		this.gridPanel.setPreferredSize(dim);
		this.gridPanel.setMinimumSize(dim);


		int w=dim.width+this.gap*2;
		int h=dim.height+this.gap*2;
		Dimension wgbDimension=new Dimension(w,h);
		this.whiteGridBackPanel.setMaximumSize(wgbDimension);
		this.whiteGridBackPanel.setPreferredSize(wgbDimension);
		this.whiteGridBackPanel.setMinimumSize(wgbDimension);
		this.arrowMouseListener.setScalingFactor(wgbDimension.getHeight()/wgbDimension.getWidth()); // calculate scaling
		this.arrowMouseListener.updateResolutions(); // updates the destination image resolution


	}

	/**
	 * Adds ImagePanels to grid cells. This happens when all ImagePanels are same size and no need to scale cell sizes.
	 */
	private void onlyAddPanelsToGrid(){
		this.gridPanel.removeAll();
		this.gridPanel.revalidate();
		Collections.sort(this.drawImagePanels, new GridComparator());
		for (int r = 1; r <= presentRowNumber; r++) {
			for (int c = 1; c <= presentColumnNumber; c++) {
				SingleDrawImagePanel sdp=getSDPatPosition(r, c);
				if(sdp != null){
					this.gridPanel.add(sdp);
				}
			}
		}

		this.gridPanel.repaint();
	}

	
	/**
	 *  Calculates new sizes for panels of Grid (Cells). This happen when ImagePanels are unequal sizes or whole grid is reformed.
	 *  Repaints the Grid.
	 */
	private void refreshGridPanelSizes(){
		try {
			int titlePanelHeight = getMaximumTitlePanelHeightByFontSize();
			this.gridPanel.removeAll();
			this.gridPanel.revalidate();
			Dimension panelDimension=calculateDrawPanelDimension();
/*
			// setup panels which has images -> has to these first to know the maximum panel sizes
			for (int r = 1; r <= presentRowNumber; r++) {
				for (int c = 1; c <= presentColumnNumber; c++) {

					SingleDrawImagePanel sdp=getSDPatPosition(r, c);
					if(sdp != null && sdp.getImage() != null){
						sdp.setDimension(panelDimension, titlePanelHeight);
						sdp.updateFont(sdp.getFont());
						this.gridPanel.add(sdp);
					}

				}
			}
*/
			//go through all rows and columns
			for (int r = 1; r <= presentRowNumber; r++) {
				for (int c = 1; c <= presentColumnNumber; c++) {
					//get SingleDrawImagePanel and set new dimension and font
					SingleDrawImagePanel sdp=getSDPatPosition(r, c);
					if(sdp != null){
						if(sdp.getImage() != null){
							sdp.setDimension(panelDimension, titlePanelHeight);
							sdp.updateFont(sdp.getFont());
							this.gridPanel.add(sdp);
						}
						//scale image if panel has any
						if(sdp.getImage() == null){
							int w = getMaximumPanelWidthAtColumn(c);
							int h = getMaximumPanelHeightAtRow(r);
							if(w == 0)
								w=getMaximumPanelWidthAtColumn(0); // get maximum width of all panels which has image
							if(h==0)
								h=getMaximumPanelHeightAtRow(0); // get maximum height of all panels which has image
							sdp.setDimension(new Dimension(w,h),titlePanelHeight);
							this.gridPanel.add(sdp);
						}
					}

				}
			}
			updateGridSize();
			this.revalidate();
			this.repaint();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}




	/**
	 * Refreshes the ImagePanels shown in Grid.
	 */
	public void setImagesToGrid(){
		try {
			if(drawImagePanels != null && drawImagePanels.size()>0){

				int titlePanelHeight = getMaximumTitlePanelHeightByFontSize();
				// sort panels with present order
				Collections.sort(this.drawImagePanels, new GridComparator());
				// set all gridPositions to null in SingleDrawPanels
				setGridPositionsToNull();
				removeEmptyPanels();
				// remove all components from Grid
				this.gridPanel.removeAll();
				this.gridPanel.revalidate();

				// calculate has enough gridpositions for images
				int[] enougGridPoints=calculateEnoughGridPoints(new int[]{this.presentRowNumber, this.presentColumnNumber},this.drawImagePanels.size());
				this.presentRowNumber=enougGridPoints[0];
				this.presentColumnNumber=enougGridPoints[1];
				updateGridComboBoxItems();
				// setup GridLayout
				this.gridLayout=new GridLayout(this.presentRowNumber, this.presentColumnNumber,this.gap,this.gap);
				this.gridPanel.setLayout(gridLayout);



					// calculate dimension for SingleDrawImagePanels
				Dimension panelDimension=calculateDrawPanelDimension();
				Dimension maxImageDimension=null;
		//		rowloop:
				for (int r = 1; r <= presentRowNumber; r++) {
					for (int c = 1; c <= presentColumnNumber; c++) {
				/*		if(importing && this.cancelImageImport.isCancelled()){ // user has cancelled importing files

							break rowloop;
						}
				 */	
						//get,set and add SingleDrawImagePanel to GRID (may be empty Panel -> added in different way)
						SingleDrawImagePanel sdp=getFirstUnPositionedSDP();
						if(sdp != null){
							sdp.setGridPosition(r, c);
							Dimension singlePanelDimension= sdp.setDimension(panelDimension, titlePanelHeight);
							sdp.updateFont(sdp.getFont()); // if title string can't fit to titlePanel -> font size changed
							if(maxImageDimension == null || maxImageDimension.width < singlePanelDimension.width && maxImageDimension.height < singlePanelDimension.height){
								maxImageDimension=singlePanelDimension;
							}
							this.gridPanel.add(sdp);
						}
						else{ // add empty panel
							SingleDrawImagePanel empty= new SingleDrawImagePanel(null, "", taskManager, getSelectedFont());
							empty.addMouseListener(this);
							empty.setGridPosition(r, c);
							if(maxImageDimension != null)
								empty.setDimension(maxImageDimension,titlePanelHeight);
							else
								empty.setDimension(panelDimension,titlePanelHeight);
							this.gridPanel.add(empty);

								this.drawImagePanels.add(empty);
						}
					}
				}
				updateGridSize();
				this.revalidate();
				this.repaint();
			
			}
		} catch (Exception e) {
			LOGGER.severe("Error in ImageSetCreator: refreshing ImagePanels in GRID:"+e.getMessage());
			e.printStackTrace();
		}

	}

	/**
	 * Returns a maximum width of column.
	 * @param c int column index
	 * @return int maximum column width
	 */
	private int getMaximumPanelWidthAtColumn(int c){
		int maxWidth=0;
		Iterator<SingleDrawImagePanel> sdpIterator=this.drawImagePanels.iterator();
		while(sdpIterator.hasNext()){
			SingleDrawImagePanel sdp=(SingleDrawImagePanel)sdpIterator.next();
			if(sdp.getImage() != null && sdp.getGridPosition() != null){
				if(c>0){
					if(sdp.getGridPosition()[0] == c && sdp.getPanelSize().width > maxWidth)
						maxWidth=sdp.getPanelSize().width;
				}
				else{ // getting maximum width of all panels which has image
					if(sdp.getPanelSize().height > maxWidth)
						maxWidth=sdp.getPanelSize().width;

				}

			}
		}
		return maxWidth;

	}

	/**
	 * Returns a maximum height of the title of panel.
	 * @return int maximum height of Title panel.
	 */

	private int getMaximumTitlePanelHeightByFontSize(){
		int maxHeight=0;
		Iterator<SingleDrawImagePanel> sdpIterator=this.drawImagePanels.iterator();
		while(sdpIterator.hasNext()){
			SingleDrawImagePanel sdp=(SingleDrawImagePanel)sdpIterator.next();
			if(sdp.getImage() != null){
				 // getting maximum of all panels which has image
					if(sdp.getFont().getSize()+6 > maxHeight)
						maxHeight=sdp.getFont().getSize()+6;
			}
		}
		return maxHeight;


	}
	/**
	 * Returns a maximum height row at grid.
	 * @param r int row number which maximum panel height is looking for. The r < 1 means searching max panel height of all panels.
	 * @return int maximum height of row.
	 */
	private int getMaximumPanelHeightAtRow(int r){
		int maxHeight=0;
		Iterator<SingleDrawImagePanel> sdpIterator=this.drawImagePanels.iterator();
		while(sdpIterator.hasNext()){
			SingleDrawImagePanel sdp=(SingleDrawImagePanel)sdpIterator.next();
			if(sdp.getImage() != null && sdp.getGridPosition() != null){
				if(r>0){
					if(sdp.getGridPosition()[0] == r && sdp.getPanelSize().height > maxHeight)
						maxHeight=sdp.getPanelSize().height;
				}
				else{ // getting maximum of all panels which has image
					if(sdp.getPanelSize().height > maxHeight)
						maxHeight=sdp.getPanelSize().height;

				}

			}

		}
		if(maxHeight>0)
			return maxHeight;
		else return getMaximumPanelHeightAtRow(0); // did't find height at row r -> search from all panels

	}



	/**
	 * Refreshes selected index of comboboxes showing column and row values.
	 */
	private void updateGridComboBoxItems(){
		this.rowBox.setSelectedItem(this.presentRowNumber);

		this.columnBox.setSelectedItem(this.presentColumnNumber);
	}

	/**
	 * Sets selected font to all SingleDrawImagePanel.
	 */
	private void updatePanelFonts(){
		//get selected font and font size
		String fontName = fontsBox.getSelectedItem().toString();
		int fSize=(int)fontSizeBox.getSelectedItem();
        Font font= new Font(fontName, Font.BOLD, fSize);
        
        //set font to all panels
		Iterator<SingleDrawImagePanel> sIterator=drawImagePanels.iterator();
		while(sIterator.hasNext()){
			SingleDrawImagePanel sdp=(SingleDrawImagePanel)sIterator.next();
			sdp.updateFont(font);
		}
	}

	/**
	 * @return Font the selected font
	 */
	private Font getSelectedFont(){
		String fontName = fontsBox.getSelectedItem().toString();
		int fSize=(int)fontSizeBox.getSelectedItem();
		return  new Font(fontName, Font.PLAIN, fSize);

	}

	/**
	 * Calculates how much window has space for ImagePanels. 
	 * @return Dimension overall space left for ImagePanels.
	 */
	private Dimension calculateDrawPanelDimension(){

		int width=(int)((this.backPanel.getWidth()-this.gap*(this.presentColumnNumber+1))/this.presentColumnNumber);
		int height=(int)((this.backPanel.getHeight()-this.gap*(this.presentRowNumber+1)-this.menuBar.getHeight()-this.browsingBackPanel.getHeight())/this.presentRowNumber);
		LOGGER.fine("height"+this.browsingBackPanel.getHeight());
		return new Dimension(width,height);
	}


	/**
	 * Searches first SingleDrawImagePanel which is not positioned yet.
	 * @return SingleDrawImagePanel which is not positioned yet.
	 */
	private SingleDrawImagePanel getFirstUnPositionedSDP(){
		Iterator<SingleDrawImagePanel> sdpIterator=this.drawImagePanels.iterator();
		while(sdpIterator.hasNext()){
			SingleDrawImagePanel sdp=(SingleDrawImagePanel)sdpIterator.next();
			if(sdp.getGridPosition() == null)
				return sdp;
		}
		return null;
	}

	/**
	 * Returns SingleDrawImagePanel at position r,c. 
	 * @param r int row
	 * @param c int column
	 * @return SingleDrawImagePanel at position r,c. If not found, null returned.
	 */
	private SingleDrawImagePanel getSDPatPosition(int r, int c){
		Iterator<SingleDrawImagePanel> sdpIterator=this.drawImagePanels.iterator();
		while(sdpIterator.hasNext()){
			SingleDrawImagePanel sdp=(SingleDrawImagePanel)sdpIterator.next();
			if(sdp.getGridPosition() != null && sdp.getGridPosition()[0] == r && sdp.getGridPosition()[1] == c)
				return sdp;
		}
		return null;
	}


	/**
	 * Imports image files which user has selected and creates @see SingleDrawImagePanel (s) of them.
	 * @param imageFiles list of image File-object
	 */
	private void importImagesFromFiles(File[] imageFiles) {
		try {

			if(imageFiles != null && imageFiles.length>0){
				String path = getFolder(imageFiles[0]);
				if(path != null)
					this.presentFolder=path;

				for (int i = 0; i < imageFiles.length; i++) {
				/*	if(this.cancelImageImport.isCancelled())
						return;
				 */
					if(imageFiles[i] != null){
						BufferedImage image= this.taskManager.createImageFile(imageFiles[i]);
						if(image != null){
							SingleDrawImagePanel sip=new SingleDrawImagePanel(image, imageFiles[i].getName(), taskManager, getSelectedFont());
							sip.addMouseListener(this);
							this.drawImagePanels.add(sip);
						}
					}
				}
				updatePanelFonts();
			}
		} catch (HeadlessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.severe("Could not import all images from files");

			e.printStackTrace();
		}
	}

	/**
	 * Opens dialog window, where user can select ImageLayers and MarkingLayers to be shown in imageset.
	 */
	private void createImagesFromPresentImageLayers(){
		SelectAndCreateImageFiles saci = new SelectAndCreateImageFiles(new JFrame(), this.gui, taskManager.getImageLayerList(), ID.EXPORT_PREVIEW_IMAGES);

		if(saci.getCreatedBufferedImages() != null && saci.getCreatedBufferedImages().size()>0){
			Iterator<BufferedImageWithName> imageIterator = saci.getCreatedBufferedImages().iterator();
			while(imageIterator.hasNext()){
				BufferedImageWithName biwn=imageIterator.next();
				SingleDrawImagePanel sip=new SingleDrawImagePanel(biwn.getImage(), biwn.getImageName(), taskManager, getSelectedFont());
				sip.addMouseListener(this);
				this.drawImagePanels.add(sip);
			}

			updatePanelFonts();
		}


	}



	/**
	 * Returns a String folder path of the given file .
	 * @param file File which folder is viewed
	 * @return String folder path of the given file 
	 */
	private String getFolder(File file){
		if(file.isDirectory()){
			return file.getAbsolutePath();
		}
		else if(file.isFile()){
			return file.getParent();
		}
		return null;
	}

	/**
	 * Sets GridPositions of all DrawImagePanel to null.
	 */
	private void setGridPositionsToNull(){
		Iterator<SingleDrawImagePanel> sdpIterator=this.drawImagePanels.iterator();
		while(sdpIterator.hasNext()){
			SingleDrawImagePanel sdp =sdpIterator.next();


				sdp.setGridPositionNull();
		}
	}
	/**
	 * Removes all SingleDrawImagePanels that has no image.
	 */
	private void removeEmptyPanels(){
		Iterator<SingleDrawImagePanel> sdpIterator=this.drawImagePanels.iterator();
		while(sdpIterator.hasNext()){


			if(sdpIterator.next().getImage() == null){
				sdpIterator.remove();
			}

		}
	}


	/**
	 * Calculates has present grid enough cells for images and if not enough cells will method count new number for rows and columns.
	 * @param rowColumn int[] present row and column number
	 * @param itemNumber int number of overall items
	 * @return int[] new row and column number (may be same as given value if there are enough cells for imagePanels)
	 */
	private int[] calculateEnoughGridPoints(int[] rowColumn, int itemNumber){

		if(rowColumn[0]*rowColumn[1]< itemNumber){
			if(rowColumn[0]>=rowColumn[1]){
				return calculateEnoughGridPoints(new int[]{rowColumn[0], rowColumn[1]+1}, itemNumber);
			}
			else
			return calculateEnoughGridPoints(new int[]{rowColumn[0]+1, rowColumn[1]}, itemNumber);
		}
		else return rowColumn;

	}

	/**
	 * Sets variable of position where ImagePanel is moved to other grid position.
	 * @param movingPosition int[] Position in grid wherefrom panel is moved.
	 */
	public void setMovingPosition(int[] movingPosition) {
		this.movingPosition = movingPosition;
		//System.out.println("setmoving: "+this.movingPosition[0] + " " +this.movingPosition[1]);
	}

	/**
	 * 
	 * Class ComboRenderer for rendering available fonts in Combobox. Shows font name in font style.
	 * @author Antti Kurronen
	 *
	 */
	private class ComboRenderer extends BasicComboBoxRenderer {

        private static final long serialVersionUID = 1L;
        private JComboBox comboBox;
        final DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
        private int row;

        private ComboRenderer(JComboBox fontsBox) {
            comboBox = fontsBox;
        }

        /**
         * Determines the Scrollpane list and selected row for JCombobox.
         */
        private void manItemInCombo() {
            if (comboBox.getItemCount() > 0) {
                final Object comp = comboBox.getUI().getAccessibleChild(comboBox, 0);
                if ((comp instanceof JPopupMenu)) {
                    final JList list = new JList(comboBox.getModel());
                    final JPopupMenu popup = (JPopupMenu) comp;
                    final JScrollPane scrollPane = (JScrollPane) popup.getComponent(0);
                    final JViewport viewport = scrollPane.getViewport();
                    final Rectangle rect = popup.getVisibleRect();
                    final Point pt = viewport.getViewPosition();
                    row = list.locationToIndex(pt);
                }
            }
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (list.getModel().getSize() > 0) {
                manItemInCombo();
            }
            final JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, row, isSelected, cellHasFocus);
            final Object fntObj = value;
            final String fontFamilyName = (String) fntObj;
            setFont(new Font(fontFamilyName, Font.PLAIN, 16));
            return this;
        }
    }

	@Override
	public void mouseClicked(MouseEvent e) {
	/*	// TODO Auto-generated method stub
		if(e.getButton()== MouseEvent.BUTTON2){
			// remove
			Point point = e.getLocationOnScreen();
			System.out.println(point.toString());
			int[] gridPoint=getGridPositionAtLocation(point);
			if(gridPoint != null){
				removePanel(gridPoint);


			}

		} */
	}

	@Override
	public void mousePressed(MouseEvent e) {

		//	System.out.println("pressedMouse");
			// TODO Auto-generated method stub
			Point startPoint = e.getLocationOnScreen();
			int[] gridPoint=getGridPositionAtLocation(startPoint);
			if(gridPoint != null){
				this.movingPosition=gridPoint;

			//	System.out.println("pressedMouse: "+this.movingPosition[0] + " " +this.movingPosition[1]);
				this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
				else
					this.movingPosition=null;

	}

	@Override
	public void mouseReleased(MouseEvent e) {

			Point endPoint = e.getLocationOnScreen();
		//	System.out.println(endPoint.toString());
			int[] gridPoint=getGridPositionAtLocation(endPoint);
			if(gridPoint != null && this.movingPosition != null){
				//System.out.println("swithc points: "+this.movingPosition[0] + "" +this.movingPosition[1]);
				swithcSingleDrawPanels(gridPoint);

			}
			else{
				if(gridPoint == null && this.movingPosition != null) {
					// remove the panel
					removePanel();
				}

			}
				this.movingPosition=null;
				this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
/*
		if(e.getSource() instanceof ImageSetCreator){
			System.out.println("exited imagesetcreator");
			if(this.movingPosition != null){
				removePanel();
			}
		}
*/
	}

	/**
	 *  Removes SingleDrawImagePanel from panel list. This happens when user drags the panel outside of grid. 
	 *  Starting point position of dragging shows which panel is removed.
	 */
	private void removePanel(){
			int selection=ID.UNDEFINED;
			Iterator<SingleDrawImagePanel> sdpIterator=this.drawImagePanels.iterator();
			while(sdpIterator.hasNext()){
				SingleDrawImagePanel sdp =(SingleDrawImagePanel)sdpIterator.next();
				if((sdp.getGridPosition()[0] == this.movingPosition[0] && sdp.getGridPosition()[1] == this.movingPosition[1])){
					if(sdp.getImage() == null){
						this.presentRowNumber-=1;
						updateGridComboBoxItems();
					}

					ShadyMessageDialog dialog = new ShadyMessageDialog(new JFrame(), "Removing ImagePanel", "Remove ImagePanel from ImageSet.", ID.YES_NO, this);
					selection=dialog.showDialog();
					if(selection == ID.YES)
						sdpIterator.remove();
					dialog=null;
				}

			}
			this.movingPosition=null;
			if(selection== ID.YES)
				setImagesToGrid();


	}

	/**
	 * @param p
	 * @return
	 */
	private int[] getGridPositionAtLocation(Point p){

		Iterator<SingleDrawImagePanel> sdpIterator=this.drawImagePanels.iterator();
		while(sdpIterator.hasNext()){
			SingleDrawImagePanel sdp =sdpIterator.next();
		//	System.out.println("Point: "+p.x +" "+p.y+" at: " +sdp.getBounds().toString());
			Rectangle location = new Rectangle(sdp.getLocationOnScreen(), sdp.getPreferredSize());
			if(location.contains(p))

				return sdp.getGridPosition();
		}

		return null;

	}

	@Override
	public void run() {
		try {
				createImage();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}



}