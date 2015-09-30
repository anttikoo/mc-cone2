package gui;

import information.Fonts;
import information.ID;
import information.MarkingLayer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import operators.ShapeDrawer;

public class MarkingProperties extends PropertiesDialog {
	protected final static Logger LOGGER = Logger.getLogger("MCCLogger");
	protected GUI gui;
	private Point topLeftPoint;
	private MarkingLayer markingLayer;
	//private boolean mouseEntered=false;
	private JColorChooser colorChooser = null;
	//private MarkingShape[] shapePanels;
	private ShapeIcon shapeIcons[];
//	private int maxHeight;

	private JComboBox shapeBox;
	//private ShapeComboboxRenderer renderer;
	private ComboBoxIconRenderer icon_renderer;

	private JPanel boxAndSlidersPanel;
	protected JPanel comboBoxPanel;
//	private Font fontConsolas16;
//	private Font fontConsolas14;
//	private Font fontConsolasBold14;
	private int[] shapeIDs;

	protected Color selectedColor;
	protected int selectedSize;
	protected int selectedThickness;
	protected int selectedOpacity;
	protected int selectedShapeID;
	private JPanel sizeSliderPanel;
	private JPanel opacitySliderPanel;
	private JPanel thicknessSliderPanel;
	private JSlider sizeSlider;
	private JSlider opacitySlider;
	private JSlider thicknessSlider;
	private JLabel sizeJLabel;
	private JLabel opacityJLabel;
	private JLabel thicknessJLabel;
//	protected int panelHeight=500;
	protected ArrayList<MarkingLayer> markingLayerList;
	private ShapeDrawer shapeDrawer;
	protected JLayeredPane layeredPane;
	private PreviewShapePanel previewShapePanel;



	public MarkingProperties(JFrame frame, GUI gui, Point point, MarkingLayer mLayer){
		super(frame, gui,point);
		this.gui = gui;
		this.topLeftPoint=point;

	//	this.maxHeight=300;
		if(mLayer != null){
			this.markingLayer=mLayer;
			this.setSelectedColor(this.markingLayer.getColor());
			this.setSelectedSize(this.markingLayer.getSize());
			this.setSelectedThickness(this.markingLayer.getThickness());
			this.setSelectedOpacity(changeUnderZeroFloatToInt(this.markingLayer.getOpacity()));
			this.setSelectedShapeID(this.markingLayer.getShapeID());
			this.shapeDrawer =new ShapeDrawer(mLayer.getShapeID(), getSelectedSize(), changeIntToFloat(getSelectedThickness()), this.markingLayer.getOpacity());
		}
		initMarkingPropertiesPanel();

	}

	public MarkingProperties(JFrame frame, GUI gui, Point point, ArrayList<MarkingLayer> mLayerList){
		super(frame, gui,point);
		this.gui = gui;
		this.topLeftPoint=point;
		this.markingLayerList=mLayerList;
	//	this.maxHeight=300;

		initMarkingPropertiesPanel();

	}

	protected void initMarkingPropertiesPanel(){
		initDialog();
		this.revalidate();
		this.repaint();
	}
	
