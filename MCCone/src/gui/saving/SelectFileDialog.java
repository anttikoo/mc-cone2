package gui.saving;

import gui.Color_schema;
import gui.ContentPane;
import gui.MouseListenerCreator;
import gui.ShadyMessageDialog;
import gui.file.CSVfilter;
import gui.file.FileManager;
import gui.file.GIFfilter;
import gui.file.JPGfilter;
import gui.file.PNGfilter;
import gui.file.TIFFfilter;
import gui.file.TXTfilter;
import gui.file.XMLfilter;
import information.Fonts;
import information.ID;
import information.PathCount;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import operators.XMLreadManager;

/**
 * Dialog for selecting image files for adding to ImageLayer list or selecting marking (xml-file) for one selected image
 * @author Antti
 *
 */
public class SelectFileDialog extends JDialog{
	final static Logger LOGGER = Logger.getLogger("MCCLogger");
	private SaverDialog markingSaverDialog;
	private JPanel selectFileDialogBackPanel;
	private JFileChooser fileChooser;
	private JButton cancelFileChooserJButton;
//	private int typeOfImport;
	private JButton addFileChooserJButton;
	private String properSavingFileOrFolderPath;
//	private JComponent parentComponent;
	private int fileType;	// ID.EXPORT_IMAGE, ID.SAVE_MARKINGS, ID.FILE_TYPE_CSV or ID.FILE_TYPE_TEXT_FILE
	private SingleImagePanel singleImagePanel;
	private ArrayList<FileFilter> fileFilters;
	private String selectedFilePath;
	public int fileWritingType; // ID.OVERWRITE, ID.APPEND or ID.FILE_NEW_FILE
	//private String fileExtension;
//	private SaverDialog saverDialog;
	private JComponent backPanelComponent;
	private JFrame ownerFrame;
	private String givenFileName="";


