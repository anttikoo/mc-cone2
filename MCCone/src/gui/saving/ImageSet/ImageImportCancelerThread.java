package gui.saving.ImageSet;

import gui.ProgressBallsDialog;

public class ImageImportCancelerThread implements Runnable{
private ImageSetCreator ics;
private ProgressBallsDialog progressBalls;
private Thread progressThread;
private boolean cancelled=false;
	public ImageImportCancelerThread(ImageSetCreator isc, ProgressBallsDialog pbd){
	this.ics=isc;
	this.progressBalls= pbd;
	this.progressThread=new Thread(this, "cancelling");
	}
	@Override
	public void run() {
		this.setCancelled(false);
		// TODO Auto-generated method stub
		while(progressBalls.isShowON()){
			try {
				this.progressThread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		setCancelled(true);

	}
	public boolean isCancelled() {
		return cancelled;
	}
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public void startWaiting(){
		this.progressThread.start();
	}
}
