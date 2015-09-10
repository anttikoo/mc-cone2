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
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import managers.PreCountThreadManager;


public class ProgressBallsDialog extends ShadyMessageDialog implements Runnable {

	//	private ArrayList<JPanel> ballPanels;
	private int paintedBall=-1;
	private int threadNumber=1;
	//private float opacity=0.0f;
	private volatile boolean showON=true;
	private boolean nextBallBigger=true;

	private Graphics2D g2d;
	private JPanel ballsPanel;

	private PreCountThreadManager pctm;
	private Thread painterThread;
//	private PreCounterThread preCounterThread;
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");

	public ProgressBallsDialog(JFrame frame, String title, String message,int id, Component comp){
		super(frame, title, message, id, comp);

		try {
			initBalls();
			initButtonAction();
			this.painterThread = new Thread(this, title+threadNumber++);

			this.validate();
		//	this.setVisible(true);
		//	this.setShowON(true);
		//	this.repaint();
		//	this.painterThread.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
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

	//	okButton.setForeground(Color_schema.color_orange_dark);
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


	private void initButtonAction(){
		try {
			if(super.getFirstButton() != null){
			super.getFirstButton().removeActionListener(super.getFirstButton().getActionListeners()[0]);
			super.getFirstButton().addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					stopPaintingAndClose();

				}
			});
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
/*
	public void initPreCounterThread(PreCounterThread pct){
		this.preCounterThread=pct;
	}
	*/
	private void initBalls(){
		try {
		/*	JPanel messageBallsPanel = new JPanel();
			messageBallsPanel.setLayout(new BoxLayout(messageBallsPanel, BoxLayout.PAGE_AXIS));
			messageBallsPanel.setBackground(super.getMessagePanel().getBackground());
*/
			ballsPanel = new JPanel();
			ballsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 0));
			ballsPanel.setBorder(null);
			ballsPanel.setBackground(super.getMessagePanel().getBackground());
			for(int i =0;i<6;i++){
				ballsPanel.add(new SingleBall());
			}
			super.getMessagePanel().setMaximumSize(new Dimension(200,100));
//	messageBallsPanel.add(new JLabel("part 1/2"));
		//	messageBallsPanel.add(ballsPanel);
			super.getMessagePanel().removeAll();
			super.getMessagePanel().setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));


			super.getMessagePanel().add(ballsPanel);
			super.getMessagePanel().revalidate();
//	super.getDialogBackPanel().add(ballsPanel);
			super.revalidate();
			this.revalidate();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}



	public int showDialog(){
		try {

			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					setVisible(true);
				}
			});
			setShowON(true);
		//	this.setVisible(true);
			if(this.painterThread != null && this.painterThread.isAlive())
				this.painterThread.interrupt();
			this.painterThread.start();


			return ID.CANCEL;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}
/*
	public void startSHowing(){
		//setShowON(true);
	//	this.setVisible(true);
		LOGGER.fine("startShowing: "+showON);
		this.painterThread.start();

	}
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
			// TODO Auto-generated catch block
			LOGGER.severe("Error in stopping progress windonw");
			e.printStackTrace();
			setShowON(false);
		}

	}
/*
	public void cancelCountingProcess(){
		try {

				LOGGER.fine("cancelCountingProcess");
				setShowON(false);


				if(this.pctm != null)
					pctm.cancelCounting();

				this.setVisible(false);
				this.dispose();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.severe("Error in stopping progress windonw");
			e.printStackTrace();
			setShowON(false);
		}

	}
*/
	public void setManager(PreCountThreadManager pctm){
		this.pctm=pctm;
	}







	@SuppressWarnings("static-access")
	private void showNextBall(){
		try {
		//	LOGGER.fine("nextballbigger: "+nextBallBigger + " paintedball:"+paintedBall);
			if(nextBallBigger){
				paintedBall++;
				if(paintedBall > 5){
					paintedBall=4;
					nextBallBigger=false;
				}
			}
			else{
				paintedBall--;
				if(paintedBall < 0){
					paintedBall=1;
					nextBallBigger=true;
				}
			}

			SingleBall ballComponent = (SingleBall)ballsPanel.getComponent(paintedBall);
		/*
			while(showON && ballComponent.getOpacity() < 0.95f){
				float opa = ballComponent.getOpacity()+0.1f;
				if(opa >1.0f)
					opa=1.0f;
				ballComponent.paintWithOpacity(opa);
				painterThread.sleep(50);

			}
			while(showON && ballComponent.getOpacity() > 0.05f){
				float opa = ballComponent.getOpacity()-0.1f;
				if(opa <0.01)
					opa=0.0f;
				ballComponent.paintWithOpacity(opa);

					painterThread.sleep(50);

			}
			*/

			while(showON && ballComponent.getaRGBopacity() < 250){
				int opa= ballComponent.getaRGBopacity()+25;
				if(opa >255)
					opa=255;
				ballComponent.paintWithOpacity(opa);
				painterThread.sleep(80);
				//Thread.sleep(80);

			}
			while(showON && ballComponent.getaRGBopacity() > 30){
				int opa= ballComponent.getaRGBopacity()-25;
				if(opa <30)
					opa=30;
				ballComponent.paintWithOpacity(opa);
				painterThread.sleep(80);
			//	Thread.sleep(80);

			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		catch (Exception e) {
			LOGGER.severe("Error in painting progress balls");
		}

	}


	public boolean isShowON() {
		return showON;
	}

	public void setShowON(boolean showON) {
		this.showON = showON;
	}



	@SuppressWarnings("static-access")
	@Override
	public void run() {
		try {


//	changePaintedBalls(); // starts painting the balls
			while(showON){
			//	LOGGER.fine("changing ball");
				showNextBall();
				painterThread.sleep(100);
			//	Thread.currentThread().sleep(100);

			}
		//	this.setVisible(false);
		//	this.dispose();
			LOGGER.fine("ProgressBall Thread ended");
		painterThread.interrupt();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			showON=false;
		}
	}



	private class SingleBall extends JPanel{
		//private boolean showON=true;
		private float opacity =0.0f;
		private int aRGBopacity=30;



		private SingleBall(){
			this.setOpaque(false);

			this.setBorder(null);
			this.setMaximumSize(new Dimension(20,20));
			this.setPreferredSize(new Dimension(20,20));
			this.setMinimumSize(new Dimension(20,20));
			this.setBackground(Color_schema.dark_30);
			this.setLayout(null);
		}

		private void paintWithOpacity(float newOpacity){
			LOGGER.fine("paint with opacity "+newOpacity);
			this.opacity=newOpacity;
			this.repaint();
		}

		private void paintWithOpacity(int newARGB){

			this.aRGBopacity=newARGB;
			this.repaint();
		}



		private float getOpacity(){
			return this.opacity;
		}

		public int getaRGBopacity() {
			return aRGBopacity;
		}

		public void setaRGBopacity(int aRGBopacity) {
			this.aRGBopacity = aRGBopacity;
		}



		@Override
		public void paintComponent(Graphics g) {

			// TODO Auto-generated method stub
			super.paintComponents(g);
			g2d = (Graphics2D) g.create();
		//	RenderingHints rh= new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		//	g2d.setRenderingHints(rh);

			g2d.setPaint(new Color(aRGBopacity,aRGBopacity,aRGBopacity));


		//	g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.opacity));
			g2d.fillOval(2, 2, 16, 16);
			g2d.dispose();


		}
	}



}
