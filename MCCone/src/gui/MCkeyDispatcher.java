package gui;

import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;

/**
 * Keyboard listener for CONTROL-Key. Mediates the releasing of CONTROL-key to GuiListener-object.
 *
 */
public class MCkeyDispatcher implements KeyEventDispatcher{
GUIListener gListener;

	/**
	 * Constructor of class.
	 * @param gListener GuiListener @see gui.GuiListener
	 */
	public MCkeyDispatcher(GUIListener gListener){
		this.gListener=gListener;
	}
	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		if(e.getID() == KeyEvent.KEY_RELEASED){
			if(e.getKeyCode()== KeyEvent.VK_CONTROL){
				gListener.releaseCtrl();

			}
		}

		return false;
	}

}
