package gui;

import gui.graphics.BigCloseIcon;
import gui.graphics.MediumCloseIcon;
import gui.graphics.SmallCloseIcon;
import information.ID;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 * Helper class for adding MouseListeners and KeyListeners to JButtons. The listeners only affects to visual style of buttons: border, color, icon, etc.
 * @author Antti Kurronen
 *
 */
public class MouseListenerCreator {

	
	/**
	 * Adds the key listener to JButton. For accepting buttons (OK, SELECT etc.) the ENTER key is set to fire the button and for canceling buttons backspace fires the button.
	 *
	 * @param button the JButton
	 * @param typeOfButton the type of button
	 * @throws Exception the exception
	 */
	public static void addKeyListenerToButton(final JButton button, int typeOfButton) throws Exception{

		if(typeOfButton== ID.BUTTON_ENTER){
			InputMap inputMap= (button).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		//	inputMap.put(KeyStroke.getKeyStroke("pressed ENTER"), "enter_pressed");
			inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0, false), "enter_pressed");
			ActionMap actionMap = 	(button).getActionMap();
			actionMap.put("enter_pressed", new AbstractAction() {
	
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {					
						SwingUtilities.invokeLater(new Runnable() {
							
							@Override
							public void run() {	
								try{
									button.doClick();
								}
								catch(Exception e){
									e.printStackTrace();
								}
								
							}
						});
				}
	
			});
		}
		if(typeOfButton== ID.BUTTON_CANCEL){
			InputMap inputMap= (button).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
			inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE,0,false), "cancel_pressed");
			ActionMap actionMap = 	(button).getActionMap();
			actionMap.put("cancel_pressed", new AbstractAction() {
	
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					
						SwingUtilities.invokeLater(new Runnable() {
						
						@Override
						public void run() {
							try{
								button.doClick();
							}
							catch(Exception e){
								e.printStackTrace();
							}
							
						}
					});
	
				}
	
			});
		}
		
	}

	/**
	 * Adds MouseListener to JButton, which has normal type, for example OK, YES, etc.
	 * @param button JButton, where the Listener is added.
	 * @throws Exception
	 */
	public static void addMouseListenerToNormalButtons(JButton button) throws Exception{

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
	 * @param button the JButton
	 * @throws Exception the exception
	 */
	public static void addMouseListenerToCancelButtons(JButton button) throws Exception{
	
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
					((JButton)e.getSource()).setForeground(Color_schema.red);
				}
				@Override
				public void mouseReleased(MouseEvent e) {
					((JButton)e.getSource()).setForeground(Color_schema.orange_dark);
				}
			});
		}
	
	/**
	 * Adds the mouse listener to normal buttons with black border.
	 *
	 * @param button the button
	 * @throws Exception the exception
	 */
	public static void addMouseListenerToNormalButtonsWithBlackBorder(JButton button) throws Exception{
	
		button.addMouseListener(new MouseListener() {
	
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				if(((JButton)e.getSource()).isEnabled())
				((JButton)e.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.button_orange_border, 2));
			}
			@Override
			public void mouseExited(MouseEvent e) {
				((JButton)e.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.dark_30, 2));
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
	 * Adds the mouse listener to buttons with dark40 border.
	 *
	 * @param button the button
	 * @throws Exception the exception
	 */
	public static void addMouseListenerToButtonsWithDark40Border(JButton button) throws Exception{
	
		button.addMouseListener(new MouseListener() {
	
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				if(((JButton)e.getSource()).isEnabled())
					((JButton)e.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.white_230, 2));
			}
			@Override
			public void mouseExited(MouseEvent e) {
				((JButton)e.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.dark_40, 2));
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
	 * Adds the mouse listener to buttons with marking with70 border.
	 *
	 * @param button the button
	 * @throws Exception the exception
	 */
	public static void addMouseListenerToButtonsWithMarkingWith70Border(JButton button) throws Exception{
	
		button.addMouseListener(new MouseListener() {
	
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				if(((JButton)e.getSource()).isEnabled())
					((JButton)e.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.white_230, 1));
			}
			@Override
			public void mouseExited(MouseEvent e) {
				((JButton)e.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.dark_70, 1));
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
	 * Adds the mouse listener to small close buttons.
	 *
	 * @param button the button
	 * @throws Exception the exception
	 */
	public static void addMouseListenerToSmallCloseButtons(JButton button) throws Exception{
		button.addMouseListener(new MouseListener() {
	
			@Override
			public void mouseReleased(MouseEvent arg0) {
				((JButton)arg0.getSource()).setIcon(new SmallCloseIcon(false));
	
			}
	
			@Override
			public void mousePressed(MouseEvent arg0) {
				((JButton)arg0.getSource()).setIcon(new SmallCloseIcon(true));
	
			}
	
			@Override
			public void mouseExited(MouseEvent arg0) {
				((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.button_grey_border, 1));
	
			}
	
			@Override
			public void mouseEntered(MouseEvent arg0) {
				((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.button_orange_border, 2));
	
			}
	
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
	
			}
		});
	}
	
	/**
	 * Adds the mouse listener to big close buttons.
	 *
	 * @param button the button
	 * @throws Exception the exception
	 */
	public static void addMouseListenerToBigCloseButtons(JButton button) throws Exception{
		button.addMouseListener(new MouseListener() {
	
			@Override
			public void mouseReleased(MouseEvent arg0) {
				((JButton)arg0.getSource()).setIcon(new BigCloseIcon(false));
	
			}
	
			@Override
			public void mousePressed(MouseEvent arg0) {
				((JButton)arg0.getSource()).setIcon(new BigCloseIcon(true));
	
			}
	
			@Override
			public void mouseExited(MouseEvent arg0) {
				((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.button_grey_border, 1));
	
			}
	
			@Override
			public void mouseEntered(MouseEvent arg0) {
				((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.button_orange_border, 2));
	
			}
	
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
	
			}
		});
	}
	
	/**
	 * Adds the mouse listener to medium close buttons.
	 *
	 * @param button the button
	 * @throws Exception the exception
	 */
	public static void addMouseListenerToMediumCloseButtons(JButton button) throws Exception{
		button.addMouseListener(new MouseListener() {
	
			@Override
			public void mouseReleased(MouseEvent arg0) {
				((JButton)arg0.getSource()).setIcon(new MediumCloseIcon(false));
	
			}
	
			@Override
			public void mousePressed(MouseEvent arg0) {
				((JButton)arg0.getSource()).setIcon(new MediumCloseIcon(true));
	
			}
	
			@Override
			public void mouseExited(MouseEvent arg0) {
				((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.button_grey_border, 1));
	
			}
	
			@Override
			public void mouseEntered(MouseEvent arg0) {
				((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.button_orange_border, 1));
	
			}
	
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
	
			}
		});
	}

}
