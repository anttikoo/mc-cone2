package gui;

import information.Fonts;
import information.ID;
import information.MarkingLayer;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import managers.TaskManager;

public class GlobalMarkingProperties extends MarkingProperties{
//private final static Logger LOGGER = Logger.getLogger("MCCLogger");
//protected int panelHeight=300;
//protected ArrayList<MarkingLayer> markingLayerList;

	public GlobalMarkingProperties(JFrame frame, GUI gui, Point point, ArrayList<MarkingLayer> markingLayerList) {
		super(frame, gui, point, markingLayerList);
		//this.markingLayerList= markingLayerList;
		// TODO Auto-generated constructor stub
	}
	protected void initMarkingPropertiesPanel(){

		if(this.markingLayerList != null && this.markingLayerList.size()>0){
			MarkingLayer mLayer = this.markingLayerList.get(0);
			if(mLayer != null){

				this.setSelectedColor(mLayer.getColor());
				this.setSelectedSize(mLayer.getSize());
				this.setSelectedThickness(mLayer.getThickness());
				this.setSelectedOpacity(changeUnderZeroFloatToInt(mLayer.getOpacity()));
				this.setSelectedShapeID(mLayer.getShapeID());
			}
			else{
				gui.showMessage("Could not set ImageLayer Properties", "Not any MarkingLayers found. Not changed properties.",ID.OK);
				hideDialog(false);
			}
		}
		else{

			gui.showMessage("Could not set ImageLayer Properties", "Not any MarkingLayers found. Not changed properties.",ID.OK);
			hideDialog(false);
		}
		this.panelHeight=300;
		initDialog();
		this.revalidate();
		this.repaint();
	}




	protected JPanel initUPPanels(){
		// contains title and colorchooser panels
					JPanel upperBackPanel= new JPanel();
					upperBackPanel.setLayout(new BoxLayout(upperBackPanel, BoxLayout.PAGE_AXIS));
					upperBackPanel.setMaximumSize(new Dimension(panelWidth,60));
					upperBackPanel.setMinimumSize(new Dimension(panelWidth,60));
					upperBackPanel.setPreferredSize(new Dimension(panelWidth,60));
					upperBackPanel.add(initTitlePanel("EDIT MARKING PROPERTIES"));

					JPanel infoPanel=new JPanel();
					infoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
					infoPanel.setMaximumSize(new Dimension(panelWidth,30));
					infoPanel.setMinimumSize(new Dimension(panelWidth,30));
					infoPanel.setPreferredSize(new Dimension(panelWidth,30));

					JLabel info = new JLabel("Sets properties of all MarkingLayers");
					info.setFont(Fonts.b14);
					infoPanel.add(info);

					upperBackPanel.add(infoPanel);
					return upperBackPanel;
	}

	protected void setUpComboBoXPanel(){
		comboBoxPanel=null;
	}

	/**
	 * Hides Dialog window and saves the changes made to MarkingLayer.
	 * @param saveChanges boolean value should the changes be saved to MarkingLayer
	 */
	protected void hideDialog(boolean saveChanges){
		//LOGGER.fine("color selected: " +colorChooser.getColor().toString());
		if(this.markingLayerList != null && this.markingLayerList.size()>0 && saveChanges){
				try {
					Iterator<MarkingLayer> mIterator = this.markingLayerList.iterator();
					while(mIterator.hasNext()){
	
						saveChanges(mIterator.next());
					}
	
				} catch (Exception e) {
					LOGGER.severe("Error in saving marking properties to MarkingLayer " +e.getClass().toString() + " :" +e.getMessage());
	
				}
				// update the GUI ImageLayerInfo JPanel
				gui.updateAllMarkingPanelProperties();
			//	gui.updateImageLayerInfos();
				gui.setMadeChanges(true);
			}
		this.setVisible(false);
		dispose();
	}
	
	protected void saveChanges(MarkingLayer mLayer){
		// save changes to markinglayer
		// set color from colorchooser
	//	mLayer.setColor(this.getSelectedColor());
		// set shape id
	//	mLayer.setShapeID(this.getSelectedShapeID());
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

}
