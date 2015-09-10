package gui;

import gui.graphics.BigCloseIcon;
import gui.graphics.MediumCloseIcon;
import gui.graphics.SmallCloseIcon;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import operators.GetResources;

/**
 * Helper class for adding MouseListeners to JButtons. The listeners only affects to visual style of buttons: border, color, icon, etc.
 * @author Antti Kurronen
 *
 */
public class MouseListenerCreator {


	/**
	 * Adds MouseListener to JButton, which has normal type, for example OK, YES, etc.
	 * @param button JButton, where the Listener is added.
	 * @throws Exception
	 */
	public static void addMouseListenerToNormalButtons(JButton button) throws Exception{

		button.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				if(((JButton)e.getSource()).isEnabled())
				((JButton)e.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.button_orange_border, 2));
			}
			@Override
			public void mouseExited(MouseEvent e) {
				((JButton)e.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.button_grey_border, 2));
			}
			@Override
			public void mousePressed(MouseEvent e) {
				((JButton)e.getSource()).setForeground(Color_schema.orange_dark);
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				((JButton)e.getSource()).setForeground(Color_schema.white_230);
			}
		});
	}



public static void addMouseListenerToCancelButtons(JButton button) throws Exception{

		button.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				if(((JButton)e.getSource()).isEnabled())
				((JButton)e.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.button_orange_border, 2));
			}
			@Override
			public void mouseExited(MouseEvent e) {
				((JButton)e.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.button_grey_border, 2));
			}
			@Override
			public void mousePressed(MouseEvent e) {
				((JButton)e.getSource()).setForeground(Color_schema.red);
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				((JButton)e.getSource()).setForeground(Color_schema.orange_dark);
			}
		});
	}

public static void addMouseListenerToNormalButtonsWithBlackBorder(JButton button) throws Exception{

	button.addMouseListener(new MouseListener() {

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
		}
		@Override
		public void mouseEntered(MouseEvent e) {
			if(((JButton)e.getSource()).isEnabled())
			((JButton)e.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.button_orange_border, 2));
		}
		@Override
		public void mouseExited(MouseEvent e) {
			((JButton)e.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.dark_30, 2));
		}
		@Override
		public void mousePressed(MouseEvent e) {
			((JButton)e.getSource()).setForeground(Color_schema.orange_dark);
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			((JButton)e.getSource()).setForeground(Color_schema.white_230);
		}
	});
}

public static void addMouseListenerToButtonsWithDark40Border(JButton button) throws Exception{

	button.addMouseListener(new MouseListener() {

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
		}
		@Override
		public void mouseEntered(MouseEvent e) {
			if(((JButton)e.getSource()).isEnabled())
				((JButton)e.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.white_230, 2));
		}
		@Override
		public void mouseExited(MouseEvent e) {
			((JButton)e.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.dark_40, 2));
		}
		@Override
		public void mousePressed(MouseEvent e) {
			((JButton)e.getSource()).setForeground(Color_schema.orange_dark);
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			((JButton)e.getSource()).setForeground(Color_schema.white_230);
		}
	});
}

public static void addMouseListenerToButtonsWithMarkingWith70Border(JButton button) throws Exception{

	button.addMouseListener(new MouseListener() {

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
		}
		@Override
		public void mouseEntered(MouseEvent e) {
			if(((JButton)e.getSource()).isEnabled())
				((JButton)e.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.white_230, 1));
		}
		@Override
		public void mouseExited(MouseEvent e) {
			((JButton)e.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.dark_70, 1));
		}
		@Override
		public void mousePressed(MouseEvent e) {
			((JButton)e.getSource()).setForeground(Color_schema.orange_dark);
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			((JButton)e.getSource()).setForeground(Color_schema.white_230);
		}
	});
}


public static void addMouseListenerToSmallCloseButtons(JButton button) throws Exception{
	button.addMouseListener(new MouseListener() {

		@Override
		public void mouseReleased(MouseEvent arg0) {
			((JButton)arg0.getSource()).setIcon(new SmallCloseIcon(false));

		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			((JButton)arg0.getSource()).setIcon(new SmallCloseIcon(true));

		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.button_grey_border, 1));

		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.button_orange_border, 2));

		}

		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}
	});
}

public static void addMouseListenerToBigCloseButtons(JButton button) throws Exception{
	button.addMouseListener(new MouseListener() {

		@Override
		public void mouseReleased(MouseEvent arg0) {
			((JButton)arg0.getSource()).setIcon(new BigCloseIcon(false));

		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			((JButton)arg0.getSource()).setIcon(new BigCloseIcon(true));

		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.button_grey_border, 1));

		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.button_orange_border, 2));

		}

		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}
	});
}

public static void addMouseListenerToMediumCloseButtons(JButton button) throws Exception{
	button.addMouseListener(new MouseListener() {

		@Override
		public void mouseReleased(MouseEvent arg0) {
			((JButton)arg0.getSource()).setIcon(new MediumCloseIcon(false));

		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			((JButton)arg0.getSource()).setIcon(new MediumCloseIcon(true));

		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.button_grey_border, 1));

		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.button_orange_border, 1));

		}

		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}
	});
}
/*
public static ImageIcon getImageIcon(String path) {

	try {
		URL url = MouseListenerCreator.class.getResource(path);
		ImageIcon img = new ImageIcon(url);
		return img;
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return null;
	}
}

*/
}