	/**
	 *  Setups the components of Dialog window
	 */
	protected void initDialog(){
		try {
			//setup fonts

		//	this.setBounds(gui.getBounds()); // sets the size of this dialog same as the GUI (the parent)
			this.setBounds(gui.getVisibleWindowBounds()); // sets the size of this dialog same as the GUI (the parent)
			this.setUndecorated(true); // no titlebar or buttons
			this.setBackground(new Color(0,0,0,0)); // transparent color
//			if(checkBounds()) // if GUI is over screen -> not using dimming of GUI (dimming would be positioned wrong)
			this.setContentPane(new ContentPane()); // makes dimming over GUI
			this.getContentPane().setBackground(Color_schema.dark_30);
			this.setLayout(null); // backpanel position is determined with setBounds(..)
			
			layeredPane=new JLayeredPane();
			layeredPane.setLayout(null);
			layeredPane.setBorder(BorderFactory.createEmptyBorder());
			layeredPane.setBounds(this.getBounds());
			
			ContentPane dimPane = new ContentPane();
			dimPane.setLayout(null);
			dimPane.setBounds(this.getBounds());
			dimPane.setBackground(Color.blue);
	//		layeredPane.add(dimPane, JLayeredPane.DEFAULT_LAYER);
			

			backPanel = new JPanel();
			backPanel.setLayout(new BorderLayout());
			backPanel.setBorder(BorderFactory.createLineBorder(Color_schema.button_light_border, 3));
			backPanel.setMaximumSize(new Dimension(panelWidth,panelHeight));
			backPanel.setMinimumSize(new Dimension(panelWidth,panelHeight));
			backPanel.setPreferredSize(new Dimension(panelWidth,panelHeight));

			// set sizes and locations of components
			setPanelPosition();
			
			backPanel.add(initUPPanels(), BorderLayout.PAGE_START);
			backPanel.add(initCenterPanels(), BorderLayout.CENTER);
			backPanel.add(initDownPanel(),BorderLayout.PAGE_END);

			layeredPane.add(backPanel,JLayeredPane.DEFAULT_LAYER);
			
			previewShapePanel = new PreviewShapePanel(this.getSelectedThickness(), changeIntToFloat(this.getSelectedOpacity()), this.getSelectedShapeID(), this.getSelectedSize(), this.getSelectedColor(), this.recOfBackpanel, gui.getVisibleWindowBounds());
			layeredPane.add(previewShapePanel,JLayeredPane.DRAG_LAYER);
			this.layeredPane.moveToFront(previewShapePanel);
	//		this.layeredPane.moveToBack(dimPane);
			
			
			this.add(layeredPane);
			this.repaint();


		} catch (Exception e) {
			LOGGER.severe("Error in initializing PropertiesDialog: " +e.getClass().toString() + " :" +e.getMessage() +" line: " +e.getStackTrace()[2].getLineNumber());
			e.printStackTrace();
		}
	}


	protected JPanel initUPPanels(){
		// contains title and colorchooser panels
					JPanel upperBackPanel= new JPanel();
					upperBackPanel.setLayout(new BoxLayout(upperBackPanel, BoxLayout.PAGE_AXIS));


					upperBackPanel.setMaximumSize(new Dimension(panelWidth,180));
					upperBackPanel.setMinimumSize(new Dimension(panelWidth,180));
					upperBackPanel.setPreferredSize(new Dimension(panelWidth,180));


					// contains label and colorchooser object
					JPanel colorChooserPanel = new JPanel();
					colorChooserPanel.setLayout(new BoxLayout(colorChooserPanel, BoxLayout.PAGE_AXIS));
				//	colorChooserPanel.setBorder(BorderFactory.createLineBorder(Color_schema.dark_100, 1));

					colorChooserPanel.setMaximumSize(new Dimension(panelWidth,140));
					colorChooserPanel.setMinimumSize(new Dimension(panelWidth,140));
					colorChooserPanel.setPreferredSize(new Dimension(panelWidth,140));

					// contains colorChooser-component
					JPanel colorLabelJPanel = new JPanel();
					colorLabelJPanel.setLayout(new FlowLayout(FlowLayout.LEFT,10,6));
					colorLabelJPanel.setMinimumSize(new Dimension(panelWidth,30));
					colorLabelJPanel.setMinimumSize(new Dimension(panelWidth,30));
					colorLabelJPanel.setMinimumSize(new Dimension(panelWidth,30));
					JLabel setColorLabel = new JLabel("SELECT COLOR:");

					setColorLabel.setFont(Fonts.b14);

					colorLabelJPanel.add(setColorLabel);

					colorChooserPanel.add(colorLabelJPanel);

					// setup colorchooser
					colorChooser = new JColorChooser(this.getSelectedColor());
					colorChooser.setPreviewPanel(new JPanel());


					// Retrieve the current set of panels
					AbstractColorChooserPanel[] oldPanels = colorChooser.getChooserPanels();
			/*		for (int i=0; i<oldPanels.length; i++) {
						 String cName = oldPanels[i].getDisplayName();

						// LOGGER.fine(" name:"+cName);
					}*/
					// Remove all panels except the Swathces panel
					for (int i=0; i<oldPanels.length; i++) {
					    String clsName = oldPanels[i].getDisplayName();
					    if (clsName.equals("HSV")) {
					        // Remove swatch chooser if desired
					        colorChooser.removeChooserPanel(oldPanels[i]);
					    } else if (clsName.equals("HSL")) {
					        // Remove rgb chooser if desired
					        colorChooser.removeChooserPanel(oldPanels[i]);
					    } else if (clsName.equals("RGB")) {
					        // Remove hsb chooser if desired
					        colorChooser.removeChooserPanel(oldPanels[i]);
					    }
					    else if (clsName.equals("CMYK")) {
					        // Remove hsb chooser if desired
					        colorChooser.removeChooserPanel(oldPanels[i]);
					    }

					}
					AbstractColorChooserPanel swatchPanel = colorChooser.getChooserPanels()[0]; // get the first panel that is swatchPanel
					JPanel p = (JPanel) swatchPanel.getComponent(0); //get JPanel
					p.remove(2);	// Remove the recent Panel
					p.remove(1);	// Remove the recent JLabel


					// listen when new color is selected
					colorChooser.getSelectionModel().addChangeListener(new ChangeListener() {

						@Override
						public void stateChanged(ChangeEvent arg0) {
							if(colorChooser.getColor() != null){
								setSelectedColor(colorChooser.getColor());
							}
							// update shapeBox's shape color
							if(shapeBox != null){
								shapeBox=setUpComboBox();
								comboBoxPanel.repaint();
							}
							
							if(previewShapePanel != null){
								previewShapePanel.setShapeColor(selectedColor);
								previewShapePanel.repaint();
								
							}



						}
					});
					colorChooserPanel.add(colorChooser);
					upperBackPanel.add(initTitlePanel("EDIT MARKING PROPERTIES"));
					upperBackPanel.add(colorChooserPanel);
					return upperBackPanel;
	}

