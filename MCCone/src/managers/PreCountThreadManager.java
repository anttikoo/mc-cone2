package managers;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import information.ID;
import gui.GUI;
import gui.ProgressBallsDialog;
import operators.PreCounterThread;

public class PreCountThreadManager {

	private PreCounterThread preCounter;
	private ProgressBallsDialog progressBallsDialog;
	private int mLayerID=-1;
	private int iLayerID=-1;

//	private Thread managerThread;
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
//	private Thread ballThread;
//	private Thread countThread;
	private GUI gui;


	public PreCountThreadManager(PreCounterThread preCounter, ProgressBallsDialog progressBalls , int iLayerID, int mLayerID){
		this.setiLayerID(iLayerID);
		this.setmLayerID(mLayerID);
		this.progressBallsDialog=progressBalls;
		this.preCounter= preCounter;
		this.preCounter.setManager(this);
		this.progressBallsDialog.setManager(this);

	}

	public void initPreCounterThread(){
		this.preCounter.initThread();
	}

	public void startCounting(){
		LOGGER.fine(this.preCounter.getThreadStatus());
		this.preCounter.startCounting();


	}
/*
	public void stopCounting(){
		if(preCounter.isCounting())
			preCounter.cancelOutside(); // cancelInside
		if(progressBallsDialog.isShowON())
			this.progressBallsDialog.stopPaintingAndClose();
	}
*/

	public void cancelCounting(){
		LOGGER.fine("Manager: cancel counting! ");
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(preCounter.isCounting())
					preCounter.cancelOutside();
			}
		});
	SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(progressBallsDialog.isShowON())
					progressBallsDialog.stopPaintingAndClose();
			}
		});


	}


	public boolean setNewSubImage(BufferedImage subImage){
		if(this.preCounter != null){
			this.preCounter.setSubImage(subImage);
			return true;
		}
		return false;
	}

	public void setProgressBallDialog(ProgressBallsDialog pbd){
		this.progressBallsDialog=pbd;
	}

	public int getmLayerID() {
		return mLayerID;
	}

	public void setmLayerID(int mLayerID) {
		this.mLayerID = mLayerID;
	}

	public int getiLayerID() {
		return iLayerID;
	}

	public void setiLayerID(int iLayerID) {
		this.iLayerID = iLayerID;
	}





}
