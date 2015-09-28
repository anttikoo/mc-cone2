package gui;


import gui.panels.ImagePanel;
import information.ID;
import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.MouseInputAdapter;

import managers.TaskManager;

public class GUIListener extends MouseInputAdapter {
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	private boolean is_CTRL_pressed = false;
	private boolean is_SPACE_pressed = false;
	private boolean is_SHIFT_pressed=false;



	private boolean isCellPickingON=false;
	private Point previousDraggingPoint=null;
	private GUI gui;
	private TaskManager taskManager;
	private Timer timerSPACEactivate;
	private Timer timerSPACEinactivate;
	private Timer hightLightTimer;
	private Timer zoomTimer;
	private Container contentPane;
	private PrecountGlassPane glassPane;
	private AbstractButton precountButton;

	private ImagePanel imagePanel;
	private JLayeredPane layers;
	private JPanel downBarPanel;
	private JSlider zoomSlider;
	private JPanel sliderPanel;
	private Toolkit toolkit;

	public GUIListener(GUI gui){
		this.gui=gui;
		this.toolkit=Toolkit.getDefaultToolkit();
		initSPACEactions();
		initHighlightTimer();
		initZoomTimer();
	}

	public void setComponents(GUI gui, TaskManager tm, Container contentPane, PrecountGlassPane glassPane, ImagePanel imagePanel,
			AbstractButton precountButton, JLayeredPane layers, JPanel downBarPanel, JSlider zoomSlider, JPanel sliderPanel){
		this.gui=gui;
		this.taskManager=tm;
		this.contentPane=contentPane;
		this.glassPane=glassPane;
		this.imagePanel=imagePanel;
		this.precountButton=precountButton;
		this.layers=layers;
		this.downBarPanel=downBarPanel;
		this.zoomSlider=zoomSlider;
		this.sliderPanel=sliderPanel;


	}


	private void forwardGlassPaneEvent(MouseEvent e){


		Point glassPanePoint = e.getPoint();
		//Container container = contentPane;
		Point containerPoint = SwingUtilities.convertPoint(glassPane, glassPanePoint, contentPane);

		Point imagePanelPoint = SwingUtilities.convertPoint(contentPane, containerPoint, layers);
		Point downBarPoint = SwingUtilities.convertPoint(contentPane, containerPoint, downBarPanel);
		Point preCountButtonPoint = SwingUtilities.convertPoint(downBarPanel, downBarPoint, precountButton);


		Point zoomSliderPoint = SwingUtilities.convertPoint(sliderPanel, SwingUtilities.convertPoint(downBarPanel, downBarPoint, sliderPanel), zoomSlider);


	//	if(imagePanel.contains(imagePanelPoint))
	//	LOGGER.fine("cPoint: "+containerPoint.x + " "+imagePanelPoint.x+ " y;"+containerPoint.y + " "+imagePanelPoint.y);

		if(containerPoint.y >=0){
			//Component guiComponent = SwingUtilities.getDeepestComponentAt(container, containerPoint.x, containerPoint.y);
			Component guiComponent=null;
				if(imagePanel.contains(imagePanelPoint)){
					guiComponent=imagePanel;
					switch (e.getID()){
					case MouseEvent.MOUSE_PRESSED:
						// start counting
						if(!is_CTRL_pressed && !is_SPACE_pressed){

							if(e.getButton()== MouseEvent.BUTTON1){
								// left mouse pressed -> start counting
								gui.startCellCounting(imagePanelPoint, this.glassPane.getRectangleSize());
							}
						}
						else{
							// zooming is redirected to components under glasspane
							redirectEventToGUIComponents(e, glassPanePoint, guiComponent);
						}
						break;

					case MouseEvent.MOUSE_MOVED:
						paintCircle(imagePanelPoint, glassPanePoint);
						break;

					case MouseEvent.MOUSE_DRAGGED:
						paintCircle(imagePanelPoint, glassPanePoint);
						//redirect to components under glasspane
						redirectEventToGUIComponents(e, glassPanePoint, guiComponent);
						break;

					default:
						//redirect to components under glasspane
						redirectEventToGUIComponents(e, glassPanePoint, guiComponent);
						break;

				}

			}
			/*
			else if(precountButton.contains(preCountButtonPoint)){
				guiComponent=precountButton;
				if(e.getID() == MouseEvent.MOUSE_RELEASED){
				//	LOGGER.fine("adding cells for counting");
					// redirect for button
					//redirectEventToGUIComponents(e, glassPanePoint, guiComponent);
					gui.startStopCellPicking();

				}


			}
			else if(zoomSlider.contains(zoomSliderPoint)){
				guiComponent=zoomSlider;

				// redirect for slider
				redirectEventToGUIComponents(e, glassPanePoint, guiComponent);
			}
				*/
				else if(downBarPanel.contains(downBarPoint)){
					if(precountButton.contains(preCountButtonPoint)){
						guiComponent=precountButton;
						if(e.getID() == MouseEvent.MOUSE_RELEASED){
						//	LOGGER.fine("adding cells for counting");
							// redirect for button
							//redirectEventToGUIComponents(e, glassPanePoint, guiComponent);
							gui.startStopCellPicking();

						}
					}
					else {
						guiComponent=zoomSlider;

						// redirect for slider
						redirectEventToGUIComponents(e, glassPanePoint, guiComponent);
					}
				}
		}
	}