	protected JPanel initCenterPanels(){


		// contains combobox and slider panels
		boxAndSlidersPanel = new JPanel();
		boxAndSlidersPanel.setLayout(new BoxLayout(boxAndSlidersPanel, BoxLayout.PAGE_AXIS));
		setUpComboBoXPanel();
		if(comboBoxPanel != null)
		boxAndSlidersPanel.add(comboBoxPanel);

		// Setup sliders for size, opacity, thickness
		sizeSliderPanel = setUpSLiderPanel(ID.SIZE_SLIDER, "SET SIZE: ", 5, 100, this.getSelectedSize(), 10, 100);
		thicknessSliderPanel = setUpSLiderPanel(ID.THICKNESS_SLIDER, "SET THICKNESS: ", 1, 20, this.getSelectedThickness(), 1, 5);
		opacitySliderPanel = setUpSLiderPanel(ID.OPACITY_SLIDER, "SET OPACITY: ", 1, 100, this.getSelectedOpacity(), 10, 100);
		boxAndSlidersPanel.add(sizeSliderPanel);
		boxAndSlidersPanel.add(thicknessSliderPanel);
		boxAndSlidersPanel.add(opacitySliderPanel);

		return  boxAndSlidersPanel;
	}

	protected void setUpComboBoXPanel(){
		// contains JComboBox-component and label
		comboBoxPanel = new JPanel();
		comboBoxPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		comboBoxPanel.setMaximumSize(new Dimension(panelWidth,50));
		comboBoxPanel.setMinimumSize(new Dimension(panelWidth,50));
		comboBoxPanel.setPreferredSize(new Dimension(panelWidth,50));

		JLabel boxLabel = new JLabel("SELECT SHAPE: ");
		boxLabel.setFont(Fonts.b14);

		// Setup JCompoBox for selecting shape
		shapeBox = setUpComboBox();
		comboBoxPanel.add(boxLabel);
		comboBoxPanel.add(shapeBox);

	}




/*
	protected void setPanelPosition(){
		Rectangle rec= getGoodBounds(); // calculates is the dialog panel going over the screen
		if(rec != null)
			backPanel.setBounds(rec);
		else
			backPanel.setBounds((int)(this.topLeftPoint.getX()-(panelWidth)), (int)this.topLeftPoint.getY(),this.panelWidth,this.panelHeight);
	//	backPanel.setPreferredSize(new Dimension((int)colorChooser.getPreferredSize().getWidth(),(int)colorChooser.getPreferredSize().getHeight()+this.maxHeight));
	//	comboBoxPanel.setMaximumSize(new Dimension((int)backPanel.getBounds().getWidth()-20,50));
	//	comboBoxPanel.setPreferredSize(new Dimension((int)backPanel.getBounds().getWidth()-20,50));
	}
*/
	/**
	 * The method checks is GUI -window positioned outside of screen and returns value  true / false
	 * @return boolean value true if the borders of window is exceeding screen size, otherwise false
	 */
	/*
	private boolean checkBounds(){
		LOGGER.fine("checkbounds" + gui.getBounds().getY() +" " +gui.getBounds().getHeight() +"" +gui.getScreenSize().getHeight());
		if((gui.getBounds().getY() +gui.getBounds().getHeight()) > gui.getScreenSize().getHeight() ||
				(gui.getBounds().getX() + gui.getBounds().getWidth()) > gui.getScreenSize().getWidth() ||
				gui.getBounds().getX() <0 || gui.getBounds().getY() <0)
			return false;
		return true;
	}
*/
	/**
	 * Calculates the location and size values for Dialog window to fit in screen
	 * @return Rectangle containing appropriate location and size values for the Dialog window.
	 */
	/*
	private Rectangle getGoodBounds(){
		try {
			//this.topLeftPoint
			int topleftX = (int)(this.topLeftPoint.getX()-colorChooser.getPreferredSize().getWidth());
			int topleftY = (int)this.topLeftPoint.getY();
			int width = (int)this.colorChooser.getPreferredSize().getWidth();
			int height = (int)this.colorChooser.getPreferredSize().getHeight()+this.maxHeight;

			int guiDownY=(int)(this.gui.getBounds().getHeight());

			int guiRightX=(int)(this.gui.getBounds().getWidth());
			int guiLeftX=(int)(this.gui.getBounds().getX());
			int guiUpY=(int)(this.gui.getBounds().getY());

			LOGGER.fine("height: " +this.gui.getBounds().getHeight() + " y: " +gui.getBounds().getY()+ "width: "+ this.gui.getBounds().getWidth()+ " x: "+gui.getBounds().getX());
			// goes over at down
			if(topleftY+height > guiDownY){
				topleftY=guiDownY-height-50;
				LOGGER.fine("over down");
			}
			else{
				// goes over at up
				if(topleftY<  guiUpY){
					topleftY=guiUpY+50;
					LOGGER.fine("over up");
				}
			}
			// goes over at right
			if(topleftX+width > guiRightX){
				topleftX = guiRightX-width-50;
				LOGGER.fine("over right");
			}
			else{
				// goes over at left
				if(topleftX <  guiLeftX){
					topleftX = guiLeftX+50;
					LOGGER.fine("over left");
				}
			}

			// check is the bounds over screeen
			// goes over at down
			if(topleftY+height > gui.getScreenSize().getHeight()){
				topleftY=(int)gui.getScreenSize().getHeight() -height-50;
				LOGGER.fine("over screen down");
			}
			// goes down at right
			if(topleftX+width > gui.getScreenSize().getWidth()){
				topleftX = (int)gui.getScreenSize().getWidth()-width-50;
				LOGGER.fine("over screen right");
			}
			// goes over at up
			if(topleftY<  0){
				topleftY=50;
				LOGGER.fine("over screen up");
			}
			// goes over at left
			if(topleftX <  0){
				topleftX = 50;
				LOGGER.fine("over screen left");
			}

			return new Rectangle(topleftX, topleftY, width , height);
		} catch (Exception e) {
			LOGGER.severe("Error in counting Bounds for MarkingProperties: " +e.getClass().toString() + " :" +e.getMessage() +"line: " +e.getStackTrace()[2].getLineNumber());
			return null;
		}
	}
*/
	/**
	 * Converts float value to integer. The value is multiplied with 100: float 0,1F -> integer 10.
	 * @param fl float to be changed
	 * @return parameter float value * 100 as int
	 */
	protected int changeUnderZeroFloatToInt(float fl){
		try {
			fl*=100;
			return (int)fl;
		} catch (Exception e) {
			LOGGER.severe("Error in converting float to int: " +e.getClass().toString() + " :" +e.getMessage());
			return 10;
		}
	}