	/**
	 * Class constructor for Dialog. The Dialog is JFilechooser which gives to select image files
	 * @param frame owner frame
	 * @param comp parent JComponent which called this constructor
	 */
	public SelectFileDialog(JFrame frame, String path, JComponent backPanel, int fileType){
		super(frame, true);
		try {
			this.ownerFrame=frame;
		//	this.saverDialog=saverDialog;
			this.backPanelComponent=backPanel;
			this.fileType=fileType;
			this.properSavingFileOrFolderPath= path;
			initFileFilters();
			initFileDialog();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}


	private void initFileFilters(){
		this.fileFilters=createFileFilterList(this.fileType);

	}

	public static ArrayList<FileFilter> createFileFilterList(int id){
		ArrayList<FileFilter> filterList=new ArrayList<FileFilter>();
		switch (id) {
		case ID.SAVE_MARKINGS:
			filterList.add(new XMLfilter());
			//this.fileExtension=".xml";
			break;
		case ID.FILE_TYPE_CSV:
			filterList.add(new CSVfilter());
			//this.fileExtension=".csv";
			break;
		case ID.FILE_TYPE_TEXT_FILE:
			filterList.add(new TXTfilter());
			//this.fileExtension=".txt";
			break;
		case ID.EXPORT_IMAGE:
			filterList.add(new GIFfilter());
			filterList.add(new JPGfilter());
			filterList.add(new PNGfilter());
			filterList.add(new TIFFfilter());
			//this.fileExtension=".txt";
			break;

		default:
			break;
		}

		return filterList;


	}




	private void initFileDialog() throws Exception{

	//	this.setBounds(saverDialog.getMarkingSaverBounds()); //sets the size of window same as the parent window size
//		LOGGER.fine("thisbounds: "+addImageLayerDialog.getBounds());
		this.setBounds(ownerFrame.getBounds());
		this.setUndecorated(true); // no titlebar or buttons
		this.setBackground(new Color(0,0,0,0)); // transparent color
		this.setContentPane(new ContentPane());
		this.getContentPane().setBackground(Color_schema.dark_30);
		this.getContentPane().setLayout(new GridBagLayout());

		selectFileDialogBackPanel = new JPanel();
		selectFileDialogBackPanel.setBackground(new Color(0,0,0));
		selectFileDialogBackPanel.setLayout(new BorderLayout());

		selectFileDialogBackPanel.setBorder(BorderFactory.createLineBorder(Color_schema.button_light_border, 5));
		selectFileDialogBackPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		selectFileDialogBackPanel.setAlignmentY(JComponent.CENTER_ALIGNMENT);
		selectFileDialogBackPanel.setMaximumSize(new Dimension((int)(backPanelComponent.getBounds().getWidth()*0.95), (int)(backPanelComponent.getBounds().getHeight()*0.95)));
		selectFileDialogBackPanel.setMinimumSize(new Dimension((int)(backPanelComponent.getBounds().getWidth()*0.7), (int)(backPanelComponent.getBounds().getHeight()*0.5)));
		selectFileDialogBackPanel.setPreferredSize(new Dimension((int)(backPanelComponent.getBounds().getWidth()*0.7), (int)(backPanelComponent.getBounds().getHeight()*0.7)));
//		LOGGER.fine("openDialogBackPanel width pref: " +openDialogBackPanel.getPreferredSize().getWidth());
		if(selectFileDialogBackPanel.getPreferredSize().getWidth()<500)
			selectFileDialogBackPanel.setPreferredSize(new Dimension((int)(backPanelComponent.getBounds().getWidth()*0.95), (int)(backPanelComponent.getBounds().getHeight()*0.95)));
//		LOGGER.fine("openDialogBackPanel width pref: " +backPanel.getPreferredSize().getWidth());

		JPanel fileChooserPanel = initFileChooserPanel();
		selectFileDialogBackPanel.add(fileChooserPanel, BorderLayout.CENTER);
		initKeyListenerFileselection();
		this.add(selectFileDialogBackPanel);
		this.validate();
		this.repaint();
	}

	/**
	 * @return
	 */
	private JPanel initFileChooserPanel() throws Exception{
		try {

			JPanel fBackPanel = new JPanel();
			fBackPanel.setLayout(new BorderLayout());
			fBackPanel.setBackground(Color_schema.dark_40);
			fBackPanel.setLayout(new BorderLayout());
			fBackPanel.setMaximumSize(backPanelComponent.getMaximumSize());
			fBackPanel.setBorder(BorderFactory.createLineBorder(Color_schema.button_light_border, 1));

			JPanel upPanel = new JPanel();
			upPanel.setBackground(Color_schema.dark_30);
			upPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			upPanel.setMaximumSize(new Dimension(100,40));
			upPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
			upPanel.setBorder(BorderFactory.createEmptyBorder());

			JLabel titleLabel = new JLabel("SELECT FILE");

			titleLabel.setFont(Fonts.b20);
			titleLabel.setForeground(Color_schema.white_230);
			upPanel.add(titleLabel);
			fBackPanel.add(upPanel, BorderLayout.PAGE_START);

			// CENTER PANEL
			JPanel centerPanel=new JPanel();
			centerPanel.setBackground(Color_schema.dark_40);
			centerPanel.setLayout(new BorderLayout());
			centerPanel.setBorder(BorderFactory.createEmptyBorder());

			// setup filechooser panels and JFilechooser
			JPanel imageChooserPanel = new JPanel();
			imageChooserPanel.setLayout(new BorderLayout(5,5));
			imageChooserPanel.setMinimumSize(new Dimension(((int)(selectFileDialogBackPanel.getMinimumSize().getWidth()*0.9)), ((int)(selectFileDialogBackPanel.getMinimumSize().getHeight()*0.7))));

			imageChooserPanel.setBorder(BorderFactory.createLineBorder(Color_schema.dark_40, 3));
			imageChooserPanel.setBackground(Color_schema.dark_40);

			fileChooser = new JFileChooser();

			fileChooser.setBorder(BorderFactory.createLineBorder(Color_schema.button_light_border, 2));
			fileChooser.setBackground(Color_schema.dark_40);
			fileChooser.setControlButtonsAreShown(false);
			fileChooser.setMultiSelectionEnabled(false); // several files are not allowed to select
			// set file filters for fileChooser
			addFileFilters();


			// add mouse listener to get select the the file by double click
			addActionListenerToFileChooser();
			// set the image file folder as current folder of jfilechooser
			setCurrentFileOrFolder();


			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.setMaximumSize(new Dimension(1000,800)); // not so nice when going too large
			fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
			setPropertyChangeListenerToFileChooser();
			imageChooserPanel.add(fileChooser, BorderLayout.CENTER);
			centerPanel.add(imageChooserPanel, BorderLayout.CENTER);

			// Setup buttons for adding file / cancel
			JPanel chooserButtonPanel = new JPanel();
			chooserButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			chooserButtonPanel.setBackground(Color_schema.dark_40);
			chooserButtonPanel.setPreferredSize(new Dimension(50,40));
			chooserButtonPanel.setMinimumSize(new Dimension(50,40));
			chooserButtonPanel.setMaximumSize(new Dimension(2000,40));
			chooserButtonPanel.setBorder(BorderFactory.createEmptyBorder());

			addFileChooserJButton = new JButton("SELECT");
			addFileChooserJButton.setPreferredSize(new Dimension(150,30));
		//	addFileChooserJButton.setBackground(Color_schema.color_dark_20_bg);
		//	addFileChooserJButton.setForeground(Color_schema.color_white_title_230_fg);
			addFileChooserJButton.setFocusable(true);
			addActionsToFileDialogButtons(addFileChooserJButton);	// setup action when pressed
			MouseListenerCreator.addMouseListenerToNormalButtons(addFileChooserJButton); // setup color and border changes when button pressed

			cancelFileChooserJButton = new JButton("CANCEL");
			cancelFileChooserJButton.setPreferredSize(new Dimension(150,30));
			cancelFileChooserJButton.setBackground(Color_schema.dark_20);
			cancelFileChooserJButton.setForeground(Color_schema.orange_dark);
			cancelFileChooserJButton.setFocusable(false);
			cancelFileChooserJButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					fileWritingType=ID.CANCEL;
				hideDialog();
				}
			});