	private void paintCircle(Point imagePanelPoint, Point glassPanePoint){
		Point up = new Point(imagePanelPoint.x, imagePanelPoint.y -glassPane.getRectangleSize()/2);
		Point right = new Point(imagePanelPoint.x+glassPane.getRectangleSize()/2, imagePanelPoint.y);
		Point down = new Point(imagePanelPoint.x, imagePanelPoint.y +glassPane.getRectangleSize()/2);
		Point left = new Point(imagePanelPoint.x-glassPane.getRectangleSize()/2, imagePanelPoint.y);

		if(imagePanel.contains(up) && imagePanel.contains(right) && imagePanel.contains(down) && imagePanel.contains(left)){
			// repaint the circle in new position
			glassPane.setCenterPoint(glassPanePoint);
			glassPane.repaint();
		}
		else{
			// hide circle
			glassPane.setCenterPoint(null);
			glassPane.repaint();
		}
	}

	private void redirectEventToGUIComponents(MouseEvent e, Point glassPanePoint, Component guiComponent){
	//	LOGGER.fine("redirecting event: "+e.getClass().toString());
		Point componentPoint = SwingUtilities.convertPoint(glassPane, glassPanePoint, guiComponent);
		if(e.getID()== MouseEvent.MOUSE_WHEEL){
			guiComponent.dispatchEvent(new MouseWheelEvent(guiComponent, e.getID(), e.getWhen(), e.getModifiers(),
					componentPoint.x, componentPoint.y, e.getClickCount(), e.isPopupTrigger(), ((MouseWheelEvent)e).getScrollType(),
					((MouseWheelEvent)e).getScrollAmount(), (int)(((MouseWheelEvent)e).getPreciseWheelRotation()*10))); //(MouseWheelEvent)e).getWheelRotation()
		}
		else{

			guiComponent.dispatchEvent(new MouseEvent(guiComponent, e.getID(), e.getWhen(), e.getModifiers(),componentPoint.x, componentPoint.y, e.getClickCount(), e.isPopupTrigger()));

		}
	}


	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getSource() instanceof ImagePanel){
			if(is_CTRL_pressed){
				double zoomValue= 0.8;
				if(e.getButton() == MouseEvent.BUTTON1)
					zoomValue=1.25;
				gui.zoomAndUpdateImage(e.getPoint(), zoomValue, ID.IMAGE_PROCESSING_BEST_QUALITY);
			}
		}
		else if(e.getSource() instanceof PrecountGlassPane){
		//	LOGGER.fine("clicked glass");
			forwardGlassPaneEvent(e);
		}


	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getSource() instanceof ImagePanel){
		if(!is_CTRL_pressed){
				// started dragging
			if(previousDraggingPoint==null && timerSPACEactivate.isRunning()){
				previousDraggingPoint = new Point(e.getPoint().x, e.getPoint().y);
			}
			else{

					if(taskManager.isSelectedMarkingLayerVisible()){

						if(is_SHIFT_pressed){
							// setting selected Grids
							gui.setGridSelectedRectangle(e.getPoint());
						}
						else{
							// adding or deleting single marking
							if(e.getButton() == MouseEvent.BUTTON1){ // left button -> add marking
								if(gui.addSingleMarking(e.getPoint()))
									gui.updateCoordinatesOfSelectedMarkingPanel();
							}
							else { // right button -> remove marking

								if(gui.removeSingleMarking(e.getPoint())){
								gui.removeHighLightPoint();

								// update selectedMarkingPanel
								gui.updateCoordinatesOfSelectedMarkingPanel();
								}
							}


						}


					}

			}
		}
		}else if(e.getSource() instanceof PrecountGlassPane){
			forwardGlassPaneEvent(e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getSource() instanceof ImagePanel){
			if(timerSPACEactivate.isRunning()){ //is dragging
				previousDraggingPoint=null; // dragging movement ended -> set starting point as null
			}
		}else if(e.getSource() instanceof PrecountGlassPane){
			forwardGlassPaneEvent(e);
		}

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {

			if(e.getSource() instanceof ImagePanel){
				if(!zoomTimer.isRunning()){
					zoomTimer.start();
				//	System.out.println("scroll: " +e.getPreciseWheelRotation()+ " " +e.getWheelRotation() + " "+ e.getPoint().x+ " amount: " +e.getScrollAmount());
					if(!is_SPACE_pressed){
					//	LOGGER.fine("wheel crolled " +e.getWheelRotation());
						double zoomValue=0.8;
						if(e.getPreciseWheelRotation() <0){ // e.getWheelRotation works only in linux
							zoomValue=1.25;
						}
						gui.zoomAndUpdateImage(e.getPoint(), zoomValue, ID.IMAGE_PROCESSING_BEST_QUALITY);
					}
				}
			}else if(e.getSource() instanceof PrecountGlassPane){
				forwardGlassPaneEvent(e);
			}

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if(e.getSource() instanceof ImagePanel){
			if(is_SPACE_pressed){
				gui.dragLayers(e);
				is_SPACE_pressed=false;
				if(timerSPACEactivate.isRunning())
				timerSPACEactivate.start(); // start counting from beginning
			}
		}else if(e.getSource() instanceof PrecountGlassPane){
			forwardGlassPaneEvent(e);
		}

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if(e.getSource() instanceof PrecountGlassPane){
			forwardGlassPaneEvent(e);

		}
		else if(e.getSource() instanceof ImagePanel){
			if(!hightLightTimer.isRunning()){
				hightLightTimer.start();
			//	System.out.println("moving");
				gui.updateHighlight(e.getPoint());
			}

		}



	}

	public boolean isCellPickingON() {
		return isCellPickingON;
	}

	public void setCellPickingON(boolean isCellPickingON) {
		this.isCellPickingON = isCellPickingON;
	}

	public Point getPreviousDraggingPoint() {
		return previousDraggingPoint;
	}

	public void setPreviousDraggingPoint(Point previousDraggingPoint) {
		this.previousDraggingPoint = previousDraggingPoint;
	}

	public boolean isIs_SPACE_pressed() {
		return is_SPACE_pressed;
	}



	private void initSPACEactions(){
		timerSPACEactivate=new Timer(50,new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				is_SPACE_pressed=true;
				
				
			//	LOGGER.fine("is space pressed true");
			//	timerSPACEactivated.stop();
			//	LOGGER.fine("ended space timer-> not pressed");
			}
		});
		//timerSPACEactivate.setDelay(1000);

		timerSPACEinactivate = new Timer(110,new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			
				timerSPACEactivate.stop();
				is_SPACE_pressed=false;
				timerSPACEinactivate.stop();
				
				
			//	LOGGER.fine("is space pressed false");
				previousDraggingPoint=null; // no more dragging -> initialize the previousdragging point
				gui.setImage(taskManager.getRefreshedImage(ID.IMAGE_PROCESSING_BEST_QUALITY));
				gui.updateCoordinatesOfVisibleMarkingPanels();
		        gui.paintLayers();

			}
		});
	}

	public void addKeyInputMap(JComponent component, int componentID){
		if(componentID == ID.IMAGE_PANEL || componentID== ID.GLASS_PANE){
			InputMap inputMap= (component).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
			inputMap.put(KeyStroke.getKeyStroke("pressed SPACE"), "space_pressed");
			inputMap.put(KeyStroke.getKeyStroke("released SPACE"), "space_released");
			inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT, KeyEvent.SHIFT_DOWN_MASK), "shift_pressed");
			inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT,0,true), "shift_released");
			ActionMap actionMap = 	(component).getActionMap();

			actionMap.put("space_pressed", new AbstractAction() {

				@Override
				public void actionPerformed(ActionEvent e) {
					
					if(!timerSPACEactivate.isRunning()){
						timerSPACEactivate.start();
						
					//	LOGGER.fine("activeted space timer");
					}
					if(timerSPACEinactivate.isRunning())
						timerSPACEinactivate.stop();

				}
			});

				actionMap.put("space_released",new AbstractAction() {

				@Override
				public void actionPerformed(ActionEvent e) {
					
					if(timerSPACEactivate.isRunning() && !timerSPACEinactivate.isRunning()){
						timerSPACEinactivate.start();
						
					//	LOGGER.fine("started inactiveted space timer");
					}


				}
			});


				actionMap.put("shift_pressed", new AbstractAction() {

					@Override
					public void actionPerformed(ActionEvent e) {
					//	System.out.println("ctrl released");
						is_SHIFT_pressed=true;
						LOGGER.fine("shift_pressed");

					}
				});

				actionMap.put("shift_released", new AbstractAction() {

					@Override
					public void actionPerformed(ActionEvent e) {
					//	System.out.println("ctrl released");
						is_SHIFT_pressed=false;
						LOGGER.fine("shift_released");

					}
				});

		}else if(componentID == ID.RIGHT_PANEL){

			InputMap inputMap= (component).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
			// LEFT
			inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0,true), "left_pressed");
			// RIGHT
			inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0,true), "right_pressed");
			// UP
			inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0,true), "up_pressed");

			//DOWN
			inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0,true), "down_pressed");

			ActionMap actionMap = 	(component).getActionMap();


			actionMap.put("left_pressed", new AbstractAction() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// change selected ImageLayer one up
					gui.changeSelectedImageLayerUpOrDown(ID.MOVE_UP);
				}
			});

			actionMap.put("right_pressed", new AbstractAction() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// change selected ImageLayer one down
					gui.changeSelectedImageLayerUpOrDown(ID.MOVE_DOWN);
				}
			});

			actionMap.put("up_pressed", new AbstractAction() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// change selected MarkingLayer one up
					gui.changeSelectedMarkingLayerUpOrDown(ID.MOVE_UP);
				}
			});

			actionMap.put("down_pressed", new AbstractAction() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// change selected MarkingLayer one down

					gui.changeSelectedMarkingLayerUpOrDown(ID.MOVE_DOWN);
				}
			});

		}
		else if(componentID == ID.WHOLE_GUI_FRAME){

			InputMap inputMap= (component).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
			// CTRL
			inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_CONTROL, KeyEvent.CTRL_DOWN_MASK), "ctrl_pressed");
			inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_CONTROL,0,true), "ctrl_released");
			inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK), "save_pressed");

			inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_DOWN_MASK), "export_images_pressed");
			inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyEvent.CTRL_DOWN_MASK), "manage_layers_pressed");
			inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK), "add_layers_pressed");
			inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK), "export_csv_pressed");
			inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.CTRL_DOWN_MASK), "export_tab_pressed");
			inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_DOWN_MASK), "export_clip_pressed");
			inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.SHIFT_DOWN_MASK |KeyEvent.CTRL_DOWN_MASK ), "show_all_markings_pressed");
			inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.SHIFT_DOWN_MASK |KeyEvent.CTRL_DOWN_MASK ), "hide_all_markings_pressed");
			inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.CTRL_DOWN_MASK), "zoom_out_pressed");
			inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.CTRL_DOWN_MASK), "zoom_in_pressed");


			ActionMap actionMap = 	(component).getActionMap();


			actionMap.put("ctrl_pressed", new AbstractAction() {

				@Override
				public void actionPerformed(ActionEvent e) {
				//	System.out.println("ctrl released");
					is_CTRL_pressed=true;

				}
			});

			actionMap.put("ctrl_released", new AbstractAction() {

				@Override
				public void actionPerformed(ActionEvent e) {
				//	System.out.println("ctrl released");
					is_CTRL_pressed=false;

				}
			});

			actionMap.put("save_pressed", new AbstractAction() {

				@Override
				public void actionPerformed(ActionEvent e) {
				//	System.out.println("ctrl pressed");
					gui.saveMarkings();

				}
			});

				actionMap.put("export_csv_pressed", new AbstractAction() {

					@Override
					public void actionPerformed(ActionEvent e) {
					//	System.out.println("ctrl released");
						gui.exportResults(ID.FILE_TYPE_CSV);

					}
				});

				actionMap.put("export_images_pressed", new AbstractAction() {

					@Override
					public void actionPerformed(ActionEvent e) {
					//	System.out.println("ctrl released");
						gui.exportImages();

					}
				});

				actionMap.put("manage_layers_pressed", new AbstractAction() {

					@Override
					public void actionPerformed(ActionEvent e) {
					//	System.out.println("ctrl released");
						gui.manageImageLayersAndMarkings();

					}
				});

				actionMap.put("add_layers_pressed", new AbstractAction() {

					@Override
					public void actionPerformed(ActionEvent e) {
					//	System.out.println("ctrl released");
						gui.openAddImageLayerDialog(null);

					}
				});

				actionMap.put("export_tab_pressed", new AbstractAction() {

					@Override
					public void actionPerformed(ActionEvent e) {
					//	System.out.println("ctrl released");
						gui.exportResults(ID.FILE_TYPE_TEXT_FILE);

					}
				});

				actionMap.put("export_clip_pressed", new AbstractAction() {

					@Override
					public void actionPerformed(ActionEvent e) {
					//	System.out.println("ctrl released");
						gui.exportResults(ID.CLIPBOARD);

					}
				});
				
				actionMap.put("hide_all_markings_pressed", new AbstractAction() {

					@Override
					public void actionPerformed(ActionEvent e) {
					//	System.out.println("ctrl released");
						if(is_SHIFT_pressed)
							gui.setVisibilityOfAllMarkingLayers(false);
						

					}
				});
				
				actionMap.put("show_all_markings_pressed", new AbstractAction() {

					@Override
					public void actionPerformed(ActionEvent e) {
					//	System.out.println("ctrl released");
						gui.setVisibilityOfAllMarkingLayers(true);

					}
				});

				actionMap.put("zoom_out_pressed", new AbstractAction() {

					@Override
					public void actionPerformed(ActionEvent e) {
						gui.zoomAndUpdateImage(new Point((int)(imagePanel.getWidth()/2), (int)(imagePanel.getHeight()/2)), 0.8, ID.IMAGE_PROCESSING_BEST_QUALITY);
					}
				});

				actionMap.put("zoom_in_pressed", new AbstractAction() {

					@Override
					public void actionPerformed(ActionEvent e) {
						gui.zoomAndUpdateImage(new Point((int)(imagePanel.getWidth()/2), (int)(imagePanel.getHeight()/2)), 1.25, ID.IMAGE_PROCESSING_BEST_QUALITY);
					}
				});

		}

	}




	private void initHighlightTimer(){
		this.hightLightTimer=new Timer(100, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(hightLightTimer.isRunning())
					hightLightTimer.stop();

			}
		});
	}

	public void releaseCtrl(){
		is_CTRL_pressed=false;
		LOGGER.fine("release control");
	}



	private void initZoomTimer(){
		this.zoomTimer=new Timer(100, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(zoomTimer.isRunning())
					zoomTimer.stop();

			}
		});
	}


}