	/**
	 * Converts integer value to float. The value isdivided with 100: int 10 -> float 0,1F.
	 * @param value integer value to be converted
	 * @return parameter integer value / 100 as float
	 */
	protected float changeIntToFloat(int value){
		try {
			float valueF = (float)value/100;
			return valueF;
		} catch (Exception e) {
			LOGGER.severe("Error in converting float to int: " +e.getClass().toString() + " :" +e.getMessage());
			return 1.0F;
		}

	}

	/**
	 * Creates JPanel containing JSlider for changing numerical properties of MarkingLayer.
	 * @param typeOfSlider ID for identifying the slider type. @see information.ID
	 * @param labelText the string title to be shown
	 * @param minValue the minimum value of slider
	 * @param maxValue the maximum value of slider
	 * @param initValue the selected value of slider in the beginning
	 * @param minorTicks the minor thicks of slider
	 * @param majorTicks the major thicks of slider
	 * @return a JPanel containing title, slider and label showing selected value
	 */
	private JPanel setUpSLiderPanel(int typeOfSlider, String labelText, int minValue, int maxValue, int initValue, int minorTicks, int majorTicks){
		try {

			JPanel backSliderPanel=new JPanel();
			backSliderPanel.setLayout(new BorderLayout(2,2));
			backSliderPanel.setBorder(BorderFactory.createLineBorder(Color_schema.dark_100, 1));
			JPanel sliderPanel = new JPanel();
			sliderPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));