			MouseListenerCreator.addMouseListenerToCancelButtons(cancelFileChooserJButton);



		chooserButtonPanel.add(addFileChooserJButton);
		chooserButtonPanel.add(cancelFileChooserJButton);

		centerPanel.add(chooserButtonPanel, BorderLayout.PAGE_END);


			fBackPanel.add(centerPanel, BorderLayout.CENTER);

			return fBackPanel;
		} catch (Exception e) {
			LOGGER.severe("Error in creating openfiledialog:  " +e.getClass().toString() + " :" +e.getMessage() + " line: " +e.getStackTrace()[2].getLineNumber());
			return null;
		}
	}
	
	private Component findJList(Component comp) {
	    if (comp instanceof JList) return comp;
	    if (comp instanceof Container) {
	        Component[] components = ((Container)comp).getComponents();
	        for(int i = 0; i < components.length; i++) {
	            Component child = findJList(components[i]);
	            if (child != null) System.out.println(child.toString());
	        }
	    }
	    return null;
	}
	
	private void setPropertyChangeListenerToFileChooser(){
		this.fileChooser.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if(evt.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)){
				File file = (File)evt.getNewValue();
				System.out.println(file.getName());
				//givenFileName= evt.getNewValue().toString();
				//System.out.println(givenFileName);
				}
			}
		});
		
		
		
	}

	private void addFileFilters(){
		//boolean hasSetFileFilter=false;
		for (Iterator<FileFilter> iterator = fileFilters.iterator(); iterator.hasNext();) {
			FileFilter filter = (FileFilter) iterator.next();
			fileChooser.addChoosableFileFilter(filter);

			fileChooser.setFileFilter(filter); // sets the last filefilter as selected



		}
	}

	public void setCurrentFileOrFolder(){
		if(this.properSavingFileOrFolderPath != null && this.properSavingFileOrFolderPath.length()>0){
			File f=new File(this.properSavingFileOrFolderPath);
			if(f.isDirectory())
			fileChooser.setCurrentDirectory(f);
			else{
				if(f.isFile() && f.exists() && fileChooser.accept(f)){

						fileChooser.setSelectedFile(f);
						showSelectedFileAtTextField();
				}
				else{
					f=f.getParentFile();
					if(f.exists()){
						fileChooser.setCurrentDirectory(f);
					}
					else{
						File folder=new File(System.getProperty("user.home"));
						if(folder.exists())
						fileChooser.setCurrentDirectory(folder);
					}
				}
			}

		}
		else{
			File folder=new File(System.getProperty("user.home"));
			if(folder.exists())
			fileChooser.setCurrentDirectory(folder);
		}
	}

	private void addActionListenerToFileChooser(){
		ActionListener actionListener =new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fChooser=(JFileChooser)e.getSource();
				String commandString = e.getActionCommand();
				if(commandString.equals(JFileChooser.APPROVE_SELECTION)){
					if(fChooser.getSelectedFile().isDirectory()){

					}
					else{
						fileSelected();
					}
				}
				else{
					
					
				}

			}
		};
		fileChooser.addActionListener(actionListener);
	}

	public void showDialog(){
		this.setVisible(true);
		//return fileWritingType;
	//	this.repaint();
	}

	public ArrayList<FileFilter> getFileFilters(){
		return this.fileFilters;
	}

	private void showSelectedFileAtTextField(){
		if(fileChooser.getSelectedFile() != null && fileChooser.getSelectedFile().isFile() &&
				fileChooser.getSelectedFile().getName().length()>0)
		((JTextField)((Container)((Container)fileChooser.getComponent(3)).getComponent(0)).getComponent(1)).setText(fileChooser.getSelectedFile().getName());
	}

	private void addActionsToFileDialogButtons(JButton button) throws Exception{
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fileSelected();
			}
		});
	}

	/**
	 *  Checks the given file and saves the path and fileWritingType. If file extension is not given -> adds the extension
	 */
	private void fileSelected(){
		try {
			boolean newFile = false;
			File file=fileChooser.getSelectedFile();
			
			
			String fileNameText =givenFileName;
			
			if(((JTextField)((Container)((Container)fileChooser.getComponent(3)).getComponent(0)).getComponent(1)).getText().trim() != null)
			fileNameText = ((JTextField)((Container)((Container)fileChooser.getComponent(3)).getComponent(0)).getComponent(1)).getText().trim();
			String finalFileNamePath=null;
			if(file != null && fileNameText.equals(file.getName())){
				finalFileNamePath=file.getAbsolutePath();
			}
			else
			 // create file
			if(fileNameText != null && fileNameText.length()>0 && (file == null || !fileNameText.equals(file.getName()))){


					LOGGER.fine("curr "+fileChooser.getFileFilter().getDescription());
				//	LOGGER.fine("filename: "+fileChooser.getCurrentDirectory().getAbsolutePath()+"/"+fileNameText+".XML");

					File f=new File(fileChooser.getCurrentDirectory()+"/"+fileNameText);
					String fileName;
					if(!f.getAbsolutePath().endsWith(fileChooser.getFileFilter().getDescription())){
						String fixedName=fixFileName(f.getAbsolutePath());
						fileName =  fixedName+fileChooser.getFileFilter().getDescription();
						LOGGER.fine(fileName);

						f= new File(fileName);
						if(!f.exists()){
						//	f.createNewFile();
							newFile=true;
						}
					}
					else{ // has extension
						fileName =  f.getAbsolutePath();
						LOGGER.fine(fileName);
						f= new File(fileName);
						if(!f.exists()){
						//	f.createNewFile();
							newFile=true;
						}
					}

					if(fileName.contains(".") )
							if( FileManager.accept(fileName, fileChooser.getFileFilter())){
						//	LOGGER.fine("accepted: " +f.getAbsolutePath());
						//	file=new File(f.getPath());
						finalFileNamePath=fileName;
					}
			}

	//				if (file != null && file.isFile() && fileChooser.accept(file)) {
			if (finalFileNamePath != null) {
					if(fileType == ID.SAVE_MARKINGS){ // saving markings to xml-file
						boolean hasFoundImageLayer=false;
						if(!newFile){
							hasFoundImageLayer=hasImageLayersFound(finalFileNamePath); // checks the file if imagelayers has found
							LOGGER.fine("foundImagelayers: "+hasFoundImageLayer);
						}
						if (!hasFoundImageLayer || showOverwriteMessage("Given ImageLayer(s) found from XML-file!", "Update information of ImageLayer(s)?") ){ // if file exists should it be overwritten

								setSelectedFilePath(finalFileNamePath);
								if(newFile)
									setFileWritingType(ID.FILE_NEW_FILE);
								else
									setFileWritingType(ID.OVERWRITE);

						}else{ // user don't want to overwrite
							//showFileMessage("File not overwritten", "Select new File.");
							return;
						}
					} else if (fileType == ID.EXPORT_IMAGE) { // exporting image
						if (!newFile && showOverwriteMessage("Image file exists!", " Overwrite?") || newFile){ // if file exists should it be overwritten

							setSelectedFilePath(finalFileNamePath);
							if(newFile)
								setFileWritingType(ID.FILE_NEW_FILE);
							else
								setFileWritingType(ID.OVERWRITE);

					}else{
					//	showFileMessage("File not overwritten", "Select new File.");
						return;
					}

					}
					else { // Export result to file (ID.FILE_TYPE_CSV or ID.FILE_TYPE_TEXT_FILE)
						if(!newFile){
							int selection = showAppendOverwriteMessage();
							if(selection == ID.CANCEL){
							//	showFileMessage("File not modified", "Select new File.");
								return;
							}
							else{
								setFileWritingType(selection);
							}
						}
						setSelectedFilePath(finalFileNamePath);

					}
			} else{
				showFileMessage("File not valid", "Select new File.");
				LOGGER.warning("Given file is not accepted");
				return;
			}
			hideDialog();
		} catch (Exception e) {
			LOGGER.severe("Error in selecting file for markings: " +e.getMessage());
			setFileWritingType(ID.ERROR);
			setSelectedFilePath(null);
			showFileMessage("Error in selecting file", "Try again.");
			hideDialog();
		}
	}

