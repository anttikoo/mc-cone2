package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.MouseInputAdapter;

/**
 * The listener interface for receiving slider events.
 * The class that is interested in processing a slider
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addSliderListener<code> method. When
 * the slider event occurs, that object's appropriate
 * method is invoked. A sliding Timer is started when sliding started. This
 * Timer restricts the updating of the image to every 100 ms.
 *
 */
public class SliderListener extends MouseInputAdapter{
	private boolean slidingON=false;
	private int startValue=0;
//	private JSlider slider;
	JLabel valueLabel;
	private Timer sliderTimer;
	
	/**
	 * Instantiates a new slider listener.
	 *
	 * @param label the label
	 */
	public SliderListener(JLabel label){
	//	this.slider=slider;
		this.valueLabel=label;
		initZoomTimer();
	}

	/**
	 * Gets the start value.
	 *
	 * @return the start value
	 */
	public int getStartValue() {
		return startValue;
	}

	/**
	 * Initializes the zoom timer.
	 */
	private void initZoomTimer(){
		this.sliderTimer=new Timer(100, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(sliderTimer.isRunning())
					sliderTimer.stop();

			}
		});
	}

	/**
	 * Checks if is slider timer running.
	 *
	 * @return true, if is slider timer running
	 */
	public boolean isSliderTimerRunning(){
		return this.sliderTimer.isRunning();
	}

	/**
	 * Checks if is sliding on.
	 *
	 * @return true, if is sliding on
	 */
	public boolean isSlidingON() {
		return slidingON;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		this.setSlidingON(true);
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		this.setSlidingON(false);
		this.setStartValue(0);
		((JSlider)(e.getSource())).setValue(0);
		valueLabel.setText("0");
		this.sliderTimer.stop();
		
	}
	
	/**
	 * Sets the sliding on.
	 *
	 * @param sliderPressed the new sliding on
	 */
	public void setSlidingON(boolean sliderPressed) {
		this.slidingON = sliderPressed;
	}
	
	/**
	 * Sets the start value.
	 *
	 * @param startValue the new start value
	 */
	public void setStartValue(int startValue) {
		this.startValue = startValue;
	}
	
	/**
	 * Start slider timer.
	 */
	public void startSliderTimer(){
		this.sliderTimer.start();
	}
}
