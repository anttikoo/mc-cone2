package gui;


import gui.panels.ImagePanel;
import information.ID;
import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
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

/**
 * Class GUIListener contains all mouse and key listeners for GUI-object. 
 * Keys: CTRL, SHIFT, SPACE and several other keys and combinations.
 * Timers: Space pressed down. highlighting.
 * Mouse Listeners: clicked, dragged, entered, exited, pressed, released, wheel moved.
 * @author Antti Kurronen
 *
 */
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



	/**
	 * A Class constructor. Inits the timers.
	 * @param gui GUI main window which is listened
	 */
	public GUIListener(GUI gui){
		this.gui=gui;
		initSPACEactions();
		initHighlightTimer();
		initZoomTimer();
	}

	/**
	 * Adds shortcut keys to given components.
	 *
	 * @param component the component where shortcut keys are added.
	 * @param componentID the component ID
	 */
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
						gui.setCursorOverLeftPanel(ID.CURSOR_HAND);
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
						gui.setCursorOverLeftPanel(ID.CURSOR_CROSS_HAIR);
					}


				}
			});

				actionMap.put("shift_pressed", new AbstractAction() {

					@Override
					public void actionPerformed(ActionEvent e) {
						is_SHIFT_pressed=true;

					}
				});

				actionMap.put("shift_released", new AbstractAction() {

					@Override
					public void actionPerformed(ActionEvent e) {
						is_SHIFT_pressed=false;

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

		} // whole window listening keys
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
					is_CTRL_pressed=true;

				}
			});

			actionMap.put("ctrl_released", new AbstractAction() {

				@Override
				public void actionPerformed(ActionEvent e) {
					is_CTRL_pressed=false;

				}
			});
			//Save markings
			actionMap.put("save_pressed", new AbstractAction() {

				@Override
				public void actionPerformed(ActionEvent e) {
					gui.saveMarkings();

				}
			});
				// export csv
				actionMap.put("export_csv_pressed", new AbstractAction() {

					@Override
					public void actionPerformed(ActionEvent e) {
						gui.exportResults(ID.FILE_TYPE_CSV);

					}
				});
				// export images
				actionMap.put("export_images_pressed", new AbstractAction() {

					@Override
					public void actionPerformed(ActionEvent e) {
						gui.exportImages();

					}
				});
				// managing layers
				actionMap.put("manage_layers_pressed", new AbstractAction() {

					@Override
					public void actionPerformed(ActionEvent e) {
						gui.manageImageLayersAndMarkings();

					}
				});
				// adding layers
				actionMap.put("add_layers_pressed", new AbstractAction() {

					@Override
					public void actionPerformed(ActionEvent e) {
						gui.openAddImageLayerDialog(null);

					}
				});
				// exportin tab delimited file
				actionMap.put("export_tab_pressed", new AbstractAction() {

					@Override
					public void actionPerformed(ActionEvent e) {
						gui.exportResults(ID.FILE_TYPE_TEXT_FILE);

					}
				});
				// export results to clipboard
				actionMap.put("export_clip_pressed", new AbstractAction() {

					@Override
					public void actionPerformed(ActionEvent e) {
						gui.exportResults(ID.CLIPBOARD);

					}
				});
				// hiding all markings
				actionMap.put("hide_all_markings_pressed", new AbstractAction() {

					@Override
					public void actionPerformed(ActionEvent e) {
						if(is_SHIFT_pressed)
							gui.setVisibilityOfAllMarkingLayers(false);
						

					}
				});
				// showin all markings
				actionMap.put("show_all_markings_pressed", new AbstractAction() {

					@Override
					public void actionPerformed(ActionEvent e) {
						gui.setVisibilityOfAllMarkingLayers(true);

					}
				});
				// zooming out
				actionMap.put("zoom_out_pressed", new AbstractAction() {

					@Override
					public void actionPerformed(ActionEvent e) {
						gui.zoomAndUpdateImage(new Point((int)(imagePanel.getWidth()/2), (int)(imagePanel.getHeight()/2)), 0.8, ID.IMAGE_PROCESSING_BEST_QUALITY);
					}
				});
				// zooming in
				actionMap.put("zoom_in_pressed", new AbstractAction() {

					@Override
					public void actionPerformed(ActionEvent e) {
						gui.zoomAndUpdateImage(new Point((int)(imagePanel.getWidth()/2), (int)(imagePanel.getHeight()/2)), 1.25, ID.IMAGE_PROCESSING_BEST_QUALITY);
					}
				});

		}

	}


	/**
	 * MouseEvents made on GlassPane are redirected to lower layer components if necessary. 
	 * @param e MouseEvents the mouse event which is forwarded.
	 */
	private void forwardGlassPaneEvent(MouseEvent e){

		Point glassPanePoint = e.getPoint();
		Point containerPoint = SwingUtilities.convertPoint(glassPane, glassPanePoint, contentPane);
		Point imagePanelPoint = SwingUtilities.convertPoint(contentPane, containerPoint, layers);
		Point downBarPoint = SwingUtilities.convertPoint(contentPane, containerPoint, downBarPanel);
		Point preCountButtonPoint = SwingUtilities.convertPoint(downBarPanel, downBarPoint, precountButton);

		if(containerPoint.y >=0){
			Component guiComponent=null;
				// check does imagePanel containg the point were mouse was pressed
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
				// check does downbarPanel containg the point were mouse was pressed
				else if(downBarPanel.contains(downBarPoint)){
					if(precountButton.contains(preCountButtonPoint)){
						guiComponent=precountButton;
						if(e.getID() == MouseEvent.MOUSE_RELEASED){			
							// redirect for button					
							gui.startStopCellPicking();
						}
					}
					else { // third option is zoomSlider
						guiComponent=zoomSlider;

						// redirect for slider
						redirectEventToGUIComponents(e, glassPanePoint, guiComponent);
					}
				}
		}
	}

	/**
	 * Gets the previous dragging point.
	 *
	 * @return the previous dragging point
	 */
	
	public Point getPreviousDraggingPoint() {
		return previousDraggingPoint;
	}

	/**
	 * Inits the highlight timer.
	 */
	private void initHighlightTimer(){
		this.hightLightTimer=new Timer(100, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(hightLightTimer.isRunning())
					hightLightTimer.stop();
			}
		});
	}


	/**
	 * Initializes the space action timers for activating and inactivating dragging.
	 */
	private void initSPACEactions(){
		timerSPACEactivate=new Timer(50,new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				is_SPACE_pressed=true;
				

			}
		});

		timerSPACEinactivate = new Timer(110,new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// stop activate timer
				timerSPACEactivate.stop();
				is_SPACE_pressed=false;
				timerSPACEinactivate.stop();
				
				previousDraggingPoint=null; // no more dragging -> initialize the previousdragging point
				// update visible image and markings
				gui.setImage(taskManager.getRefreshedImage(ID.IMAGE_PROCESSING_BEST_QUALITY));
				gui.updateCoordinatesOfVisibleMarkingPanels();
				
		        gui.paintLayers();

			}
		});
	}

	/**
	 * Initializes the timer for zooming.
	 */
	private void initZoomTimer(){
		this.zoomTimer=new Timer(100, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(zoomTimer.isRunning())
					zoomTimer.stop();

			}
		});
	}

	/**
	 * Returns true if precounting Thread is running. Otherwise false.
	 * @return boolean true if Precounting Thread is running.
	 */
	public boolean isCellPickingON() {
		return isCellPickingON;
	}

	/**
	 * Checks if is is space pressed.
	 *
	 * @return true, if space is pressed
	 */
	public boolean isIs_SPACE_pressed() {
		return is_SPACE_pressed;
	}

	/** 
	 * When mouse clicked over ImagePanel and ctrl is pressed down the zooming is launched. Left-button -> zoom in; right-button -> zoom out.
	 * If mouse was pressed on glasspane (Precounting running) -> the event is forwarded to method forwardGlassPaneEvent.
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getSource() instanceof ImagePanel){
			if(is_CTRL_pressed){
				double zoomValue= 0.8;
				//Button1 -> zoom in
				if(e.getButton() == MouseEvent.BUTTON1)
					zoomValue=1.25;
				gui.zoomAndUpdateImage(e.getPoint(), zoomValue, ID.IMAGE_PROCESSING_BEST_QUALITY);
			}
		}
		else if(e.getSource() instanceof PrecountGlassPane){
			forwardGlassPaneEvent(e);
		}


	}

	/** 
	 * Mediates the mouseWheelMoved Event to wanted procedure. Events of PrecountGlassPane are forwarded to forwardGlassPaneEvent(..).
	 * @see java.awt.event.MouseAdapter#mouseDragged(java.awt.event.MouseEvent)
	 */
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
	public void mouseEntered(MouseEvent e) {
		// do nothing
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// do nothing
	}

	/**
	 *  Mediates the mouseWheelMoved Event to wanted procedure. Events of PrecountGlassPane are forwarded to forwardGlassPaneEvent(..).
	 * @see java.awt.event.MouseAdapter#mouseMoved(java.awt.event.MouseEvent)
	 */
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

	/**
	 * Mediates the MousePressed Event to wanted procedure. Events of PrecountGlassPane are forwarded to forwardGlassPaneEvent(..).
	 * Computation of events of ImagePanel depends on which keys are pressed down or which threads are running at same time:
	 * CTRL-down -> do nothing
	 * SPACE-down -> dragging -> set init dragging point.
	 * SHIFT-down -> select/unselect grid cell.
	 * MOUSE-LEFT-button -> add marking
	 * MOUSE-RIGHT-button -> remove marking and it's highlight point.
	 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
	 */
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

	/**
	 * Mediates the MouseReleased Event to wanted procedure. Events of PrecountGlassPane are forwarded to forwardGlassPaneEvent(..).
	 * In ImagePanel made releasing sets previous dragging point as null. -> No More dragging.
	 * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
	 */
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

	/**
	 * Mediates the mouseWheelMoved Event to wanted procedure. Events of PrecountGlassPane are forwarded to forwardGlassPaneEvent(..).
	 * Single wheel movement zooms 25% in or out depending wheel movement direction.
	 * @see java.awt.event.MouseAdapter#mouseWheelMoved(java.awt.event.MouseWheelEvent)
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {

			if(e.getSource() instanceof ImagePanel){
					// if previous zooming has stopped can start new zooming
					if(!zoomTimer.isRunning()){
						zoomTimer.start();									
							double zoomValue=0.8;
							if(e.getPreciseWheelRotation() <0){ // e.getWheelRotation works only in linux
								zoomValue=1.25;
							}
							gui.zoomAndUpdateImage(e.getPoint(), zoomValue, ID.IMAGE_PROCESSING_BEST_QUALITY);
			
				}
			}else if(e.getSource() instanceof PrecountGlassPane){
				forwardGlassPaneEvent(e);
			}

	}

	/**
	 * Paints the circle and square for precounting if fits fully to imagePanel.
	 * @param imagePanelPoint Point at imagePanel where mouse focused
	 * @param glassPanePoint Point at glassPane where mouse focused
	 */
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



	/**
	 * Dispatches the triggered event to given component. Determines is the event mouseWheel event or some other.
	 * @param e MouseEvent the event triggered at glassPane.
	 * @param glassPanePoint Point the point where mouse triggered event.
	 * @param guiComponent Component the component of GUI where event is wanted to work.
	 */
	private void redirectEventToGUIComponents(MouseEvent e, Point glassPanePoint, Component guiComponent){
	
		Point componentPoint = SwingUtilities.convertPoint(glassPane, glassPanePoint, guiComponent);
		if(e.getID()== MouseEvent.MOUSE_WHEEL){
			guiComponent.dispatchEvent(new MouseWheelEvent(guiComponent, e.getID(), e.getWhen(), e.getModifiers(),
					componentPoint.x, componentPoint.y, e.getClickCount(), e.isPopupTrigger(), ((MouseWheelEvent)e).getScrollType(),
					((MouseWheelEvent)e).getScrollAmount(), (int)(((MouseWheelEvent)e).getPreciseWheelRotation()*10))); 
		}
		else{

			guiComponent.dispatchEvent(new MouseEvent(guiComponent, e.getID(), e.getWhen(), e.getModifiers(),componentPoint.x, componentPoint.y, e.getClickCount(), e.isPopupTrigger()));

		}
	}

	/**
	 * Sets the variable is_CTRL_down to false;
	 */
	public void releaseCtrl(){
		is_CTRL_pressed=false;
	}




	/**
	 *  Sets is the precounting Thread running.
	 * @param isCellPickingON boolean running or not running
	 */
	public void setCellPickingON(boolean isCellPickingON) {
		this.isCellPickingON = isCellPickingON;
	}

	/**
	 * Sets the components. Components are used to check in which part of gui the mouse was pressed.
	 *
	 * @param gui the gui
	 * @param tm the TaskManager
	 * @param contentPane the content pane at gui
	 * @param glassPane the glass pane of gui
	 * @param imagePanel the visible image panel at gui
	 * @param precountButton the precount button at gui
	 * @param layers JLayeredPane containing Layers at gui
	 * @param downBarPanel the down bar panel at gui
	 * @param zoomSlider the zoom slider at gui
	 * @param sliderPanel the slider panel at gui
	 */
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
		


	}

	/**
	 * Sets the previous dragging point.
	 *
	 * @param previousDraggingPoint the new previous dragging point
	 */
	public void setPreviousDraggingPoint(Point previousDraggingPoint) {
		this.previousDraggingPoint = previousDraggingPoint;
	}


}