			JPanel labelPanel=new JPanel();
			labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 2));
			JLabel label = new JLabel(labelText);

			label.setFont(Fonts.b14);
			JSlider slider = new JSlider(JSlider.HORIZONTAL,minValue,maxValue, initValue);
			slider.putClientProperty("type", typeOfSlider);


			JLabel numberLabel = new JLabel(""+initValue);
			numberLabel.putClientProperty("name", ID.NUMBER_LABEL); // by adding this can JLabels be identified in statechanged -listener
			numberLabel.setFont(Fonts.b14);

			slider.setMajorTickSpacing(majorTicks);
			slider.setMinorTickSpacing(minorTicks);
			slider.setPaintTicks(true);

			// select the slider type
			switch (typeOfSlider){
			case ID.SIZE_SLIDER:
				this.sizeSlider = slider;
				this.sizeJLabel = numberLabel;
				break;
			case ID.THICKNESS_SLIDER:
				this.thicknessSlider = slider;
				this.thicknessJLabel = numberLabel;
				break;
			case ID.OPACITY_SLIDER:
				this.opacitySlider=slider;
				this.opacityJLabel = numberLabel;
				break;
			}
			labelPanel.add(label);
			sliderPanel.add(slider);
			sliderPanel.add(numberLabel);

			slider.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {

				if((int)((JSlider)e.getSource()).getClientProperty("type") == ID.SIZE_SLIDER){
					setSelectedSize(sizeSlider.getValue());
					setMaximumThickness();
					sizeJLabel.setText(""+sizeSlider.getValue());
				//	previewShapePanel.setShapeSize(sizeSlider.getValue());
					
				}
				else
					if((int)((JSlider)e.getSource()).getClientProperty("type") == ID.THICKNESS_SLIDER){

						setSelectedThickness(thicknessSlider.getValue());
						thicknessJLabel.setText(""+thicknessSlider.getValue());
					//	previewShapePanel.setShapeThickness(changeIntToFloat(getSelectedThickness()));
					}
					else
						if((int)((JSlider)e.getSource()).getClientProperty("type") == ID.OPACITY_SLIDER){

							setSelectedOpacity(opacitySlider.getValue()); // in slider int values 1-100 -> 0.01F-1.0F
							opacityJLabel.setText(""+opacitySlider.getValue());
						
						//	previewShapePanel.setShapeOpacity(changeIntToFloat(opacitySlider.getValue()));
						
							

						}
				
			//	previewShapePanel.repaint();
				}
				
			});
			
			slider.addMouseListener(new MouseListener() {
				
				@Override
				public void mouseReleased(MouseEvent e) {
					if((int)((JSlider)e.getSource()).getClientProperty("type") == ID.SIZE_SLIDER){					
						previewShapePanel.setShapeSize(sizeSlider.getValue());
						if(thicknessSlider !=null && thicknessSlider.getValue() >0)
						previewShapePanel.setShapeThickness(thicknessSlider.getValue());
						
					}
					else
						if((int)((JSlider)e.getSource()).getClientProperty("type") == ID.THICKNESS_SLIDER){
							previewShapePanel.setShapeThickness(thicknessSlider.getValue());
						}
						else
							if((int)((JSlider)e.getSource()).getClientProperty("type") == ID.OPACITY_SLIDER){
								previewShapePanel.setShapeOpacity(changeIntToFloat(opacitySlider.getValue()));
							
								

							}

					previewShapePanel.repaint();
					
				}
				
				@Override
				public void mousePressed(MouseEvent arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void mouseExited(MouseEvent arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void mouseEntered(MouseEvent arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void mouseClicked(MouseEvent arg0) {
					// TODO Auto-generated method stub
					
				}
			});
			
			
			backSliderPanel.add(labelPanel,BorderLayout.PAGE_START);
			backSliderPanel.add(sliderPanel, BorderLayout.CENTER);
			slider.setMaximumSize(new Dimension(350,40));
			slider.setPreferredSize(new Dimension(350,40));

			return backSliderPanel;
		} catch (Exception e) {
			LOGGER.severe("Error in initializing sliders: " +e.getClass().toString() + " :" +e.getMessage());
			return null;
		}
	}


	  /**
	 *  This method is called when the value of sizeSlider is changed.
	 *  -> Changes the maximum and present value of thicknessSlider, because small shape shouldn't be very thick.
	 */
	private void setMaximumThickness(){
		  try {
			int size = this.getSelectedSize();
			  if(size >0){
			  int maxValue = (int)(size/3);
			  if(maxValue<1)
				  maxValue =3;
			  
			  if(maxValue>20)
				  maxValue=20;

			  int presentValue = (int)(maxValue/2);
			  if(presentValue <1)
				  presentValue =1;

			  this.thicknessSlider.setMaximum(maxValue);

			  this.thicknessSlider.setValue(presentValue);
			  this.thicknessJLabel.setText(""+presentValue);
			  this.setSelectedThickness(presentValue);
			//  this.thicknessSliderPanel.revalidate();
			  this.thicknessSliderPanel.repaint();
			  }
		} catch (Exception e) {
			LOGGER.severe("Error in changing maximumThickenss values: " +e.getClass().toString() + " :" +e.getMessage());

		}

	  }



	/**
	 * Hides Dialog window and saves the changes made to MarkingLayer.
	 * @param saveChanges boolean value should the changes be saved to MarkingLayer
	 */
	protected void hideDialog(boolean saveChanges){
		//LOGGER.fine("color selected: " +colorChooser.getColor().toString());
		if(saveChanges){
			try {
			saveChanges(this.markingLayer);
			gui.setMadeChanges(true);
			} catch (Exception e) {
				LOGGER.severe("Error in saving marking properties to MarkingLayer " +e.getClass().toString() + " :" +e.getMessage());

			}
			// update the GUI ImageLayerInfo JPanel
			gui.updateMarkingPanelProperties(this.markingLayer.getLayerID());
		//	gui.updateImageLayerInfos();

		}
		this.setVisible(false);
		dispose();
	}

	protected void saveChanges(MarkingLayer mLayer){
		// save changes to markinglayer
		// set color from colorchooser
		mLayer.setColor(this.getSelectedColor());
		// set shape id
		mLayer.setShapeID(this.getSelectedShapeID());
		if(this.getSelectedSize()>0){
			// set size value
			mLayer.setSize(this.getSelectedSize());
		}

		if(this.getSelectedThickness()>0){
			// set thickness value
			mLayer.setThickness(this.getSelectedThickness());
		}

		if(this.getSelectedOpacity()> 0.0F){
			// set opacity value
			mLayer.setOpacity(changeIntToFloat(this.getSelectedOpacity()));
		}
	}

	/**
	 * Sets the Dialog visible
	 */
	public void showDialog(){
		setVisible(true);
	}



/**
 * Setups JComboBox for selecting shape for MarkingLayer. JComboBox contains array of integers,
 * which are corresponding to shapeID values of int[] chapeIDs -array.
 * @return JComboBox-object for selecting Shapes of Markings
 */
private JComboBox<Integer> setUpComboBox(){


	try {
		int[] tempIDlist = {ID.SHAPE_CIRCLE, ID.SHAPE_CROSS, ID.SHAPE_DIAMOND, ID.SHAPE_OVAL, ID.SHAPE_PLUS, ID.SHAPE_SQUARE, ID.SHAPE_TRIANGLE};
		shapeIDs=new int[tempIDlist.length]; // can't initialize global variable other way

	//	shapePanels = new MarkingShape[shapeIDs.length]; // 6 different shapes
		shapeIcons = new ShapeIcon[shapeIDs.length];
		Integer[] intArray = new Integer[shapeIDs.length];
		for (int i = 0; i < shapeIDs.length; i++) {
			shapeIDs[i]=tempIDlist[i];
		    intArray[i] = new Integer(i);
		   // shapePanels[i] = createShapePanel(shapeIDs[i]);
		    shapeIcons[i] =  createShapeIcon(shapeIDs[i]);
		}
		JComboBox<Integer> box = new JComboBox<Integer>(intArray);
		icon_renderer= new ComboBoxIconRenderer(); // create renderer for JComboBox
		icon_renderer.setPreferredSize(new Dimension(32,32));
		box.setRenderer(icon_renderer); // set renderer

		box.setMaximumSize(new Dimension(50,37));
		box.setPreferredSize(new Dimension(50,37));
		box.setBackground(Color_schema.dark_40);
		box.setForeground(Color_schema.white_230);

		box.setMaximumRowCount(this.shapeIcons.length);
		box.setSelectedIndex(getselectedMarkingLayerShapeIndex());
		box.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int index= ((JComboBox<Integer>)e.getSource()).getSelectedIndex();
				setSelectedShapeID(getShapeIDfromComboBoxIndex(index));
				previewShapePanel.setShapeID(getShapeIDfromComboBoxIndex(index));
				previewShapePanel.repaint();
			}
		});


		return box;
	} catch (Exception e) {
		LOGGER.severe("Error in initializing combobox: " +e.getClass().toString() + " :" +e.getMessage());
		return null;
	}
}

