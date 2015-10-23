package gui;

import gui.graphics.SmallCloseIcon;
import information.Fonts;
import information.ID;
import information.SharedVariables;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import operators.GetResources;


public class PropertiesDialog extends JDialog {
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	protected GUI gui;
	protected Point topLeftPoint;
//	private int maxHeight;


	protected JPanel backPanel;
	protected int panelWidth=400;
	protected int rightPanelWidth=this.panelWidth; // in MarkingProperties this will be 700
	protected int panelHeight=500;
	protected Rectangle recOfBackpanel;


	public PropertiesDialog(JFrame frame, GUI gui, Point point){
		super(frame,true);
		this.gui = gui;
		this.topLeftPoint=point;
	//	this.revalidate();
	//	this.repaint();
	}
	
	

	/**
	 *  Setups the components of Dialog window
	 */
	protected void initDialog(){
		try {
			//setup dimming of window
			this.setResizable(false);
			this.setBounds(this.gui.getVisibleWindowBounds()); // sets the size of this dialog same as the GUI (the parent)
			this.setUndecorated(true); // no titlebar or buttons
			this.setBackground(new Color(0,0,0,0)); // transparent color
			ContentPane cone = new ContentPane(new GridBagLayout());
			this.setContentPane(cone); // makes dimming over GUI	
			backPanel = new JPanel();
			backPanel.setLayout(new BorderLayout());
			backPanel.setBorder(BorderFactory.createLineBorder(Color_schema.button_light_border, 3));
			backPanel.setMaximumSize(new Dimension(panelWidth,panelHeight));
			backPanel.setMinimumSize(new Dimension(panelWidth,panelHeight));
			backPanel.setPreferredSize(new Dimension(panelWidth,panelHeight));

			// set sizes and locations of components
		//	setPanelPosition();
			
			backPanel.add(initUPPanels(), BorderLayout.PAGE_START);
			backPanel.add(initCenterPanels(), BorderLayout.CENTER);
			backPanel.add(initDownPanel(),BorderLayout.PAGE_END);
		
			this.add(backPanel);
			LOGGER.fine("called propertiesDialog");
			

		} catch (Exception e) {
			LOGGER.severe("Error in initializing PropertiesDialog: " +e.getClass().toString() + " :" +e.getMessage() +" line: " +e.getStackTrace()[2].getLineNumber());
			e.printStackTrace();
		}
	}

	protected void setPanelPosition(){
		
		recOfBackpanel = getGoodBoundsForPanel();
		if(recOfBackpanel != null){
			backPanel.setBounds(recOfBackpanel);		
		}
		else{
			recOfBackpanel = new Rectangle((int)(this.topLeftPoint.getX()-(panelWidth)), (int)this.topLeftPoint.getY(),this.panelWidth,this.panelHeight);
			backPanel.setBounds(recOfBackpanel);
			//backPanel.setBounds((int)(this.topLeftPoint.getX()-(panelWidth)), (int)this.topLeftPoint.getY(),this.panelWidth,this.panelHeight);
			
		}
		this.setBounds(this.gui.getVisibleWindowBounds());	
	}
		
	/**
	 * Initializes the layers panel.
	 */
	protected void initLayersPanel(){
		// do nothing here -> done in extended class
	}

	/**
	 * Initializes the uppermost panels.
	 *
	 * @return the created JPanel
	 * @throws Exception the exception
	 */
	protected JPanel initUPPanels() throws Exception{
		return null;
	}

	/**
	 * Initializes  the center panels.
	 *
	 * @return the created JPanel
	 */
	protected JPanel initCenterPanels(){
		return null;
	}

