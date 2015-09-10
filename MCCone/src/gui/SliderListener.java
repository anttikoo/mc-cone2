package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.MouseInputAdapter;
import javax.swing.JPanel;

public class SliderListener extends MouseInputAdapter{
	private boolean slidingON=false;
	private int startValue=0;
//	private JSlider slider;
	JLabel valueLabel;
	private Timer sliderTimer;
	
	public SliderListener(JLabel label){
	//	this.slider=slider;
		this.valueLabel=label;
		initZoomTimer();
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

	public boolean isSlidingON() {
		return slidingON;
	}

	public void setSlidingON(boolean sliderPressed) {
		this.slidingON = sliderPressed;
	}

	public int getStartValue() {
		return startValue;
	}

	public void setStartValue(int startValue) {
		this.startValue = startValue;
	}
	
	private void initZoomTimer(){
		this.sliderTimer=new Timer(100, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(sliderTimer.isRunning())
					sliderTimer.stop();

			}
		});
	}
	
	public boolean isSliderTimerRunning(){
		return this.sliderTimer.isRunning();
	}
	
	public void startSliderTimer(){
		this.sliderTimer.start();
	}
}