/**
 * Return shape ID (int) located from shapeIDs-array at given index
 * @param index the index of shapeComboBox
 * @return ID (int) of shape that corresponds to shapeIDs -array at given index
 */
private int getShapeIDfromComboBoxIndex(int index){

	if(index >=0 && index < shapeIDs.length)
		return shapeIDs[index];

	return shapeIDs[0];
}

/**
 * Manages creation of shape Icon
 * @param id shape ID. @see information.ID
 * @return Custom Icon with painted shape
 */
private ShapeIcon createShapeIcon(int id){
	return new ShapeIcon(id, 32,32,getSelectedColor(), Color_schema.dark_40);
}

/**
 *  Sets the selected color
 * @param color the color that will be saved
 */
protected void setSelectedColor(Color color){
	if(color != null)
		this.selectedColor=color;
	else
		this.selectedColor=new Color(51,255,51); // just for case but hardly happens ever
}

/**
 *
 * @return the selected color for marking
 */
private Color getSelectedColor(){
	return this.selectedColor;
}

/**
 * Gives the index of shape JCombobox corresponding to shapeID of MarkingLayer
 * @return int index of combobox corresponding for Shape in MarkingLayer
 */
private int getselectedMarkingLayerShapeIndex(){
	try {
		for(int i=0;i < this.shapeIDs.length; i++){
			if(shapeIDs[i] == this.getSelectedShapeID()){
				LOGGER.fine("found shapeID: "+i);
				return i;
			}
		}
		LOGGER.fine("doesn't found shapeID");
		return 0;
	} catch (Exception e) {
		LOGGER.severe("Error in searching selected shape of Markinglayer as combobox index: " +e.getClass().toString() + " :" +e.getMessage());
		return 0;
	}

}
/*
private MarkingShape createShapePanel(int shapeID){
	return new MarkingShape(shapeID, 30, 30, colorChooser.getColor(), 1.0f, 1.0f, 15, 15);
}
*/

