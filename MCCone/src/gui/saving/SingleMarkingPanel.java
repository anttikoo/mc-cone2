package gui.saving;


import gui.Color_schema;
import information.Fonts;
import information.MarkingLayer;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import operators.CheckBoxIcon;

/**
 * The Class SingleMarkingPanel. Contains name of MarkingLayer, checkboxes to select it and grid.
 */
public class SingleMarkingPanel extends JPanel{

		private JCheckBox saveCheckBox;
		protected MarkingLayer mLayer;
		private JPanel markingLabelPanel;
		private JPanel gridDrawingSelection;



		/**
		 * Instantiates a new single marking panel.
		 *
		 * @param mLayer the MarkingLayer
		 */
		public SingleMarkingPanel(MarkingLayer mLayer){
			this.mLayer=mLayer;

			try {
				this.setOpaque(true);
				this.setMaximumSize(new Dimension(2000,40));
				this.setPreferredSize(new Dimension(400,40));
				this.setMinimumSize(new Dimension(200,40));
				this.setBackground(Color_schema.dark_35);
				this.setBorder(BorderFactory.createLineBorder(Color_schema.dark_50, 1));
				this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
				Icon checkBoxIcon=new CheckBoxIcon();
				// checkBox for selecting
				saveCheckBox=new JCheckBox(checkBoxIcon);		
				saveCheckBox.setSelected(true);
				saveCheckBox.setBackground(Color_schema.dark_35);
				saveCheckBox.setMaximumSize(new Dimension(25,25));
				saveCheckBox.setPreferredSize(new Dimension(25,25));
				saveCheckBox.setMinimumSize(new Dimension(25,25));

				// marking title
				JLabel markingLabel = new JLabel(getMarkingName());		
				markingLabelPanel = new JPanel();
				markingLabelPanel.setLayout(new BorderLayout());
				markingLabelPanel.setMaximumSize(new Dimension(2000, 40));
				markingLabel.setFont(Fonts.b15);
				markingLabel.setForeground(Color_schema.white_230);
				int markinglabelwidth=markingLabel.getFontMetrics(Fonts.b15).stringWidth(markingLabel.getText());

				markingLabelPanel.setPreferredSize(new Dimension(markinglabelwidth+50,40));
				markingLabelPanel.add(markingLabel);
				markingLabelPanel.setBackground(Color_schema.dark_35);
				gridDrawingSelection = initDrawGridCheckBoxPanel();

				this.add(Box.createRigidArea(new Dimension(30,0)));
				this.add(saveCheckBox);
				this.add(Box.createRigidArea(new Dimension(5,0)));
				this.add(markingLabelPanel);
				if(gridDrawingSelection != null){
					this.add(Box.createHorizontalGlue());
					this.add(gridDrawingSelection);
				}
			
			} catch (Exception e) {

				e.printStackTrace();
		}

		}

		/**
		 * Initializes the JPanel for Grid CheckBox.
		 *
		 * @return the JPanel
		 */
		protected JPanel initDrawGridCheckBoxPanel(){
			return null;
		}

			public boolean isSelected(){
				return this.saveCheckBox.isSelected();
			}

			public void setSelected(boolean selected){
				this.saveCheckBox.setSelected(selected);
				this.repaint();
			}
			
			public void setCheckBoxEnableState(boolean state){			
				this.saveCheckBox.setEnabled(state);
				this.repaint();
			}

			public int getMarkingLayerID(){
				return this.mLayer.getLayerID();
			}



			public String getMarkingName() {
				return this.mLayer.getLayerName();
			}

			public MarkingLayer getMarkingLayer() {
				return mLayer;
			}



			public void setBGColorBySuccessfullSaving(boolean savedSuccessfully){
				if(savedSuccessfully){
					this.setBackground(Color_schema.darkest_green);
					this.saveCheckBox.setBackground(Color_schema.darkest_green);
					this.markingLabelPanel.setBackground(Color_schema.darkest_green);
					

				}
				else{
				this.setBackground(Color_schema.dark_35);
				this.saveCheckBox.setBackground(Color_schema.dark_35);
				this.markingLabelPanel.setBackground(Color_schema.dark_35);
				}
			}
		/*
			public void setMarkingName(String markingName) {
				this.markingName = markingName;
			}

			public int getImageLayerID() {
				return iLayerID;
			}

			public void setImageLayerID(int iLayerID) {
				this.iLayerID = iLayerID;
			}
		*/

		}
