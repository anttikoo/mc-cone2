package gui.saving.image;

import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import operators.CheckBoxIcon;
import gui.Color_schema;
import gui.saving.SingleMarkingPanel;
import information.Fonts;
import information.MarkingLayer;

/**
 * The Class ExImaSingleMarkingPanel. Extends SingleMarkingPanel and shows selectors for MarkingLayer to be drawn on image. 
 */
public class ExImaSingleMarkingPanel extends SingleMarkingPanel{

	private JCheckBox drawGridCheckBox;

	/**
	 * Instantiates a new Panel.
	 *
	 * @param mlayer the MarkingLayer
	 */
	public ExImaSingleMarkingPanel(MarkingLayer mlayer){
		super(mlayer);
	}

	protected JPanel initDrawGridCheckBoxPanel(){
		drawGridCheckBox=null;
		if(this.mLayer.getGridProperties() != null && this.mLayer.getGridProperties().isGridON()){
		JPanel drawGridPanel=new JPanel();
		drawGridPanel.setMaximumSize(new Dimension(110,40));
		drawGridPanel.setPreferredSize(new Dimension(110,40));
		drawGridPanel.setMinimumSize(new Dimension(110,40));
		drawGridPanel.setLayout(new BoxLayout(drawGridPanel, BoxLayout.LINE_AXIS));
		drawGridCheckBox = new JCheckBox(new CheckBoxIcon());
		drawGridCheckBox.setSelected(false);
		drawGridCheckBox.setBackground(Color_schema.dark_40);
		drawGridCheckBox.setMaximumSize(new Dimension(20,20));
		drawGridCheckBox.setPreferredSize(new Dimension(20,20));
		drawGridCheckBox.setMinimumSize(new Dimension(20,20));
		drawGridCheckBox.setMargin(new Insets(0, 0, 0, 0));

		// marking title
		JLabel drawGridLabel = new JLabel("Draw Grid");
		drawGridLabel.setFont(Fonts.p15);
		drawGridLabel.setForeground(Color_schema.white_180);
		drawGridPanel.add(Box.createRigidArea(new Dimension(5,0)));
		drawGridPanel.add(drawGridCheckBox);
		drawGridPanel.add(Box.createRigidArea(new Dimension(5,0)));
		drawGridPanel.add(drawGridLabel);
		drawGridPanel.add(Box.createRigidArea(new Dimension(5,0)));

		return drawGridPanel;
		}
		else
			return null;
	}

	/**
	 * Checks if CheckBox selected for drawing MarkingLayer on image.
	 *
	 * @return true, if CheckBox is selected
	 */
	protected boolean isDrawCheckBoxSelected(){
		if(this.drawGridCheckBox != null){
			return this.drawGridCheckBox.isSelected();
		}
		return false;
	}


}