/**
 * @return the size of shape
 */
public int getSelectedSize() {
	return selectedSize;
}

/**
 * @param selectedSize the new size (int) of shape to be saved
 */
public void setSelectedSize(int selectedSize) {
	this.selectedSize = selectedSize;
}

/**
 * @return the selected thickness (int)
 */
public int getSelectedThickness() {
	return selectedThickness;
}

/**
 * @param selectedThickness the thickenss (int) of shape to be saved
 */
public void setSelectedThickness(int selectedThickness) {
	this.selectedThickness = selectedThickness;
}

/**
 * @return the selected opacity (float)
 */
public int getSelectedOpacity() {
	return this.selectedOpacity;
}

/**
 * @param selectedOpacity the new opacity value (float) of shape to be saved
 */
public void setSelectedOpacity(int selectedOpacity) {
	this.selectedOpacity = selectedOpacity;
}

/**
 * @return the shape ID of selected shape. @see information.ID
 */
public int getSelectedShapeID() {
	return this.selectedShapeID;
}

/**
 * @param selectedShapeID the shape id (int) to been saved
 */
public void setSelectedShapeID(int selectedShapeID) {
	this.selectedShapeID = selectedShapeID;
}

private void refresh(){
	this.repaint();
}
/*
@Override
public void paint(Graphics g) {
	// TODO Auto-generated method stub
	super.paint(g);
	Graphics2D g2d = (Graphics2D) g.create();
	g2d.setPaint(selectedColor);
	g2d.setStroke(new BasicStroke(this.selectedThickness)); // set thickness
	RenderingHints rh= new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	g2d.setRenderingHints(rh);
	
	switch (this.selectedShapeID){
		case ID.SHAPE_CIRCLE:
			this.shapeDrawer.drawCircle(g2d,this.recOfBackpanel.x-selectedSize-50, this.recOfBackpanel.y+this.selectedSize);
			break;
		case ID.SHAPE_CROSS:
			this.shapeDrawer.drawCross(g2d,this.recOfBackpanel.x-selectedSize-50, this.recOfBackpanel.y+this.selectedSize);	
			break;
		case ID.SHAPE_DIAMOND:
			this.shapeDrawer.drawDiamond(g2d,this.recOfBackpanel.x-selectedSize-50, this.recOfBackpanel.y+this.selectedSize);	
			break;
		case ID.SHAPE_OVAL:
			this.shapeDrawer.drawOval(g2d,this.recOfBackpanel.x-selectedSize-50, this.recOfBackpanel.y+this.selectedSize);	
			break;
		case ID.SHAPE_PLUS:
			this.shapeDrawer.drawPlus(g2d,this.recOfBackpanel.x-selectedSize-50, this.recOfBackpanel.y+this.selectedSize);	
			break;
		case ID.SHAPE_SQUARE:
			this.shapeDrawer.drawSquare(g2d,this.recOfBackpanel.x-selectedSize-50, this.recOfBackpanel.y+this.selectedSize);	
			break;
		case ID.SHAPE_TRIANGLE:
			this.shapeDrawer.drawTriangle(g2d,this.recOfBackpanel.x-selectedSize-50, this.recOfBackpanel.y+this.selectedSize);	
			break;
	
	}
	
	g2d.dispose();
	
}
*/

