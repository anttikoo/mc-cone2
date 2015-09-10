package gui.saving;

import gui.saving.SaverDialog;
import gui.Color_schema;
import gui.GUI;
import gui.saving.SelectFileDialog;
import gui.MouseListenerCreator;
import gui.ShadyMessageDialog;
import information.ID;
import information.ImageLayer;
import information.LayersOfPath;
import information.MarkingLayer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class ExportResults extends SaverDialog{ // implements ActionListener
	//private ButtonGroup buttonGroup;
	private int fileType=ID.FILE_TYPE_TEXT_FILE;
	private ByteArrayOutputStream byteStream;
	private String csvString="csv";
	private String txtString="txt";
	private String clipboardString="clipboard";
	private JLabel savingPathJLabel;



	/**
	 * @param frame
	 * @param gui
	 * @param iList
	 * @param eID Integer ID for export type: ID.FILE_TYPE_TEXT_FILE, ID.FILE_TYPE_CSV or ID.CLIPBOARD
	 */
	public ExportResults(JFrame frame, GUI gui, ArrayList<ImageLayer> iList, int eID){
		super(frame, gui, iList, eID);




	}


	protected SingleImagePanel createSingleImagePanel(ImageLayer layer){
		return new ExSingleImagePanel(layer,this);
	}

	protected JPanel initBrowsingPanelWithLabel() throws Exception{
		return initBrowsingPanel(this.savingType, "Browse");
	}

	protected void setSaveButtonEnabledByFileValidity(int fileValidity){
		// check
		if(fileValidity == ID.FILE_OK || fileValidity == ID.FILE_NEW_FILE)
			this.saveJButton.setEnabled(true);
		else
			this.saveJButton.setEnabled(true);
	}


	protected JPanel initBrowsingPanel(int exportID, String label){
		try {
			JPanel downBrowsingPanel = new JPanel();
			downBrowsingPanel.setLayout(new BoxLayout(downBrowsingPanel, BoxLayout.LINE_AXIS));
			downBrowsingPanel.setBackground(Color_schema.dark_30);
			downBrowsingPanel.setMaximumSize(new Dimension(5000, 50));
			downBrowsingPanel.setPreferredSize(new Dimension(800, 50));
			downBrowsingPanel.setMinimumSize(new Dimension(200, 50));

			JLabel saveToLabel = new JLabel("Save to file: ");
			saveToLabel.setFont(new Font("Consolas", Font.BOLD,16));
			downBrowsingPanel.add(Box.createRigidArea(new Dimension(20,0)));
			downBrowsingPanel.add(saveToLabel);
			downBrowsingPanel.add(Box.createRigidArea(new Dimension(20,0)));
			savingPathJLabel = new JLabel("");
			savingPathJLabel.setFont(new Font("Consolas", Font.PLAIN,16));
			downBrowsingPanel.add(savingPathJLabel);
			downBrowsingPanel.add(Box.createHorizontalGlue());

//	addImagesJPanel.setMaximumSize(new Dimension(400,40));
			// Select file path for all ImageLayers
			selectFileJButton = new JButton(label);
			selectFileJButton.setPreferredSize(new Dimension(150,30));
			selectFileJButton.setMaximumSize(new Dimension(150,30));
			selectFileJButton.setContentAreaFilled(false);
			selectFileJButton.setBackground(Color_schema.dark_20);
			selectFileJButton.setFont(new Font("Consolas", Font.BOLD,16));
		//	initActionsToButtons(selectFileJButton, ID.OPEN_MARKING_FILE);
			selectFileJButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {

					try {
						selectPathForAllSingleImagePanels();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
			selectFileJButton.setFocusable(false);
			MouseListenerCreator.addMouseListenerToNormalButtons(selectFileJButton);
			downBrowsingPanel.add(Box.createRigidArea(new Dimension(20,0)));
			downBrowsingPanel.add(selectFileJButton);
			downBrowsingPanel.add(Box.createRigidArea(new Dimension(20,0)));

		//	browsingJPanel.add(downBrowsingPanel, BorderLayout.PAGE_END);
		//	browsingJPanel.validate();
			if(exportID != ID.CLIPBOARD)
				return downBrowsingPanel;
			else
				return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.fine("Error initializing BrowsingPanel: "+ e.getMessage());
			e.printStackTrace();
			return null;
		}
	}




		protected void selectPathForAllSingleImagePanels() throws Exception{
			if(this.selectFileDialog == null)
				initSelectFileDialog();
			//get properpath of first SingleImagePanel
			String path = getfirstProperPathOfSingleImagePanels();
			this.selectFileDialog.setProperFilePathForSaving(path);

		//	this.selectFileDialog.setMultiFileSelectionEnabled(false);
			this.selectFileDialog.setVisible(true);
			if(this.selectFileDialog.fileWritingType != ID.CANCEL && this.selectFileDialog.fileWritingType != ID.ERROR){


				setExportingPath(this.selectFileDialog.getSelectedFilePath());
				setFileWritingType(this.selectFileDialog.getFileWritingType());
				// refresh backgroundColors of MarkingLayers
				setMarkingLayerBackgroundsToDefault(ID.IMAGELAYER_UNDEFINED);

			}
			this.selectFileDialog.setVisible(false);
			this.selectFileDialog.dispose();

		}


		protected void initSelectFileDialog() throws Exception{
			String path = System.getProperty("user.home");
			this.selectFileDialog = new SelectFileDialog(this.gui, path, this.backPanel, this.savingType);
		}



	protected void setExportingPath(String ePath) {
		int fileValidity = checkFileValidity(new File(ePath));

		informUserFromFileValidity(fileValidity, savingPathJLabel, true);

		savingPathJLabel.setText(ePath.trim());
	}

	protected JPanel initImageViewPanelWithTitle(){
		return initImageViewPanel(getTitleString(this.savingType));
	}

	protected boolean isFilePathPanelShown(){
		return false;
	}

	private String getTitleString(int id){
		if (id== ID.FILE_TYPE_TEXT_FILE)
			return "EXPORT RESULTS TO TAB-DELIMITED TEXT FILE";
		else if (id== ID.FILE_TYPE_CSV)
				return "EXPORT RESULTS TO CSV-FILE";
			else if (id== ID.CLIPBOARD)
				return "EXPORT RESULTS TO CLIPBOARD";
			else if (id== ID.EXPORT_IMAGE)
				return "EXPORT IMAGES WITH MARKINGS";
		return null;


	}

	protected JPanel initSaveButtonWithTitle() throws Exception{
		return initSaveButton("Export results");
	}





	protected void startSavingProcess(int exportID, int exportType){
		try {
			setAllMarkingLayerBackgroundsToDefault();
			boolean successFullyExported = false;
			ArrayList<Integer> successfullIDs=new ArrayList<Integer>();
			LOGGER.fine("exportID:"+exportID);
			byteStream=new ByteArrayOutputStream();
			if(exportType==ID.APPEND)
				writeEmptyLine();
				//go through all panels and pick imageLayers and MarkingLayers which will be saved
				Component[] imPanelList= imageScrollPanel.getComponents();
				if(imPanelList != null && imPanelList.length>0 ){
					// create new LayersOfPath objects and add them to list
					 for (int i = 0; i < imPanelList.length; i++) {
						SingleImagePanel imp= (SingleImagePanel)imPanelList[i];
						ArrayList<MarkingLayer> selectedMarkingLayers = imp.getAllSelectedMarkingLayers();
						if(selectedMarkingLayers != null && selectedMarkingLayers.size()>0){
							if(exportID==ID.FILE_TYPE_CSV){
								if(i==0  && exportType != ID.APPEND)
								writeCSVLineTitle();
							}
							else if(exportID == ID.FILE_TYPE_TEXT_FILE || exportID == ID.CLIPBOARD){
								if(i==0  && exportType != ID.APPEND)
									writeTXTorClipboardTitle();
								writeTXTorClipboardLine(imp.getImageLayerName());
							}


							for (Iterator<MarkingLayer> iterator = selectedMarkingLayers.iterator(); iterator.hasNext();) {
								MarkingLayer mLayer = (MarkingLayer) iterator.next();
								if(exportID==ID.FILE_TYPE_CSV){
										writeCSVLine(imp.getImageLayerName(), mLayer.getLayerName(), mLayer.getCounts());
								}
								else if(exportID == ID.FILE_TYPE_TEXT_FILE || exportID == ID.CLIPBOARD){
									writeTXTorClipboardLine(mLayer.getLayerName(), mLayer.getCounts());
									}

								successfullIDs.add(mLayer.getLayerID());
							}
							if(i<imPanelList.length-1 && hasSelectedMarkinglayersInBelowImageLayers(i+1) && exportID != ID.FILE_TYPE_CSV){
								writeEmptyLine();
								writeEmptyLine();
							}

						}

					 }

					 // write the byte array to file

						 if(exportID == ID.FILE_TYPE_TEXT_FILE || exportID == ID.FILE_TYPE_CSV ) {
							successFullyExported= writeByteArrayToFile(exportType);
					 	}
						 else{
							 successFullyExported = copyToClipboard();

						 }


					 byteStream.close();
					 if(successFullyExported){
						 setSuccesfullSavingBackgrounds(successfullIDs);
						 ShadyMessageDialog dialog = new ShadyMessageDialog(new JFrame(), "Exporting succesfull", "Successfully exported MarkingLayers are at green background.  ", ID.OK, this);
						 dialog.showDialog();
						 dialog=null;
					 }
					 else{
						 ShadyMessageDialog dialog = new ShadyMessageDialog(new JFrame(), "Exporting not succesfull", "Exporting not successfull propably due to unacceptable file path.", ID.OK, this);
						 dialog.showDialog();
					 }
				}


			} catch (Exception e) {
				LOGGER.severe("Error in Exporting results: "+ e.getMessage());
			}
	}

	private boolean hasSelectedMarkinglayersInBelowImageLayers(int startIndex){
		Component[] imPanelList= imageScrollPanel.getComponents();
		for (int i = startIndex; i < imPanelList.length; i++) {
			SingleImagePanel imp= (SingleImagePanel)imPanelList[i];
			ArrayList<MarkingLayer> selectedMarkingLayers = imp.getAllSelectedMarkingLayers();
			if(selectedMarkingLayers != null && selectedMarkingLayers.size()>0)
				return true;
		}
		return false;
	}

	private boolean writeByteArrayToFile(int exportType) throws Exception{

		 if(this.savingPathJLabel != null && this.savingPathJLabel.getText() != null && this.savingPathJLabel.getText().length()>0){
			 boolean append=false;
			if(exportType == ID.APPEND){
				append=true;
			}
				// write to file
				 PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(this.savingPathJLabel.getText(), append)));
				    out.println(byteStream.toString());
				    out.close();


/*
				FileOutputStream fStreamOut = new FileOutputStream(this.savingPathJLabel.getText());
				fStreamOut.write(byteStream.toByteArray());
				fStreamOut.flush();
				fStreamOut.close();
*/
				    return true; // succesfull
		 }
		 else{
			 LOGGER.warning("Exporting not succesfull: file not valid!");
			 return false;
		 }

	}

	private void writeCSVLineTitle() throws Exception{
		String line = "ImageLayer,MarkingLayer,Counts";
		byteStream.write(line.getBytes());

	}

	private void writeCSVLine(String imageLayerName, String markingLayerName, int counts) throws Exception{
		String line = "\n"+imageLayerName + ","+markingLayerName+","+counts;
		byteStream.write(line.getBytes());

	}

	private void writeTXTorClipboardLine(String markingLayerName, int counts) throws Exception{
		String line = "\n"+markingLayerName+"\t"+counts;
		byteStream.write(line.getBytes());

	}

	private void writeTXTorClipboardLine(String imageLayerName) throws Exception{
		String line = imageLayerName;
		byteStream.write(line.getBytes());

	}

	private void writeTXTorClipboardTitle() throws Exception{
		String line = "Layers"+ "\t" +"Counts"+"\n";
		byteStream.write(line.getBytes());

	}


	public String getSelectedButtonText(ButtonGroup buttonGroup) {
        for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();

            if (button.isSelected()) {
            	LOGGER.fine("found selected: "+ button.getActionCommand());
                return button.getActionCommand();
            }
        }

        return null;
    }

	private void writeEmptyLine() throws Exception{
		String line = "\n";
		byteStream.write(line.getBytes());
	}

	private boolean copyToClipboard() throws Exception{
		String myString = byteStream.toString();
		if(myString != null && myString.length()>0){
			StringSelection stringSelection = new StringSelection (myString);
			Clipboard clpbrd = Toolkit.getDefaultToolkit ().getSystemClipboard ();
			clpbrd.setContents (stringSelection, null);
			return true;
		}
		return false;
	}

/*
	@Override
	public void actionPerformed(ActionEvent e) {

		LOGGER.fine("action command"+ e.getActionCommand());
	}
*/
	protected String getProperFilePathForSaving(ImageLayer iLayer){
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








}
