package managers;

import java.awt.image.BufferedImage;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import gui.ProgressBallsDialog;
import operators.PreCounterThread;

/**
 * The Class PreCountThreadManager. Manages the PreCounterThread and Thread of ProgressBallsDialog. When other is stopped another is also.
 */
public class PreCountThreadManager {

	private PreCounterThread preCounter;
	
	/** The progress balls dialog. */
	private ProgressBallsDialog progressBallsDialog;
	
	/** The ID of MarkingLayer. */
	private int mLayerID=-1;
	
	/** The ID of ImageLayer. */
	private int iLayerID=-1;
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	
	/**
	 * Instantiates a new PreCountThreadManager.
	 *
	 * @param preCounter the PreCounterThread
	 * @param progressBalls the ProgressBallsDialog
	 * @param iLayerID the ID of ImageLayer
	 * @param mLayerID the ID of MarkingLayer
	 */
	public PreCountThreadManager(PreCounterThread preCounter, ProgressBallsDialog progressBalls , int iLayerID, int mLayerID){
		this.setiLayerID(iLayerID);
		this.setmLayerID(mLayerID);
		this.progressBallsDialog=progressBalls;
		this.preCounter= preCounter;
		this.preCounter.setManager(this);
		this.progressBallsDialog.setManager(this);
	}

	/**
	 * Initializes the PreCounterThread.
	 */
	public void initPreCounterThread(){
		this.preCounter.initThread();
	}

	/**
	 * Start the counting in PreCounterThread.
	 */
	public void startCounting(){
		LOGGER.fine(this.preCounter.getThreadStatus());
		this.preCounter.startCounting();
	}

	/**
	 * Cancels Threads of PreCounterThread and ProgressBallsDialog if running.
	 */
	public void cancelCounting(){
		LOGGER.fine("Manager: cancel counting! ");
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
			
				if(preCounter.isCounting())
					preCounter.cancelOutside();
			}
		});
	SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if(progressBallsDialog.isShowON())
					progressBallsDialog.stopPaintingAndClose();
			}
		});
	}

	/**
	 * Sets the subImage to PreCounterThread. 
	 *
	 * @param subImage the sub image
	 * @return true, if successful
	 */
	public boolean setNewSubImage(BufferedImage subImage){
		if(this.preCounter != null){
			this.preCounter.setSubImage(subImage);
			return true;
		}
		return false;
	}

	/**
	 * Sets the ProgressBallsDialog.
	 *
	 * @param pbd the new ProgressBallsDialog
	 */
	public void setProgressBallDialog(ProgressBallsDialog pbd){
		this.progressBallsDialog=pbd;
	}

	/**
	 * Returns the ID of MarkingLayer.
	 *
	 * @return the ID of MarkingLayer
	 */
	public int getmLayerID() {
		return mLayerID;
	}

	/**
	 * Sets the ID of MarkingLayer.
	 *
	 * @param mLayerID the new ID of MarkingLayer
	 */
	public void setmLayerID(int mLayerID) {
		this.mLayerID = mLayerID;
	}

	/**
	 * Returns the ID of ImageLayer.
	 *
	 * @return the ID of ImageLayer
	 */
	public int getiLayerID() {
		return iLayerID;
	}

	/**
	 * Sets the ID of ImageLayer.
	 *
	 * @param iLayerID the new ID of ImageLayer
	 */
	public void setiLayerID(int iLayerID) {
		this.iLayerID = iLayerID;
	}

}