	/**
	 * Creates lowest JPanel of dialog
	 * @return JPanel lowest JPanel of dialog
	 * @throws Exception
	 */
	protected JPanel initDownPanel() throws Exception{
		// Setup buttons:
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.setBackground(Color_schema.dark_30);
		buttonPanel.setMinimumSize(new Dimension(50,40));

		// Button for
		JButton okButton = new JButton("SAVE");
		okButton.setPreferredSize(new Dimension(120,30));
		okButton.setBackground(Color_schema.dark_20);

		okButton.setFocusable(false);
		MouseListenerCreator.addMouseListenerToNormalButtons(okButton);  // add listener when button pressed -> change visualization of button
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				hideDialog(true); // close dialog and save changes
			}
		});

		JButton cancelButton = new JButton("CANCEL");
		cancelButton.setPreferredSize(new Dimension(120,30));
		cancelButton.setBackground(Color_schema.dark_20);
		cancelButton.setForeground(Color_schema.orange_dark);
		cancelButton.setBorder(BorderFactory.createLineBorder(Color_schema.orange_dark, 2));
		cancelButton.setFocusable(false);
		MouseListenerCreator.addMouseListenerToCancelButtons(cancelButton); // add listener when button pressed -> change visualization of button
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			hideDialog(false);	// close dialog without saving
			}
		});

		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(okButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(20,0)));
		buttonPanel.add(cancelButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(10,0)));

		return buttonPanel;
	}

	/**
	 * Initializes the title panel, showing title of the dialog.
	 *
	 * @param title the title String
	 * @return the created JPanel
	 */
	protected JPanel initTitlePanel(String title){
		try {
			//contains title JLabel
			JPanel backTitlePanel = new JPanel();
			backTitlePanel.setLayout(new BoxLayout(backTitlePanel,BoxLayout.LINE_AXIS));
			backTitlePanel.setMaximumSize(new Dimension(rightPanelWidth, 30));
			backTitlePanel.setMinimumSize(new Dimension(rightPanelWidth, 30));
			backTitlePanel.setPreferredSize(new Dimension(rightPanelWidth, 30));
			backTitlePanel.setBackground(Color_schema.dark_25);

			JPanel titleJPanel = new JPanel();
			titleJPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			titleJPanel.setMaximumSize(new Dimension(rightPanelWidth - 40, 30));
			titleJPanel.setMinimumSize(new Dimension(rightPanelWidth - 40, 30));
			titleJPanel.setPreferredSize(new Dimension(rightPanelWidth - 40, 30));
			titleJPanel.setBackground(Color_schema.dark_30);
			JLabel titleLabel = new JLabel(title);
			titleLabel.setFont(Fonts.b18);
			titleJPanel.add(titleLabel);

			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new GridBagLayout());
			buttonPanel.setMaximumSize(new Dimension(30, 30));
			buttonPanel.setMinimumSize(new Dimension(30, 30));
			buttonPanel.setPreferredSize(new Dimension(rightPanelWidth, 30));
			buttonPanel.setBackground(Color_schema.dark_30);
			JButton closeJButton = new JButton(new SmallCloseIcon(false));
			closeJButton.setAlignmentY(Component.CENTER_ALIGNMENT);
			closeJButton.setPreferredSize(new Dimension(16, 16));
			closeJButton.setMargin(new Insets(0, 0, 0, 0));
			closeJButton.setContentAreaFilled(false);
			closeJButton.setFocusable(false);
			closeJButton.setBorder(BorderFactory.createLineBorder(Color_schema.button_grey_border, 1));
			closeJButton.setToolTipText("close window");
			closeJButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					hideDialog(false);

				}
			});

			MouseListenerCreator.addMouseListenerToSmallCloseButtons(closeJButton);
			buttonPanel.add(closeJButton);

			backTitlePanel.add(titleJPanel);
			backTitlePanel.add(Box.createHorizontalGlue());
			backTitlePanel.add(buttonPanel);
			return backTitlePanel;
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.severe("Error in initializing title panel of Dialog!");
			return null;
		}
	}
	
	/**
	 * Calculates the location and size values for Dialog window to fit in screen
	 * @return Rectangle containing appropriate location and size values for the Dialog window.
	 */
	protected Rectangle getGoodBoundsForPanel(){
		try {			
			// get the Bounds of GUI-object
			Rectangle guiRectangle=gui.getVisibleWindowBounds();
					
			int x = (int)((guiRectangle.width-panelWidth)/2); // set to middle horizontally
			int y=  (int)((guiRectangle.height-panelHeight)/2); // set to middle vertically
			return new Rectangle(x,y,panelWidth,panelHeight);
			

		} catch (Exception e) {
			LOGGER.severe("Error in counting Bounds for MarkingProperties: " +e.getClass().toString() + " :" +e.getMessage() +"line: " +e.getStackTrace()[2].getLineNumber());
			return null;
		}
	}

	/**
	 * Hides Dialog window and saves the changes made to MarkingLayer.
	 * @param saveChanges boolean value should the changes be saved to MarkingLayer
	 */
	protected void hideDialog(boolean saveChanges){
		// implemented in extended class
	}

	/**
	 * Sets the Dialog visible.
	 */
	public void showDialog(){
		setVisible(true);	
	}
}

