package gui;

import information.ID;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import managers.PreCountThreadManager;


/**
 * The Class ProgressBallsDialog. Shows balls moving and shows that something is running.
 */
public class ProgressBallsDialog extends ShadyMessageDialog implements Runnable {
	private int paintedBall=-1;
	private int threadNumber=1;
	private volatile boolean showON=true;
	private boolean nextBallBigger=true;
	private Graphics2D g2d;
	private JPanel ballsPanel;
	private PreCountThreadManager pctm;
	private Thread painterThread;
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");

	public ProgressBallsDialog(JFrame frame, String title, String message,int id, Component comp){
		super(frame, title, message, id, comp);

		try {
			initBalls();
			initButtonAction();
			this.painterThread = new Thread(this, title+threadNumber++);
			this.validate();
		} catch (Exception e) {
			LOGGER.severe("Error starting Progress dialog.");
			e.printStackTrace();
		}

	}

	/* Overrides super class createButton -method. In ProgressBallsDialog only one button is created -> setup it here.
	 * @see gui.ShadyMessageDialog#createButton(int)
	 */
	protected JButton createButton(final int buttonID) throws Exception{
		JButton button=new JButton(getButtonText(buttonID));
		button.setPreferredSize(new Dimension(150,30));
		button.setBackground(Color_schema.dark_20);

		if(buttonID == ID.NO || buttonID == ID.CANCEL){
			button.setForeground(Color_schema.orange_dark);
			button.setBorder(BorderFactory.createLineBorder(Color_schema.orange_dark, 2));
			MouseListenerCreator.addMouseListenerToCancelButtons(button);
		}
		else{
			MouseListenerCreator.addMouseListenerToNormalButtons(button);
		}

		button.setFocusable(false);

		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopPaintingAndClose();
			}
		});
		return button;
	}

	public void refreshDialog(){
		setShowON(true);
		nextBallBigger=true;
		initBalls();
		this.painterThread = new Thread(this, "painter"+threadNumber);
	}


	private void initButtonAction() throws Exception{
			if(super.getFirstButton() != null){
				super.getFirstButton().removeActionListener(super.getFirstButton().getActionListeners()[0]);
				super.getFirstButton().addActionListener(new ActionListener() {
	
					@Override
					public void actionPerformed(ActionEvent e) {
						stopPaintingAndClose();
	
					}
				});
			}	
	}
	
	/**
	 * Initializes the components of window.
	 */
	private void initBalls(){
		try {

			ballsPanel = new JPanel();
			ballsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 0));
			ballsPanel.setBorder(null);
			ballsPanel.setBackground(super.getMessagePanel().getBackground());
			for(int i =0;i<6;i++){
				ballsPanel.add(new SingleBall());
			}
			// set messagepanel at super class
			super.getMessagePanel().setMaximumSize(new Dimension(200,100));
			super.getMessagePanel().removeAll();
			super.getMessagePanel().setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
			super.getMessagePanel().add(ballsPanel);
			super.getMessagePanel().revalidate();
			super.revalidate();
			this.revalidate();
		} catch (Exception e) {
			
			e.printStackTrace();
		}


	}


	/* (non-Javadoc)
	 * @see gui.ShadyMessageDialog#showDialog()
	 */
	public int showDialog(){
		try {

			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {			
					setVisible(true);
				}
			});
			setShowON(true);
		
			if(this.painterThread != null && this.painterThread.isAlive())
				this.painterThread.interrupt();
			this.painterThread.start();

			return ID.CANCEL;
		} catch (Exception e) {
			LOGGER.severe("Error in showing Progress dialog!");
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * Stops the painting and closes window.
	 */
	public void stopPaintingAndClose(){
		try {

				LOGGER.fine("StopPaintingAndClose");
				setShowON(false);

				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						if(pctm != null)
							pctm.cancelCounting();

					}
				});


				this.setVisible(false);
				this.dispose();

		} catch (Exception e) {
			LOGGER.severe("Error in stopping progress dialog");
			e.printStackTrace();
			setShowON(false);
		}

	}

	/**
	 * Sets the PreCountThreadManager.
	 *
	 * @param pctm the new manager
	 */
	public void setManager(PreCountThreadManager pctm){
		this.pctm=pctm;
	}



	/**
	 * Draws the next ball to window.
	 */
	@SuppressWarnings("static-access")
	private void showNextBall(){
		try {
			// check if next ball can be more right (bigger) -> add paintedBall value.
			if(nextBallBigger){
				paintedBall++;
				if(paintedBall > 5){ // ball is rightmost -> start going to left
					paintedBall=4;
					nextBallBigger=false;
				}
			}
			else{
				paintedBall--;
				if(paintedBall < 0){ // ball is leftmost -> start going to right
					paintedBall=1;
					nextBallBigger=true;
				}
			}

			SingleBall ballComponent = (SingleBall)ballsPanel.getComponent(paintedBall);
		
			// paint the balls
			while(showON && ballComponent.getaRGBopacity() < 250){
				int opa= ballComponent.getaRGBopacity()+25;
				if(opa >255)
					opa=255;
				ballComponent.paintWithOpacity(opa);
				painterThread.sleep(80);
				

			}
			while(showON && ballComponent.getaRGBopacity() > 30){
				int opa= ballComponent.getaRGBopacity()-25;
				if(opa <30)
					opa=30;
				ballComponent.paintWithOpacity(opa);
				painterThread.sleep(80);
			

			}
		} catch (InterruptedException e) {
			
			e.printStackTrace();

		}
		catch (Exception e) {
			LOGGER.severe("Error in painting progress dialog balls");
		}

	}


	/**
	 * Checks if is show on.
	 *
	 * @return true, if is show on
	 */
	public boolean isShowON() {
		return showON;
	}

	/**
	 * Sets the show on.
	 *
	 * @param showON the new show on
	 */
	public void setShowON(boolean showON) {
		this.showON = showON;
	}



	
	@SuppressWarnings("static-access")
	@Override
	public void run() {
		try {

		// starts painting the balls
		while(showON){
			showNextBall();
			painterThread.sleep(100);	

		}
		
		LOGGER.fine("ProgressBall Thread ended");
		painterThread.interrupt();
		
		} catch (InterruptedException e) {		
			e.printStackTrace();
			showON=false;
		}
	}



	/**
	 * The Class SingleBall.
	 */
	private class SingleBall extends JPanel{
		private int aRGBopacity=30;


		/**
		 * Instantiates a new single ball.
		 */
		private SingleBall(){
			this.setOpaque(false);
			this.setBorder(null);
			this.setMaximumSize(new Dimension(20,20));
			this.setPreferredSize(new Dimension(20,20));
			this.setMinimumSize(new Dimension(20,20));
			this.setBackground(Color_schema.dark_30);
			this.setLayout(null);
		}

		

		/**
		 * Sets the new integer for creating new argb color.
		 *
		 * @param newARGB the new argb
		 */
		private void paintWithOpacity(int newARGB){

			this.aRGBopacity=newARGB;
			this.repaint();
		}		

		/**
		 * Gets the a rgb opacity.
		 *
		 * @return the a rgb opacity
		 */
		public int getaRGBopacity() {
			return aRGBopacity;
		}


		@Override
		public void paintComponent(Graphics g) {		
			super.paintComponents(g);
			g2d = (Graphics2D) g.create();
			g2d.setPaint(new Color(aRGBopacity,aRGBopacity,aRGBopacity));
			g2d.fillOval(2, 2, 16, 16);
			g2d.dispose();
		}
	}

}
