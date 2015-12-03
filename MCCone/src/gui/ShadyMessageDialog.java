package gui;

import information.Fonts;
import information.ID;
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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;



public class ShadyMessageDialog extends JDialog{
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	private String message;
	private String title;
	private JLabel titleLabel;
	private int typeOfButtons;
	private int returnValue=-1;
	private Component parentComponent;
	private JPanel dialogBackPanel;
	private JPanel messagePanel;
	private JPanel buttonPanel;
	

	public ShadyMessageDialog(JFrame frame, String title, String message, int typeOfButtons, Component comp){
		super(frame,true);
		super.setLocationRelativeTo(comp);
		
	
		this.setLocationRelativeTo(comp);
		this.parentComponent = comp;
		this.title=title;
		this.message=message;
		this.typeOfButtons=typeOfButtons;	
		initDialog();
		
	}
	
	public ShadyMessageDialog(JDialog dialog, String title, String message, int typeOfButtons, Component comp){
		super(dialog,true);
	
		super.setLocationRelativeTo(comp);
		this.setLocationRelativeTo(comp);
		this.parentComponent = comp;
		this.title=title;
		this.message=message;
		this.typeOfButtons=typeOfButtons;	
		initDialog();
		
	}

	private void initDialog(){

		try {
			this.setResizable(false);		
			this.setBounds(WindowLocator.getVisibleWindowBounds(parentComponent));
			this.setUndecorated(true); // no titlebar or buttons
			this.setBackground(new Color(0,0,0,0)); // transparent color
			this.setContentPane(new ContentPane());
			this.getContentPane().setBackground(Color_schema.dark_30);
			this.getContentPane().setLayout(new GridBagLayout());

			dialogBackPanel = new JPanel();
			dialogBackPanel.setBackground(Color_schema.dark_30);
			dialogBackPanel.setLayout(new BorderLayout());	
			dialogBackPanel.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color_schema.white_230));
			dialogBackPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
			dialogBackPanel.setAlignmentY(JComponent.CENTER_ALIGNMENT);
			if(dialogBackPanel.getPreferredSize().getWidth()<500)
				dialogBackPanel.setPreferredSize(new Dimension((int)(this.getBounds().getWidth()*0.95), (int)(this.getBounds().getHeight()*0.95)));

			JPanel titlePanel = new JPanel();
			titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			titlePanel.setBackground(Color_schema.dark_30);
			titleLabel = new JLabel(this.title);
			Font fontConsolas20 = new Font("Consolas", Font.BOLD,20);
			titleLabel.setFont(fontConsolas20);

			titleLabel.setForeground(Color_schema.white_230);
			titlePanel.add(Box.createRigidArea(new Dimension(20,0)));
			titlePanel.add(titleLabel);

			messagePanel = new JPanel();
			messagePanel.setLayout(new FlowLayout(FlowLayout.CENTER,10,10));
			messagePanel.setBackground(Color_schema.dark_30);
			JLabel messageLabel = new JLabel(this.message);
			Font fontConsolas18 = new Font("Consolas", Font.PLAIN,18);
			int maxwidth =messageLabel.getFontMetrics(fontConsolas18).stringWidth(messageLabel.getText());
			int titleWidth = titleLabel.getFontMetrics(fontConsolas20).stringWidth(titleLabel.getText());
			if(titleWidth > maxwidth)
				maxwidth = titleWidth;
			
			messageLabel.setFont(fontConsolas18);
			messageLabel.setForeground(Color_schema.white_230);
			messagePanel.add(messageLabel);