public void setProperFilePathForSaving(String path){
	this.properSavingFileOrFolderPath=path;
	setCurrentFileOrFolder();
}

private String fixFileName(String fileName){
	if(fileName != null && fileName.length()>1 && fileName.contains(".")){
		int index=fileName.indexOf(".");
		return fileName.substring(0, index-1);


	}
	return fileName;
}

public int getFileWritingType(){
	return this.fileWritingType;
}
/*
public void setMultiFileSelectionEnabled(boolean b){
	this.fileChooser.setMultiSelectionEnabled(b);
}
*/
	private boolean showOverwriteMessage(String title, String text){
		// confirm overwrite
		ShadyMessageDialog dialog = new ShadyMessageDialog(new JFrame(), title, text, ID.YES_NO, this);
		if(dialog.showDialog() == ID.YES){
			dialog=null;
			return true;
		}
		dialog=null;
		return false;
	}

	private int showAppendOverwriteMessage(){
		// confirm overwrite
		ShadyMessageDialog dialog = new ShadyMessageDialog(new JFrame(), "File exists", "Append or Overwrite?", ID.APPEND_OVERWRITE_CANCEL, this);
		return dialog.showDialog();


	}

	private void showFileMessage(String title, String message){
		ShadyMessageDialog dialog = new ShadyMessageDialog(new JFrame(), title, message, ID.OK, this);
		dialog.showDialog();
		dialog=null;
	}

	private void showNotOverWritingFileMessage(){
		ShadyMessageDialog dialog = new ShadyMessageDialog(new JFrame(), "File not overwritten", "Select new file", ID.OK, this);
		dialog.showDialog();
		dialog=null;
	}

	private void setFileWritingType(int id){
		this.fileWritingType=id;
	}

	private void setSelectedFilePath(String path){
		this.selectedFilePath=path;
	}

	private void initKeyListenerFileselection(){

		InputMap inputMap= (this.selectFileDialogBackPanel).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(KeyStroke.getKeyStroke("pressed ENTER"), "enter_pressed");
		ActionMap actionMap = 	(this.selectFileDialogBackPanel).getActionMap();
		actionMap.put("enter_pressed", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				addFileChooserJButton.doClick();

			}

		});
	}

	 private JTextField getTexField(Container container, String childCounter,int depth) {
		 LOGGER.fine("children: " +container.getComponentCount());
		 int comps= container.getComponentCount()-1;
         for (int i = 0; i < container.getComponentCount(); i++) {
        	 childCounter+= " "+i+"/"+comps+ " d"+depth;
             Component child = container.getComponent(i);
             if (child instanceof JTextField) {
            	 LOGGER.fine("textfield: "+childCounter+ " "+((JTextField) child).getText()+ "depth: " + depth);

                 return (JTextField) child;
             } else{
                 JTextField field = getTexField((Container) child,childCounter, depth+1);
                 if (field != null) {
                     return field;
                 }
             }
         }
         return null;
     }

	 protected boolean hasImageLayersFound(String fileName){
			return true; // implemented in extended class
		}

	 public void setImageLayerNamesForXMLSearch(ArrayList<String> list){
			// implement the code in extended class
		}



	private String getMostCommonPath(File[] fileGroup){
		try {
			ArrayList<PathCount> countedPathList = new ArrayList<PathCount>();
			PathCount biggestPathCount = null;
			if(fileGroup != null && fileGroup.length>0){

				for (int j = 0; j < fileGroup.length; j++) {
					if(fileGroup[j] != null){
						if(fileGroup[j].exists()){ // file exist
							String folderPath="";
							if(fileGroup[j].isDirectory()){ // is folder
								folderPath=fileGroup[j].getAbsolutePath();
							}
							else if(fileGroup[j].isFile()){ // is file -> get folder
								folderPath= (getFolder(fileGroup[j].getAbsolutePath())).getAbsolutePath();
							}
							// add folder to list -> returns the PathCount object with highest count value
							biggestPathCount = updatePathCount(countedPathList, folderPath, biggestPathCount);
						}
					}
				}
			}
			else{
				return null;
			}

			return biggestPathCount.getFolderPath();
		} catch (Exception e) {
			LOGGER.severe("Error in gettin most common folderpath " +e.getClass().toString() + " :" +e.getMessage());
			return null;
		}

	}

	/**
	 * Adds or updates PathCount objects of array which is used to calculate occurences of folder paths
	 * @param countList the array of PathCount objects where the occurences of paths are saved
	 * @param folderPath the path of folder which is added to array
	 * @return The PathCount object which has biggest count value by this far
	 */
	private PathCount updatePathCount(ArrayList<PathCount> countList, String folderPath,PathCount biggestCount){


		try {
			if(folderPath != null && folderPath.length()>2){
				if(countList.size()>0){
					boolean foundPath = false;
					Iterator<PathCount> iterator = countList.iterator();
					while(iterator.hasNext()){
						PathCount pc= iterator.next();
						if(pc.getFolderPath().equals(folderPath)){
							pc.addOne(); // increase the count value
							foundPath=true;
							if(biggestCount == null || biggestCount.getCount()<pc.getCount())
								biggestCount=pc;
						}

					}
					if(!foundPath) {// path not found -> add new PathCount object
						PathCount pcount= new PathCount(folderPath);
						countList.add(pcount);
						if(biggestCount == null || biggestCount.getCount()<pcount.getCount())
							biggestCount=pcount;
						}

				}
				else{ // add first object to list
					PathCount pcount= new PathCount(folderPath);
					countList.add(pcount);
					biggestCount=pcount;

				}
			}
			return biggestCount;
		} catch (Exception e) {
			LOGGER.severe("Error in updatePathCount " +e.getClass().toString() + " :" +e.getMessage());
			return null;
		}
	}

	/**
	 * Converts given File to directory if it is not already one.
	 * @param f the file that may be file or folder
	 * @return the folder of given file
	 */
	private File getFolder(String filePath){
		try {
			File f = new File(filePath);
			if (f.exists()) {
				if (f.isDirectory()) {
					// return the folder
					return f;
				} else if (f.isFile()) {
					// the folderPath was a file -> get the folder of that file
					return new File(f.getParent());
				}
			}
			// file not exists return the home folder
			return new File(System.getProperty("user.home"));
		} catch (Exception e) {
			LOGGER.severe("Error in getting folder of file " +e.getClass().toString() + " :" +e.getMessage()+ " line: "+ e.getStackTrace()[2].getLineNumber());
			return null;
		}
	}

	private void hideDialog(){
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				hideThis();

			}
		});
	}

	private void hideThis(){

		this.setVisible(false); // close dialog
		this.dispose();
	}

	public String getSelectedFilePath() {
		return this.selectedFilePath;
	}
/*
	public void setExportingPath(String ePath) {
		exportingPath = ePath;
	}
*/
}
