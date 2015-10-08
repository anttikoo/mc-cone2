package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.Timer;


/**
 * The listener interface for receiving GUIcomponent events.
 * The class that is interested in processing a GUIcomponent
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addGUIcomponentListener<code> method. When
 * the GUIcomponent event occurs, that object's appropriate
 * method is invoked.
 *
 * @see GUIcomponentEvent
 */
public class GUIcomponentListener implements ComponentListener{

/** The gui. */
private GUI gui;
private Timer waitPaintingTimer;

/** The child dialog. */
private JDialog childDialog=null;
	
	/**
	 * Instantiates a new GU icomponent listener.
	 *
	 * @param gui the GUI object
	 */
	public GUIcomponentListener(GUI gui){
		this.gui=gui;
		initTimer();
		
		
	}
	
	private void  initTimer(){
		this.waitPaintingTimer = new Timer(100, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				childDialog.setBounds(gui.getVisibleWindowBounds());
				((PropertiesDialog)childDialog).setPanelPosition();		
				
				childDialog.repaint();
				waitPaintingTimer.stop();
				
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
	 */
	@Override
	public void componentHidden(ComponentEvent e) {

	}

	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
	 */
	public void componentMoved(ComponentEvent e) {
		try {
			if(this.childDialog != null && this.childDialog instanceof PropertiesDialog){
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						
						if(!waitPaintingTimer.isRunning()){
							waitPaintingTimer.start();
						}
						
					}
				});
				
			}
			
						
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
	 */
	@Override
	public void componentResized(ComponentEvent e) {
		// resize the lower panel
		gui.visualPanel.setBounds(0,0,(int)(gui.leftPanel.getBounds().getWidth()),(int)(gui.leftPanel.getBounds().getHeight()-gui.downBarPanel.getBounds().getHeight()));
		gui.visualPanel.revalidate();
	
		//	updateImagePanelSize();
		gui.resizeLayerComponents();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
	 */
	@Override
	public void componentShown(ComponentEvent e) {

	}
	
	/**
	 * Gets the child dialog.
	 *
	 * @return the child dialog
	 */
	public JDialog getChildDialog() {
		return childDialog;
	}
	
	/**
	 * Sets the child dialog.
	 *
	 * @param childDialog the new child dialog
	 */
	public void setChildDialog(JDialog childDialog) {
		this.childDialog = childDialog;
	}
	
	

	
	

}