package gui.grid;

import information.Fonts;
import information.GridProperties;
import information.ID;
import information.ImageLayer;
import information.MarkingLayer;
import information.PositionedRectangle;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.metal.MetalIconFactory.FolderIcon16;

import gui.Color_schema;
import gui.GUI;
import gui.MouseListenerCreator;
import gui.PropertiesDialog;

/**
 * 
 * Dialog panel, which shows settings for selecting grid dimensions and visibility of grid and single cells of grid.
 * @author Antti Kurronen
 *
 */
public class GridPropertiesPanel extends PropertiesDialog {
	private final static Logger LOGGER = Logger.getLogger("MCCLogger");
	private ArrayList<MarkingLayer> markingLayerList;
	private boolean gridON;
	private JButton on_button;
	private JButton off_button;
	private ArrayList<SingleGridSize> gridSizes;
	private JPanel gridPanel;
	private JComboBox<String> gridComboBox;
	private JSlider randomSlider;
	private JPanel backGridExamplePanel;
	private JLabel gridLabel;
	private JLabel comboLabel;
	private GridProperties templateGP=null; // GridProperty which data is used as template for selected markingLayer(s) 
	private JLabel sliderLabel;
	private JLabel sliderValueLabel;
	



	/**
	 * Constructor of class.
	 * @param frame JFrame of owner
	 * @param gui GUI object
	 * @param point Point top left point location of window
	 * @param markingLayerList ArrayList of MarkingLayers, which are modified
	 * @param gridSizeList ArrayList of SingleGridSizes, determining the grid dimension.
	 */
	public GridPropertiesPanel(JFrame frame, GUI gui, Point point, ArrayList<MarkingLayer> markingLayerList, ArrayList<SingleGridSize> gridSizeList) {
		super(frame, gui, point);
		this.markingLayerList=markingLayerList;
		this.gridON= isAnyGridON();

		this.gridSizes=gridSizeList;
		this.templateGP= getFirstGridPropertiesFromAllMarkingLayers(0, 0);
		initDialog();
	
	}

	/* (non-Javadoc)
	 * @see gui.PropertiesDialog#initUPPanels()
	 */
	protected JPanel initUPPanels() throws Exception{

		JPanel upperBackPanel= new JPanel();
		upperBackPanel.setLayout(new BorderLayout());
		upperBackPanel.add(initTitlePanel("Set Grid for all MarkingLayers"), BorderLayout.PAGE_START);

		JPanel buttonTitlePanel = new JPanel();
		buttonTitlePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 3));

		buttonTitlePanel.setBackground(Color_schema.dark_30);
		buttonTitlePanel.setMaximumSize(new Dimension(150,36));
		buttonTitlePanel.setMinimumSize(new Dimension(150,36));
		buttonTitlePanel.setPreferredSize(new Dimension(150,36));
		JLabel buttontitleJLabel= new JLabel("SET GRID ON/OFF:");
		buttontitleJLabel.setFont(Fonts.b14);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT,  20, 3));
		buttonPanel.setBorder(BorderFactory.createLineBorder(Color_schema.grey_100, 1));
		buttonPanel.setMaximumSize(new Dimension(150,36));
		buttonPanel.setMinimumSize(new Dimension(150,36));
		buttonPanel.setPreferredSize(new Dimension(150,36));
		buttonPanel.setBackground(Color_schema.dark_30);
		buttonPanel.add(buttontitleJLabel);

		on_button = new JButton(gui.getImageIcon("/images/on_unselected.png"));
		on_button.setMaximumSize(new Dimension(45,30));
		on_button.setMinimumSize(new Dimension(45,30));
		on_button.setPreferredSize(new Dimension(45,30));
		on_button.setFocusable(false);
		on_button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				gridON=true;
				updateOnOffButtonIcons();
				enableComponents();

			}
		});

		on_button.setBorder(BorderFactory.createLineBorder(Color_schema.dark_30, 2));

		MouseListenerCreator.addMouseListenerToNormalButtonsWithBlackBorder(on_button);

		off_button = new JButton(gui.getImageIcon("/images/off_selected.png"));
		off_button.setMaximumSize(new Dimension(44,30));
		off_button.setMinimumSize(new Dimension(44,30));
		off_button.setPreferredSize(new Dimension(44,30));
		off_button.setFocusable(false);
		off_button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				gridON=false;
				updateOnOffButtonIcons();
				enableComponents();

			}
		});

		off_button.setBorder(BorderFactory.createLineBorder(Color_schema.dark_30, 2));
		MouseListenerCreator.addMouseListenerToNormalButtonsWithBlackBorder(off_button);

		updateOnOffButtonIcons();

		buttonPanel.add(on_button);
		buttonPanel.add(off_button);
