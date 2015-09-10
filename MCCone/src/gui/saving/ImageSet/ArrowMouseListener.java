package gui.saving.ImageSet;

import gui.Color_schema;
import information.SharedVariables;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.Timer;

public class ArrowMouseListener implements MouseListener {
	public final String widthUp = "WIDTH_UP";
	public final String widthDown = "WIDTH_DOWN";
	public final String heighthUp = "HEIGHT_UP";
	public final String heightDown = "HEIGHT_DOWN";
	private JTextField widthField;
	private JTextField heightField;
	private Timer pressingWidthUPTimer;
	private Timer pressingWidthDownTimer;
	private Timer pressingHeightUPTimer;
	private Timer pressingHeightDownTimer;
	private Timer multiplierTimer;
	private boolean changingValuesON=false;
	private int valueChange=0;
	private int maxValueChange=30;
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	private int maximumImageSize=SharedVariables.IMAGESET_EXPORT_MAX_RESOLUTION;
	public static double scalingFactor=0;



	public ArrowMouseListener(JTextField wf, JTextField hf){
		this.widthField=wf;
		this.heightField=hf;
		initPressingTimers();

	}

	public void setScalingFactor(double factor){
		ArrowMouseListener.scalingFactor=factor;
	}

	private void initPressingTimers(){
		this.pressingWidthUPTimer=new Timer(50, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!changingValuesON)
				updateField(valueChange, widthField, heightField, false);
			}
		});
		this.pressingWidthDownTimer=new Timer(50, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!changingValuesON)
				updateField(-valueChange, widthField, heightField, false);
			}
		});
		this.pressingHeightUPTimer=new Timer(50, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!changingValuesON)
				updateField(valueChange, heightField, widthField, true);
			}
		});
		this.pressingHeightDownTimer=new Timer(50, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!changingValuesON)
				updateField(-valueChange, heightField, widthField, true);
			}
		});

		this.multiplierTimer=new Timer(200,new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(valueChange<maxValueChange)
					valueChange++;

			}
		});


	}
	@Override
	public void mouseClicked(MouseEvent e) {
		stopTimers();
		if(((JButton)e.getSource()).getActionCommand().equals(widthUp)){
			if(!changingValuesON)
			updateField(1, this.widthField, this.heightField, false);
		}
		else if(((JButton)e.getSource()).getActionCommand().equals(widthDown)){
			if(!changingValuesON)
			updateField(-1, this.widthField, this.heightField, false);
		}
		else if(((JButton)e.getSource()).getActionCommand().equals(heighthUp)){
			if(!changingValuesON)
			updateField(1, heightField, widthField, true);
		}
		else if(((JButton)e.getSource()).getActionCommand().equals(heightDown)){
			if(!changingValuesON)
			updateField(-1, heightField, widthField, true);
		}

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(!this.multiplierTimer.isRunning())
			this.multiplierTimer.start();

		if(((JButton)e.getSource()).getActionCommand().equals(widthUp)){
			if(!this.pressingWidthUPTimer.isRunning())
				this.pressingWidthUPTimer.start();
		}
		else if(((JButton)e.getSource()).getActionCommand().equals(widthDown)){
			if(!this.pressingWidthDownTimer.isRunning())
				this.pressingWidthDownTimer.start();
		}
		else if(((JButton)e.getSource()).getActionCommand().equals(heighthUp)){
			if(!this.pressingHeightUPTimer.isRunning())
				this.pressingHeightUPTimer.start();
		}
		else if(((JButton)e.getSource()).getActionCommand().equals(heightDown)){
			if(!this.pressingHeightDownTimer.isRunning())
				this.pressingHeightDownTimer.start();
		}


	}

	@Override
	public void mouseReleased(MouseEvent e) {
		stopTimers();

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void updateResolutions(){
		updateField(0,this.widthField, this.heightField, false);
	}

	private void updateField(int resolutionchange, JTextField field_changed, JTextField field_calculated, boolean divide){
		changingValuesON=true;
		if(field_changed.getText() != null && field_changed.getText().length()>0){

			try {
				int presentValue=Integer.parseInt(field_changed.getText().trim());
				presentValue+=resolutionchange;
				if(presentValue<=maximumImageSize && presentValue > 0){

					if(scalingFactor !=0){
						int secondValue=0;
						if(divide){
							secondValue= (int)(((double)presentValue)/scalingFactor);
						}
						else{

							secondValue= (int)(((double)presentValue)*scalingFactor);
						}
						if(secondValue <=maximumImageSize && secondValue > 0){
							field_changed.setText(""+presentValue);
							field_changed.setForeground(Color_schema.white_230);

							field_calculated.setText(""+secondValue);
							field_calculated.setForeground(Color_schema.white_230);
						}
						else{
							field_calculated.setForeground(Color_schema.orange_dark);

						}



					}
					else{

						field_changed.setText(""+presentValue);
						field_changed.setForeground(Color_schema.white_230);
					}

				}
				else{
					if(presentValue >maximumImageSize){
						presentValue=maximumImageSize;
					int secondValue =(int)(((double)presentValue)/scalingFactor);
					if(secondValue>maximumImageSize){
						if(divide){
							presentValue= (int)(((double)secondValue)*scalingFactor);
						}
						else{
							presentValue= (int)(((double)secondValue)/scalingFactor);

						}
						field_calculated.setForeground(Color_schema.orange_dark);
					}
					else{
						field_calculated.setForeground(Color_schema.white_230);
					}
					field_changed.setText(""+presentValue);
					field_changed.setForeground(Color_schema.white_230);

					field_calculated.setText(""+secondValue);

					field_changed.setForeground(Color_schema.orange_dark);
					}
				}

				changingValuesON=false;
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				LOGGER.warning("Resolution has to be numerical value");
				changingValuesON=false;
			}


		}

	}



	private void stopTimers(){
		if(this.multiplierTimer.isRunning()){
			this.multiplierTimer.stop();
			this.valueChange=0;
		}


		if(this.pressingWidthUPTimer.isRunning())
			this.pressingWidthUPTimer.stop();

		if(this.pressingWidthDownTimer.isRunning())
			this.pressingWidthDownTimer.stop();

		if(this.pressingHeightUPTimer.isRunning())
			this.pressingHeightUPTimer.stop();

		if(this.pressingHeightDownTimer.isRunning())
			this.pressingHeightDownTimer.stop();
	}

}