/*
private class ShapeComboboxRenderer extends JPanel implements ListCellRenderer{
	JPanel innerPanel;

	private ShapeComboboxRenderer(){
		 setOpaque(true);
		 this.setPreferredSize(new Dimension(40,40));
		 this.setMaximumSize(new Dimension(40,40));
		//this.setLayout(new BorderLayout());
		 this.setLayout(new BorderLayout(5,5));
		this.setBackground(Color_schema.color_dark_30_bg);
		// this.setLayout(ne);
		innerPanel = new JPanel();
		innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.LINE_AXIS));
		innerPanel.setMaximumSize(new Dimension(40,40));
		innerPanel.setMinimumSize(new Dimension(40,40));
		innerPanel.setPreferredSize(new Dimension(40,40));
		innerPanel.setBackground(Color_schema.color_dark_30_bg);

       //  setVerticalAlignment(CENTER);
	}


	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {


		int selectedIndex = ((Integer)value).intValue();
		LOGGER.fine("selected index " +index+ " value" +selectedIndex);

		  if (isSelected) {
            //  setBorder(BorderFactory.createLineBorder(Color_schema.color_orange_bright, 2));  //(list.getSelectionBackground());
           //    setBorder(list.getsel);(list.getSelectionForeground());
          } else {
         //     setBorder
       //       setForeground(list.getForeground());
          }

		     if (isSelected) {
              innerPanel.setBackground(Color_schema.color_dark_50_bg);
              ((MarkingShape)shapePanels[selectedIndex]).setBackground(Color_schema.color_dark_50_bg);
              innerPanel.setForeground(list.getForeground());
          } else {
        	  innerPanel.setBackground(Color_schema.color_dark_30_bg);
        	  ((MarkingShape)shapePanels[selectedIndex]).setBackground(Color_schema.color_dark_30_bg);
        	  innerPanel.setForeground(list.getForeground());
          }

		  this.removeAll();
		  this.revalidate();
		  innerPanel.removeAll();
		  innerPanel.revalidate();
		  innerPanel.add(Box.createRigidArea(new Dimension(2,2)));
		  innerPanel.add(shapePanels[selectedIndex]);
		//  this.add(shapePanels[selectedIndex], BorderLayout.CENTER);
		  this.add(innerPanel,BorderLayout.CENTER);

		return this;
	}
}
*/
	/**
	 * Custom renderer for JComboBox. It places icons to ComboBox list and manages drawing the effects when items viewed
	 * @author Antti Kurronen
	 *
	 */
	class ComboBoxIconRenderer extends JLabel implements ListCellRenderer {


	/**
	 * Class constructor
	 */
	public ComboBoxIconRenderer() {
		setOpaque(true);
		setHorizontalAlignment(SwingConstants.CENTER);
		setVerticalAlignment(SwingConstants.CENTER);
		setBackground(Color_schema.dark_40);
	}

	/*
	* This method finds the image and text corresponding
	* to the selected value and returns the label, set up
	* to display the text and image.
	*/
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		//Get the selected index. (The index param isn't
		//always valid, so just use the value.)
		int selectedIndex = ((Integer)value).intValue();

		if (isSelected) {
			setBackground(list.getBackground());
			setForeground(list.getSelectionForeground());
			setBorder(BorderFactory.createLineBorder(Color_schema.orange_bright, 1));
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
			setBorder(BorderFactory.createEmptyBorder());
		}

		//Set the icon
		ShapeIcon icon = shapeIcons[selectedIndex];
		setIcon(icon);
		return this;
	}


	}

}