//		buttonBackPanel.add(buttonPanel);
//		buttonBackPanel.setBorder(BorderFactory.createLineBorder(Color_schema.button_grey_border, 1));
		//updateGridDimensionFromComboBox
		upperBackPanel.add(buttonPanel, BorderLayout.CENTER);


		return upperBackPanel;

	}

	/* (non-Javadoc)
	 * @see gui.PropertiesDialog#initCenterPanels()
	 */
	protected JPanel initCenterPanels(){
		JPanel centerBackPanel = new JPanel();
		centerBackPanel.setLayout(new BoxLayout(centerBackPanel, BoxLayout.PAGE_AXIS));
		centerBackPanel.setMaximumSize(new Dimension(panelWidth,300));
		centerBackPanel.setMinimumSize(new Dimension(panelWidth,300));
		centerBackPanel.setPreferredSize(new Dimension(panelWidth,300));

		centerBackPanel.add(setUpComboBoxPanel());	
		centerBackPanel.add(setupSliderPanel());
		centerBackPanel.add(setUpGridExamplePanel());

		return centerBackPanel;
	}

	/**
	 * Creates JPanel containing combobox for size of the grid
	 * @return JCombobox object
	 */
	private JPanel setUpComboBoxPanel(){
		try{
		JPanel comboBoxPanel=new JPanel();
		comboBoxPanel.setLayout(new BoxLayout(comboBoxPanel, BoxLayout.LINE_AXIS));
		comboBoxPanel.setMaximumSize(new Dimension(panelWidth,36));
		comboBoxPanel.setMinimumSize(new Dimension(panelWidth,36));
		comboBoxPanel.setPreferredSize(new Dimension(panelWidth,36));
		JPanel comboLabelPanel=new JPanel();
		comboLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
		comboLabel = new JLabel("SELECT GRID DIMENSION:");
		comboLabel.setFont(Fonts.b14);
		comboLabelPanel.add(comboLabel);
		comboBoxPanel.add(Box.createRigidArea(new Dimension(20,0)));
		comboBoxPanel.add(comboLabel);
		comboBoxPanel.add(Box.createRigidArea(new Dimension(20,0)));
		comboBoxPanel.add(Box.createRigidArea(new Dimension(0,5)));
		
		gridComboBox = new JComboBox<String>();
		gridComboBox.setMaximumSize(new Dimension(80,25));
		gridComboBox.setPreferredSize(new Dimension(80,25));
		gridComboBox.setMinimumSize(new Dimension(80,25));
		gridComboBox.setMaximumRowCount(20);
		gridComboBox.setBackground(Color_schema.dark_20);
		gridComboBox.setForeground(Color_schema.white_230);
		gridComboBox.setFont(Fonts.b14);

		//go trough all gridsizes and add to Combobox
		Iterator<SingleGridSize> sgIterator = this.gridSizes.iterator();
		while(sgIterator.hasNext()){
			SingleGridSize sgs= sgIterator.next();
			String size= ""+sgs.getRows() + " x "+sgs.getColumns();
			gridComboBox.addItem(size);

		}
		if(gridComboBox.getItemCount() >0){
			//GridProperties gp = getFirstGridPropertiesWithGridON();
			
			if(this.templateGP != null){

				int index= getIndexOfGridSizeList(this.templateGP.getGridRowCount(), this.templateGP.getGridColumnCount());
				if(index >0)
					gridComboBox.setSelectedIndex(index);
				else
					gridComboBox.setSelectedIndex(0);
			}
			else{
				gridComboBox.setSelectedIndex(0);
			}
		}

		gridComboBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
			//	comboSelectedIndex=((JComboBox<String>)e.getSource()).getSelectedIndex();
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						updateGridDimensionFromComboBox(ID.GPANEL_GRID_SIZE_CHANGED);

					}
				});


			}
		});
		
		comboBoxPanel.add(gridComboBox);
		return comboBoxPanel;
		
		}catch(Exception e){
			LOGGER.severe("unable to create combobox for grid sizes "+e.getMessage());
			return new JPanel();
		}


	}
	
	/**
	 * Creates JPanel, where is JSlider for selecting for percent value of selected cells in grid.
	 * @return JPanel containing JSlider for percent value of selected cells in grid
	 */
	private JPanel setupSliderPanel(){
		
		JPanel percentSliderPanel=new JPanel();
		percentSliderPanel.setLayout(new BoxLayout(percentSliderPanel, BoxLayout.LINE_AXIS));
		percentSliderPanel.setMaximumSize(new Dimension(panelWidth,36));
		percentSliderPanel.setMinimumSize(new Dimension(panelWidth,36));
		percentSliderPanel.setPreferredSize(new Dimension(panelWidth,36));
		JPanel pSliderLabelPanel=new JPanel();
		pSliderLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
		sliderLabel = new JLabel("RANDOM FILL %:");
		sliderLabel.setFont(Fonts.b14);
		pSliderLabelPanel.add(sliderLabel);
		percentSliderPanel.add(Box.createRigidArea(new Dimension(10,0)));
		percentSliderPanel.add(pSliderLabelPanel);
		percentSliderPanel.add(Box.createRigidArea(new Dimension(10,0)));
		percentSliderPanel.add(Box.createRigidArea(new Dimension(0,5)));
		//setup randow Slider
		randomSlider=new JSlider(10, 100,50);
		

		randomSlider.setMinimumSize(new Dimension(200,30));
		randomSlider.setPreferredSize(new Dimension(200,30));
		randomSlider.setMaximumSize(new Dimension(200,30));
		
		randomSlider.setMajorTickSpacing(20);
		randomSlider.setMinorTickSpacing(5);
		randomSlider.setPaintTicks(true);
		randomSlider.setSnapToTicks(true);
		
		randomSlider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						sliderValueLabel.setText(""+randomSlider.getValue()+" %");
						//update grid, because amount of selected cells is changed
						updateGridDimensionFromComboBox(ID.GPANEL_RANDOM_PROCENT_CHANGED);

					}
				});
				
			}
		});
		
		if(this.templateGP != null && this.templateGP.getRandomProcent() >0)
			randomSlider.setValue(this.templateGP.getRandomProcent());
		
		percentSliderPanel.add(randomSlider);
		
		JPanel pSliderValueLabelPanel=new JPanel();
		pSliderValueLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 2));
		sliderValueLabel = new JLabel(""+this.randomSlider.getValue()+" %");
		sliderValueLabel.setFont(Fonts.b18);
		
		pSliderValueLabelPanel.add(sliderValueLabel);
		percentSliderPanel.add(pSliderValueLabelPanel);
		
		return percentSliderPanel;
		
	}

	/**
	 *  Setups the Preview Grid at startup: visible or invisible.
	 *  
	 */
	private void enableComponents(){

			this.gridComboBox.setEnabled(gridON);
			if(gridON){
				this.gridPanel.setVisible(true);
			//	if(this.gridPanel.getComponentCount()==0){
					updateGridDimensionFromComboBox(ID.GPANEL_STARTUP);
			//	}
				this.gridPanel.setBackground(Color_schema.grey_150);
				this.gridLabel.setForeground(Color_schema.white_230);
				this.comboLabel.setForeground(Color_schema.white_230);
				this.sliderLabel.setForeground(Color_schema.white_230);
				this.sliderValueLabel.setForeground(Color_schema.white_230);
				this.randomSlider.setEnabled(true);
				this.randomSlider.setForeground(Color_schema.white_230);
				this.randomSlider.putClientProperty("JSlider.isFilled", Boolean.TRUE);

			}
			else{
				this.gridPanel.setVisible(false);
				this.gridPanel.setBackground(Color_schema.dark_40);
				this.gridLabel.setForeground(Color_schema.grey_100);
				this.comboLabel.setForeground(Color_schema.grey_100);
				this.sliderLabel.setForeground(Color_schema.grey_100);
				this.sliderValueLabel.setForeground(Color_schema.grey_100);
				this.randomSlider.setEnabled(false);
				this.randomSlider.setForeground(Color_schema.dark_40);
				this.randomSlider.putClientProperty("JSlider.isFilled", Boolean.FALSE);
				
				
				
				
			}
			this.backGridExamplePanel.repaint();
		//	updateGridDimensionFromComboBox();
	}

	/**
	 * Set ups the Grid example JPanel.
	 * @return JPanel the Grid example panel.
	 */
	private JPanel setUpGridExamplePanel(){
		backGridExamplePanel = new JPanel();
		backGridExamplePanel.setLayout(new BoxLayout(backGridExamplePanel, BoxLayout.PAGE_AXIS));
		backGridExamplePanel.setMaximumSize(new Dimension(panelWidth,320));
		backGridExamplePanel.setMinimumSize(new Dimension(panelWidth,320));
		backGridExamplePanel.setPreferredSize(new Dimension(panelWidth,320));
		backGridExamplePanel.setBorder(BorderFactory.createLineBorder(Color_schema.grey_100, 1));

		JPanel gridTitlePanel=new JPanel();
		gridTitlePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 5));
		gridTitlePanel.setMaximumSize(new Dimension(panelWidth,30));
		gridTitlePanel.setPreferredSize(new Dimension(panelWidth,30));
		gridTitlePanel.setMinimumSize(new Dimension(panelWidth,30));

		gridLabel = new JLabel("SELECT ACTIVE GRID CELLS:");
		gridLabel.setFont(Fonts.b14);
		gridTitlePanel.add(gridLabel);
		backGridExamplePanel.add(gridTitlePanel);

		gridPanel = new JPanel();
		gridPanel.setLayout(new GridLayout(2, 2,1,1));
		gridPanel.setMaximumSize(new Dimension(panelWidth,280));
		gridPanel.setMinimumSize(new Dimension(panelWidth,280));
		gridPanel.setPreferredSize(new Dimension(panelWidth,280));
		gridPanel.setBackground(Color_schema.grey_150);
	//	updateGridDimensionFromComboBox();
	//	initUnSelectedGridCells();
		backGridExamplePanel.add(gridPanel);

		enableComponents();

		return backGridExamplePanel;

	}

	
	/**
	 * Selects randomly which grid cells are selected.
	 * @param r int rows of grid
	 * @param c int columns of grid
	 */
	private void setRandomGridShown(int r, int c){
		int rows = r;
		int columns = c;
		
		unselectAllCells(rows, columns); // set all cells unselected
		int cellsCount = rows*columns;
		double value=((double)this.randomSlider.getValue())/100.0;
		double selectedCells= Math.ceil(((double)cellsCount)*value); // round selected number to next upper integer value
		int partOfCellsCount= (int)selectedCells;
	//	int partOfCellsCount = cellsCount*(this.randomSlider.getValue()/100);
		if(partOfCellsCount==0)
			partOfCellsCount=1;
		
			for(int i=0;i<partOfCellsCount;i++){
				// check that unselected grid rectangles number is smaller than selected ones.
				if(countSelectedGridRectangle(rows, columns)<partOfCellsCount){
					GridRectangle gr = getRandomGridRectangle(rows, columns);
					if(gr != null){
						gr.setShown(true);
						gr.updatePanel();
					}
				}
			}
		
	}
	
	
	private void unselectAllCells(int rowCount,int columnCount){
		for(int r=1;r<=rowCount;r++){
			for(int c=1;c<=columnCount;c++){
				GridRectangle gr= getGridRectangle(r, c);
				gr.setShown(false);	
				gr.updatePanel();
			}		
		}
	}
	
	private GridRectangle getRandomGridRectangle(int rowCount, int columnCount){
		if(countUnselectedGridRectangle(rowCount, columnCount)>0){
		
			Random rand = new Random();
			int randomRow = rand.nextInt((rowCount - 1) + 1) + 1;
			int randomColumn = rand.nextInt((columnCount - 1) + 1) + 1;
			GridRectangle gr= getGridRectangle(randomRow, randomColumn);
			if(gr.isShown())
				return getRandomGridRectangle(rowCount, columnCount);
			else
				return gr;
		}
		return null;
		
		
	}
	
	private int countUnselectedGridRectangle(int rowCount, int columnCount){
		int count_unselected=0;
		
		for(int r=1;r<=rowCount;r++){
			for(int c=1;c<=columnCount;c++){
				GridRectangle gr= getGridRectangle(r, c);
				if(!gr.isShown()){
					count_unselected++;
				}
			}		
		}
		return count_unselected;
		
	}
	
	private int countSelectedGridRectangle(int rowCount, int columnCount){
		int count_selected=0;
		
		for(int r=1;r<=rowCount;r++){
			for(int c=1;c<=columnCount;c++){
				GridRectangle gr= getGridRectangle(r, c);
				if(gr.isShown()){
					count_selected++;
				}
			}		
		}
		return count_selected;
		
	}
	
	/** 
	 * Returns first Gridproperties of any markinglayers. 
	 * First searching visible Gridproperties from MarkingLayers, which Gridproperties are modified at same time.
	 * Then searching unvisible Gridproperties from MarkingLayers, which Gridproperties are modified at same time.
	 * Then searching visible Gridproperties from MarkingLayers under same Imagelayer.
	 * Then searching unvisible Gridproperties under same Imagelayer.
	 * Then searching visible Gridproperties from MarkingLayers under other Imagelayers.
	 * Then searching unvisible Gridproperties under same Imagelayer.
	 * Otherwise any Gridproperties is returned if found. If no any Gridproperties is not found, null is returned.
	 * @return Gridproperties
	 * @see GridProperties
	 */
	/**
	 * @return
	 */
	private GridProperties getFirstGridPropertiesFromAllMarkingLayers(int r, int c){
		boolean checkRandomProcent=false;
		if(r>0 && c>0)
			checkRandomProcent=true;
		
		GridProperties returnGP=null;
		
			returnGP= getFirstGridProperties(r,c, true, checkRandomProcent); // Just visible GridProperties from Markinglayers that are modified at the same time. 
			
			if(returnGP == null)
				returnGP = getFirstGridProperties(r,c, false, checkRandomProcent); // Unvisible GridProperties from Markinglayers that are modified at the same time. 
			if(returnGP == null){
				// Get GridProperties from markingLayers that are under same ImageLayer (not needed to be modified)
				// preferable visible, but returns also unvisible GridProperties
				returnGP= getGPFromMarkingLayerList(this.markingLayerList, r, c, checkRandomProcent); 
				if(returnGP == null){
					returnGP= getGPFromMarkingLayerList(this.gui.getAllMarkingLayers(), r,c,checkRandomProcent); 
					
				}
				
			}
		
		
		return returnGP;
	}
	
	/**
	 * Tries to Find GridProperties from all given MarkingLayers and markingLayers that are under same ImageLayer.
	 * Returns first GridProperties, that is found. Preferable visible @see GridProperties, but if not found, then first unvisible GridProperties is returned.
	 * Return null if not any Gridproperties are found.
	 * @param mLayerList list of MarkingLayers, which GridProperties are viewed.
	 * @param r integer for rows in grid
	 * @param c integer for columns in grid
	 * @return GridProperties
	 */
	private GridProperties getGPFromMarkingLayerList(ArrayList<MarkingLayer> mLayerList, int r, int c, boolean checkProcent){
		try{
	
		if(mLayerList != null){
			Iterator<MarkingLayer> miterator=mLayerList.iterator();
			while(miterator.hasNext()){
				MarkingLayer mlayer= miterator.next();
				
				ImageLayer iLayer = gui.getImageLayerByMarkingLayerID(mlayer.getLayerID());
				if(iLayer != null){
					ArrayList<MarkingLayer> mlayerlistOFSingleILayer = iLayer.getMarkingLayers();
					if(mlayerlistOFSingleILayer != null && mlayerlistOFSingleILayer.size()>0){
						Iterator<MarkingLayer> mSingleILiterator=mLayerList.iterator();
						while(mSingleILiterator.hasNext()){ // go through all MarkingLayers under iLayer				
							GridProperties gpSingle = mSingleILiterator.next().getGridProperties();
							// c and r is zero when initializing panel. And they are something else, when user selects row and column in combobox.
							if(gpSingle != null && gpSingle .isGridON() && ( (c==0 && r==0) || (gpSingle.getGridColumnCount() == c && gpSingle.getGridRowCount() == r) ) )
								if(!checkProcent || checkProcent && gpSingle.getRandomProcent()== this.randomSlider.getValue()) // check that percentSlider value is same
								return gpSingle; // found Gridproperties that is visible (ON) and under same ImageLayer
						}
						// try to find unvisible Gridproperties here, because not found any visible ones.
						Iterator<MarkingLayer> mSingleILiteratorUnvisible=mLayerList.iterator();
						while(mSingleILiteratorUnvisible.hasNext()){ // go through all MarkingLayers under iLayer						
							GridProperties gpSingle = mSingleILiteratorUnvisible.next().getGridProperties();
							// c and r is zero when initializing panel. And they are something else, when user selects row and column in combobox.
							if(gpSingle != null && ( (c==0 && r==0) || (gpSingle.getGridColumnCount() == c && gpSingle.getGridRowCount() == r) ))
								if(!checkProcent || checkProcent && gpSingle.getRandomProcent()== this.randomSlider.getValue()) // check that percentSlider value is same								
								return gpSingle; // found Gridproperties that is under same ImageLayer
						}					
					}
				}	
			}
		}
			return null;
		}
		catch (Exception e) {
			LOGGER.severe("Error in finding GridProperties from MarkingLayers:  " +e.getClass().toString() + " :" +e.getMessage());
			return null;
		}
	}


	/** 
	 * Returns first Gridproperties, which is set to be visible and found from markinglayer under same Imagelayer. 
	 * Otherwise any Gridproperties is returned if found. If no any Gridproperties is not found, null is returned.
	 * @return Gridproperties
	 * @see GridProperties
	 */
