package operators;

import javax.swing.JButton;

public class ButtonRunner implements Runnable{
	JButton button=null;
	
	public ButtonRunner(JButton butt){
		button=butt;
	}
	@Override
	public void run() {
		try{
			
			button.doClick();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
