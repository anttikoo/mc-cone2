package gui.saving.ImageSet;

import gui.ProgressBallsDialog;

/**
 * The Class ImageImportCancelerThread. This Class is unused in version 0.1. Probably used in future releases.
 */
public class ImageImportCancelerThread implements Runnable{
private ProgressBallsDialog progressBalls;
private Thread progressThread;
private boolean cancelled=false;


	/**
	 * Instantiates a new ImageImportCancelerThread.
	 *
	 * @param isc the isc
	 * @param pbd the pbd
	 */
	public ImageImportCancelerThread(ImageSetCreator isc, ProgressBallsDialog pbd){
	this.progressBalls= pbd;
	this.progressThread=new Thread(this, "cancelling");
	}
	
	
	/**
	 * Checks if is Thread cancelled.
	 *
	 * @return true, if is cancelled
	 */
	public boolean isCancelled() {
		return cancelled;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		this.setCancelled(false);
		while(progressBalls.isShowON()){
			try {
				this.progressThread.sleep(500);
			} catch (InterruptedException e) {			
				e.printStackTrace();
			}
		}

		setCancelled(true);

	}
	
	/**
	 * Sets the Thread cancelled.
	 *
	 * @param cancelled the new cancelled
	 */
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	/**
	 * Sets Thread running.
	 */
	public void startWaiting(){
		this.progressThread.start();
	}
}