/*	private GridProperties getFirstGridPropertiesWithGridON(int r, int c){
		if(this.markingLayerList != null && this.markingLayerList.size()>0){
			MarkingLayer layer = getFirstMarkingLayerWithGridON();
			if(layer != null){
				GridProperties gp=layer.getGridProperties();
				if( gp != null && r > 0 && c > 0){ // specific column and row number			
					if(gp.getGridRowCount()==r && gp.getGridColumnCount()==c){
						return gp;
					}
				}
				
			}
		}

		return null;
	}
*/
	/**
	 * Finds any GridProperties, that are found from given MarkingLayer-list (modified at same time as Gridproperties of this MarkingLayer)
	 * Returns found GridProperties, otherwise null is returned.
	 * 
	 * @return GridProperties
	 */
	private GridProperties getFirstGridProperties(int r, int c, boolean findON, boolean checkProcent){
		if(this.markingLayerList != null && this.markingLayerList.size()>0){
			Iterator<MarkingLayer> miterator=this.markingLayerList.iterator();
			while(miterator.hasNext()){
				MarkingLayer layer= miterator.next();
				if(layer.getGridProperties() != null)
				{	
						GridProperties gp=layer.getGridProperties();
						//if r and c >0 has gp include same values of columns and rows.
						if(gp != null && ((c==0 && r==0) || (gp.getGridColumnCount()==c && gp.getGridRowCount()==r) ) ){
							if( (findON && gp.isGridON() ) || !findON) // if searching GRID which is ON (visible) then check that it is ON.
								if(!checkProcent || checkProcent && gp.getRandomProcent()== this.randomSlider.getValue()) // check that percentSlider value is same
									return gp;
						}
				}						
			}
		}
		return null;
	}



	private MarkingLayer getFirstMarkingLayerWithGridON(){
		if(this.markingLayerList != null && this.markingLayerList.size()>0){
		Iterator<MarkingLayer> miterator=this.markingLayerList.iterator();
		while(miterator.hasNext()){
			MarkingLayer layer= miterator.next();
			if(layer.getGridProperties() != null && layer.getGridProperties().isGridON())
				return layer;
		}
		}
		return null;
	}


	private boolean isAnyGridON(){
		if(getFirstMarkingLayerWithGridON() != null){
			return true;
		}
		return false;
	}


