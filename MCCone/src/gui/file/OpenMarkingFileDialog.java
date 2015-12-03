package gui.file;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 * The Class OpenMarkingFileDialog.
 */
public class OpenMarkingFileDialog extends OpenFileDialog{

	public OpenMarkingFileDialog(JFrame frame, Rectangle pcb, Rectangle pcbb, String presentFolder) {
		super(frame, pcb, pcbb, presentFolder);
		
	}
	
	/**
	 * Instantiates a new open marking file dialog.
	 *
	 * @param d the d
	 * @param pcb the pcb
	 * @param pcbb the pcbb
	 * @param presentFolder the present folder
	 */
	public OpenMarkingFileDialog(JDialog d, Rectangle pcb, Rectangle pcbb, String presentFolder) {
		super(d, pcb, pcbb, presentFolder);
		
	}

	/* (non-Javadoc)
	 * @see gui.file.OpenFileDialog#getWindowTitle()
	 */
	protected String getWindowTitle(){
		return "OPEN XML FILE FOR IMPORTING MARKINGS";
	}

	/* (non-Javadoc)
	 * @see gui.file.OpenFileDialog#setUpFilechooserSettings()
	 */
	protected void setUpFilechooserSettings(){
		fileChooser.setMultiSelectionEnabled(false); // only one file is allowed
		fileChooser.setFileFilter(new XMLfilter()); // XML filter in package gui.file
		// set the image file folder as current folder of jfilechooser
		File f=getFolder(this.presentFolder);
		if(f != null){
			fileChooser.setCurrentDirectory(f);
		}
	}

	/* (non-Javadoc)
	 * @see gui.file.OpenFileDialog#addActionsToFileDialogButtons(javax.swing.JButton)
	 */
	protected void addActionsToFileDialogButtons(JButton button){
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			if(fileChooser.getSelectedFile() != null){
				hideDialog();
				selectedFiles=new File[] {fileChooser.getSelectedFile()};
				// update present folder to ->-> informationCenter (MAYBE BETTER NOT TO UPDATE present folder with MARKINGS PATH)
				//setPresentFolder(getFolder(fileChooser.getSelectedFile().getAbsolutePath()).getAbsolutePath());
			}
			}
		});


	}

}
