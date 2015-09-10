package gui.file;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;

public class OpenImageFilesDialog extends OpenFileDialog{

	public OpenImageFilesDialog(JFrame frame, Rectangle pcb, Rectangle pcbb, String presentFolder) {
		super(frame, pcb, pcbb, presentFolder);
		// TODO Auto-generated constructor stub
	}

	protected String getWindowTitle(){
		return "SELECT IMAGES";
	}

	protected void setUpFilechooserSettings(){

		fileChooser.setMultiSelectionEnabled(true); // multiselection is allowed
		fileChooser.setFileFilter(new ImageFilter()); // Image filter in package gui.file
		// set the current folder of jfilechooser
		File f=getFolder(this.presentFolder);
		if(f != null){
			fileChooser.setCurrentDirectory(f);
		}
	}

	protected void addActionsToFileDialogButtons(JButton button){
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(fileChooser.getSelectedFiles() != null && fileChooser.getSelectedFiles().length>0){
					hideDialog();
				//	LOGGER.fine("selected file and closing file dialog: " +(fileChooser.getSelectedFiles()[0]).getName());
				//	addImageLayerDialog.addImagesToImageLayerList(fileChooser.getSelectedFiles());
					selectedFiles=fileChooser.getSelectedFiles();
					// update present folder to ->-> informationCenter
					String commonFolder = getMostCommonPath(fileChooser.getSelectedFiles());
					if(commonFolder != null && commonFolder.length()>0){
						File file = new File(commonFolder);
						if(file.exists() && file.isDirectory()) // folder exists
							setPresentFolder(commonFolder);
					}

				}
			}
		});


	}

}