/*
	private void updateGridDimensionFromLayer(){
		GridProperties gp= getFirstGridPropertiesWithGridON();
		if(gp != null){
			this.gridPanel.setLayout(new GridLayout(gp.getGridRowCount(), gp.getGridColumnCount(), 10, 10));


		}

		this.gridPanel.repaint();
	}
	*/

	private int getIndexOfGridSizeList(int row, int column){
		if(this.gridSizes != null && gridSizes.size()>0){
			for(int i=0; i<this.gridSizes.size();i++){

				SingleGridSize sgs = this.gridSizes.get(i);
				if(sgs.getRows() == row && sgs.getColumns() == column){
					return i;
				}

			}

		}
		return -1;

	}
	
	private int[] getRowAndColumnFromListByIndex(){
		if(this.gridSizes != null && gridSizes.size()>0){
			SingleGridSize sgs = this.gridSizes.get(this.gridComboBox.getSelectedIndex());
			if(sgs != null){
				return new int[] {sgs.getRows(),sgs.getColumns()};
				
			}
		}
		return null;
	}
	
	private void updateGridDimensionFromComboBox(int updateType){
		int index=0;
		SingleGridSize sgs=null;
		switch(updateType){
			case ID.GPANEL_STARTUP:
				if(this.templateGP != null)
					index = getIndexOfGridSizeList(this.templateGP.getGridRowCount(), this.templateGP.getGridColumnCount());
				break;
			case ID.GPANEL_GRID_SIZE_CHANGED:
				 // user pressed new index of combobox
				index = this.gridComboBox.getSelectedIndex();
				// get new template GP, because the column and row are changed
				if(this.gridSizes != null && this.gridSizes.size()>index){
					sgs= this.gridSizes.get(index);
					if(sgs != null){
						this.templateGP = getFirstGridPropertiesFromAllMarkingLayers(sgs.getRows(), sgs.getColumns());
					}
				}
				break;
				
			case ID.GPANEL_RANDOM_PROCENT_CHANGED:
				index = this.gridComboBox.getSelectedIndex();
				if(this.gridSizes != null && this.gridSizes.size()>index){
					sgs= this.gridSizes.get(index);
					if(sgs != null){
						this.templateGP = getFirstGridPropertiesFromAllMarkingLayers(sgs.getRows(), sgs.getColumns());
					}
				}
				break;
			
		
		}
		
		if(this.gridSizes != null && this.gridSizes.size()>index){
			if(sgs==null)
				sgs= this.gridSizes.get(index);
			if(sgs != null){				
			//	if(((GridLayout)this.gridPanel.getLayout()).getRows() != sgs.getRows() || ((GridLayout)this.gridPanel.getLayout()).getColumns() != sgs.getColumns() ||this.gridPanel.getComponentCount() ==0){					
					updateGridSize(sgs.getRows(), sgs.getColumns());
			//	}

			}
			
		}

		//this.gridPanel.repaint();
	}
	
	/**
	 * Returns SingleGridSize object by given index.
	 * @param index index of gridSizes-list (list of combobox items)
	 * @return SingleGridSize object that contains column and row, etc.
	 */
	private SingleGridSize getGridSizeByIndex(int index){
		SingleGridSize sgs=null;
		sgs= this.gridSizes.get(index);
		return sgs;
	}

	private void updateGridSize(int r, int c){
		this.gridPanel.removeAll();
		this.gridPanel.revalidate();
		boolean usedTemplateGridProperties = false;
	//	if(gridON){
			this.gridPanel.setLayout(new GridLayout(r, c, 1, 1));
			this.gridPanel.revalidate();
		//	int maxSize=Math.min((int)((gridPanel.getPreferredSize().getWidth()-((c-1)*10))/(double)c), (int)((gridPanel.getPreferredSize().getHeight()-((r-1)*10))/(double)r));
			int maxSize=Math.min((int)(backGridExamplePanel.getPreferredSize().getWidth()/(double)c), (int)((backGridExamplePanel.getPreferredSize().getHeight()-35)/(double)r));
			int maxGridPanelWidth= maxSize*c;
			int maxGridPanelHeight= maxSize*r;
			this.gridPanel.setMaximumSize(new Dimension(maxGridPanelWidth,maxGridPanelHeight));
			this.gridPanel.setMinimumSize(new Dimension(maxGridPanelWidth,maxGridPanelHeight));
			this.gridPanel.setPreferredSize(new Dimension(maxGridPanelWidth,maxGridPanelHeight));
		//	GridProperties firstGP=getFirstGridPropertiesWithGridON();
			
			for(int i=1;i<= r;i++){
				for(int j=1;j<= c;j++){
					if(this.templateGP != null && this.templateGP.getGridColumnCount()==c && this.templateGP.getGridRowCount()==r){
						boolean showGR=this.templateGP.isSelectedGridCellAt(i, j);
						gridPanel.add(new GridRectangle(i, j, showGR));
						
						usedTemplateGridProperties=true;
					}
					else
						gridPanel.add(new GridRectangle(i, j, false));
				}
			}
			if(!usedTemplateGridProperties) // no any present properties found -> create random selections
				setRandomGridShown(r,c);
	//	}
		backGridExamplePanel.repaint();

	}
	
	