			JScrollPane messageScrollPane = new JScrollPane(messagePanel);
			messageScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			messageScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);


			buttonPanel = new JPanel();
			buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
			buttonPanel.setBackground(Color_schema.dark_30);
			buttonPanel.add(Box.createHorizontalGlue());

			// add buttons by the type of message box
			switch (typeOfButtons) {
				case ID.OK:
					buttonPanel.add(addKeyListenerToButton(createButton(ID.OK)));
					buttonPanel.add(Box.createRigidArea(new Dimension(20,0)));
					break;
				case ID.YES_NO:
					buttonPanel.add(addKeyListenerToButton(createButton(ID.YES)));
					buttonPanel.add(Box.createRigidArea(new Dimension(20,0)));
					buttonPanel.add(createButton(ID.NO));
					buttonPanel.add(Box.createRigidArea(new Dimension(20,0)));
					break;					
				case ID.YES_NO_CANCEL:
					buttonPanel.add(addKeyListenerToButton(createButton(ID.YES)));
					buttonPanel.add(Box.createRigidArea(new Dimension(20,0)));
					buttonPanel.add(createButton(ID.NO));
					buttonPanel.add(Box.createRigidArea(new Dimension(20,0)));
					buttonPanel.add(createButton(ID.CANCEL));
					buttonPanel.add(Box.createRigidArea(new Dimension(20,0)));
					break;
				case ID.APPEND_OVERWRITE_CANCEL:
					buttonPanel.add(createButton(ID.APPEND));
					buttonPanel.add(Box.createRigidArea(new Dimension(20,0)));
					buttonPanel.add(createButton(ID.OVERWRITE));
					buttonPanel.add(Box.createRigidArea(new Dimension(20,0)));
					buttonPanel.add(createButton(ID.CANCEL));
					buttonPanel.add(Box.createRigidArea(new Dimension(20,0)));
					break;
				case ID.CANCEL:
					buttonPanel.add(createButton(ID.CANCEL));
					buttonPanel.add(Box.createRigidArea(new Dimension(20,0)));
					break;

				case ID.NO_BUTTONS:

					// do nothing
					break;
				}
			maxwidth=Math.max(maxwidth,getDownButtonPanelWidth());

			dialogBackPanel.setPreferredSize(new Dimension(maxwidth+100,120));
			titlePanel.setMinimumSize(new Dimension(maxwidth+100,50));
			titlePanel.setMaximumSize(new Dimension((int)dialogBackPanel.getMaximumSize().getWidth(),50));
			messagePanel.setMaximumSize(new Dimension((int)dialogBackPanel.getMaximumSize().getWidth(),50));
			buttonPanel.setMaximumSize(new Dimension((int)dialogBackPanel.getMaximumSize().getWidth(),40));
			buttonPanel.setMinimumSize(new Dimension(maxwidth,40));
			buttonPanel.setPreferredSize(new Dimension(maxwidth,40));
			messageScrollPane.setPreferredSize(new Dimension((int)dialogBackPanel.getPreferredSize().getWidth(),50));

			dialogBackPanel.add(titlePanel, BorderLayout.PAGE_START);
			dialogBackPanel.add(messageScrollPane, BorderLayout.CENTER);
			dialogBackPanel.add(buttonPanel, BorderLayout.PAGE_END);
			this.add(dialogBackPanel);
			this.validate();
			this.repaint();

		} catch (Exception e) {
			LOGGER.severe("Error in initializing Dialog: " +e.getClass().toString() + " :" +e.getMessage());
		}
	}

	/**
	 * Calculates and returns the width of downButton Panel. Withd depends on how many buttons there are.
	 *
	 * @return the down button panel width
	 */
	private int getDownButtonPanelWidth(){
		int width=0;
		Component[] bComp=this.buttonPanel.getComponents();
		for (int i = 0; i < bComp.length; i++) {
			if(bComp[i] instanceof JButton){
			JButton bb=(JButton)bComp[i];
			width+=bb.getMaximumSize().width;
			}
			
		}
		return width;
	}
	
	/**
	 * Creates the button by given ButtonID.
	 *
	 * @param buttonID the button id
	 * @return the j button
	 * @throws Exception the exception
	 */
	protected JButton createButton(final int buttonID) throws Exception{
		JButton button=new JButton(getButtonText(buttonID));
		int maxStringWidth = button.getFontMetrics(Fonts.b15).stringWidth(getButtonText(buttonID));
		button.setPreferredSize(new Dimension(maxStringWidth+20,30));
		button.setMinimumSize(new Dimension(maxStringWidth+20,30));
		button.setMaximumSize(new Dimension(maxStringWidth+20,30));
		button.setBackground(Color_schema.dark_20);
		button.setFont(Fonts.b15);

		if(buttonID == ID.NO || buttonID == ID.CANCEL){
			button.setForeground(Color_schema.orange_dark);
			button.setBorder(BorderFactory.createLineBorder(Color_schema.orange_dark, 2));
			addMouseListenerToCancelButtons(button);
		}
		else{
			addMouseListenerToNormalButtons(button);
		}

		button.setFocusable(false);

		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				returnValue=buttonID;
				hideDialog();
			}
		});


		return button;
	}
	
	/**
	 * Sets the panel position with given Rectangle.
	 *
	 * @param bounds the new panel position
	 */
	public void setPanelPosition(Rectangle bounds){
		if(bounds != null)
			this.setBounds(bounds);
	
	}

	/**
	 * Gets the Text of JButton by given ID.
	 *
	 * @param ID the id
	 * @return the button text
	 */
	protected String getButtonText(int id){
		switch (id) {

		case ID.CANCEL:
			return "CANCEL";

		case ID.YES:
			return "YES";

		case ID.NO:
			return "NO";

		case ID.OVERWRITE:
			return "OVERWRITE";

		case ID.APPEND:
			return "APPEND";

		default:
			return "OK";
		}
	}


	/**
	 * Adds the key listener to a JButton.
	 *
	 * @param button the button
	 * @return the j button
	 */
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

	/**
	 * Shows dialog.
	 *
	 * @return the int
	 */
	public int showDialog(){
		setVisible(true);
		return returnValue;
	}

	/**
	 * Hides dialog.
	 */
	private void hideDialog(){
		
		this.setVisible(false);
		this.dispose();
	}

	/**
	 * Gets the backPanel of dialog
	 *
	 * @return the dialog back panel
	 */
	public JPanel getDialogBackPanel(){
		return this.dialogBackPanel;
	}
	
	/**
	 * Gets the first button.
	 *
	 * @return the first button
	 */
	
	
	public JButton getFirstButton(){
		Component[] buttons=this.buttonPanel.getComponents();
		if(buttons.length>0 && buttons[0] instanceof JButton)
			return (JButton)buttons[0];
		else
			return null;
	}

	/**
	 * Gets the JPanel messagePanel.
	 *
	 * @return JPanel MessagePanel
	 */
	public JPanel getMessagePanel(){
		return this.messagePanel;
	}
	

	/**
	 * Adds the mouse listener to normal buttons.
	 *
	 * @param button the button
	 * @throws Exception the exception
	 */
	private void addMouseListenerToNormalButtons(JButton button) throws Exception{

		button.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
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

/**
 * Adds the mouse listener to cancel buttons.
 *
 * @param button the button
 * @throws Exception the exception
 */
private void addMouseListenerToCancelButtons(JButton button) throws Exception{

		button.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				if(((JButton)e.getSource()).isEnabled())
				((JButton)e.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.button_orange_border, 2));
			}
			@Override
			public void mouseExited(MouseEvent e) {
				((JButton)e.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.orange_dark, 2));
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
	}



}