/*
	private void updateGridDimensionFromComboBox(){
		int index = this.gridComboBox.getSelectedIndex();
		if(this.gridSizes != null && this.gridSizes.size()>index){
			SingleGridSize sgs= this.gridSizes.get(index);
			if(sgs != null){
				if(((GridLayout)this.gridPanel.getLayout()).getRows() != sgs.getRows() ||
						((GridLayout)this.gridPanel.getLayout()).getColumns() != sgs.getColumns() ||
						this.gridPanel.getComponentCount() ==0)
				updateGridSize(sgs.getRows(), sgs.getColumns());

			}
		}

		//this.gridPanel.repaint();
	}

	private void updateGridSize(int r, int c){
		this.gridPanel.removeAll();
		this.gridPanel.revalidate();
		boolean usedPresentGridProperties = false;
	//	if(gridON){
			this.gridPanel.setLayout(new GridLayout(r, c, 1, 1));
			this.gridPanel.revalidate();
		//	int maxSize=Math.min((int)((gridPanel.getPreferredSize().getWidth()-((c-1)*10))/(double)c), (int)((gridPanel.getPreferredSize().getHeight()-((r-1)*10))/(double)r));
			int maxSize=Math.min((int)(backGridExamplePanel.getPreferredSize().getWidth()/(double)c), (int)((backGridExamplePanel.getPreferredSize().getHeight()-35)/(double)r));
			int maxGridPanelWidth= maxSize*c;
			int maxGridPanelHeight= maxSize*r;
			this.gridPanel.setMaximumSize(new Dimension(maxGridPanelWidth,maxGridPanelHeight));
			this.gridPanel.setMinimumSize(new Dimension(maxGridPanelWidth,maxGridPanelHeight));
			this.gridPanel.setPreferredSize(new Dimension(maxGridPanelWidth,maxGridPanelHeight));
		//	GridProperties firstGP=getFirstGridPropertiesWithGridON();
			GridProperties firstGP = getFirstGridPropertiesFromAllMarkingLayers(r,c);
			for(int i=1;i<= r;i++){
				for(int j=1;j<= c;j++){
					if(firstGP != null && firstGP.getGridColumnCount()==c && firstGP.getGridRowCount()==r){
						boolean showGR=firstGP.isSelectedGridCellAt(r, c);
						gridPanel.add(new GridRectangle(i, j, showGR));
						usedPresentGridProperties=true;
					}
					else
						gridPanel.add(new GridRectangle(i, j, false));
				}
			}
			if(!usedPresentGridProperties) // no any present properties found -> create random selections
				setRandomGridShown(r,c);
	//	}
		backGridExamplePanel.repaint();

	}
*/
	private void updateOnOffButtonIcons(){
		try {
			if(gridON){
				on_button.setIcon((gui.getImageIcon("/images/on_selected.png")));
				off_button.setIcon((gui.getImageIcon("/images/off_unselected.png")));
			}
			else{
				on_button.setIcon((gui.getImageIcon("/images/on_unselected.png")));
				off_button.setIcon((gui.getImageIcon("/images/off_selected.png")));
			}
			on_button.revalidate();
			off_button.revalidate();
			on_button.repaint();
			off_button.repaint();

		//	LOGGER.fine("grid on: "+gridON);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void hideDialog(boolean saveChanges){
		if(saveChanges){

			// create gridProperty
			GridProperties gridProperty=new GridProperties(this.gui.getPresentImageDimension());
			gridProperty.setGridON(this.gridON);
			gridProperty.setRandomProcent(this.randomSlider.getValue()); // set random procent value
			SingleGridSize sgs = this.gridSizes.get(this.gridComboBox.getSelectedIndex());
			int x=sgs.getWidthAlign();
			int y=sgs.getHeightAlign();
			// add vertical lines and rectangles
			for(int r=1;r <= sgs.getRows();r++){
				gridProperty.addRowLineY(y);
				x=sgs.getWidthAlign(); // start new row at left
				for(int c=1; c<= sgs.getColumns();c++){
				//	if(r==1) // add only once
				//	gridProperty.addColumnLineX(x);
				//	if(r< sgs.getRows() && c<sgs.getColumns()){ // in last row and column no rectangles created
						GridRectangle gr = getGridRectangle(r, c);
						if(gr != null){
							/*
							Rectangle rec = new Rectangle(x, y, sgs.getGridCellSize(), sgs.getGridCellSize());
							if(gr.isShown()){
								gridProperty.addSelectedRectangle(rec);
							}
							else{
								gridProperty.addUnselectedRectangle(rec);
								gridProperty.addUnselectedGridCellNumbers((r-1)*sgs.getColumns()+c);
							}
							*/
							PositionedRectangle pRec = new PositionedRectangle(x, y, sgs.getGridCellSize(), sgs.getGridCellSize(), r,c, gr.isShown());
							gridProperty.addSinglePositionedRectangle(pRec);
						}
				//	}
					x+=sgs.getGridCellSize();
				}
				y+=sgs.getGridCellSize();
			}

			gridProperty.addRowLineY(y); // rightmost line

			// add horizontal lines
			x=sgs.getWidthAlign();
			for(int c=1; c<= sgs.getColumns();c++){
				gridProperty.addColumnLineX(x);
				x+=sgs.getGridCellSize();
			}
			gridProperty.addColumnLineX(x); // lowest line

			// save the selected gridproperties to markinglayers
			Iterator<MarkingLayer> mIterator = this.markingLayerList.iterator();
			while(mIterator.hasNext()){
				MarkingLayer mlayer= mIterator.next();
				mlayer.setGridProperties(gridProperty);
			}

		}
		this.setVisible(false);
		dispose();
	}

	private GridRectangle getGridRectangle(int r, int c){
		if(this.gridPanel != null && this.gridPanel.getComponentCount()>0){

			Component[] grs= gridPanel.getComponents();

			for(int i=0; i<grs.length;i++){

				GridRectangle gr =(GridRectangle)grs[i];
				if(gr.getRow()==r && gr.getColumn()==c){
					return gr;
				}

			}
		}
		return null;
	}


}
