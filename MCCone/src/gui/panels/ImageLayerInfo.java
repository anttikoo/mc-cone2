package gui.panels;


//import gui.AddImageLayerDialog.ImageAndMarkingPanel;
import gui.Color_schema;
import gui.GUI;
import gui.MarkingProperties;
import gui.MouseListenerCreator;
import gui.ShapeIcon;
import information.Fonts;
import information.ID;
import information.ImageLayer;
import information.MarkingLayer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import managers.ProgramLogger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * Panel for presenting information of one Image Layer and it's Markings
 * @author Antti
 *
 */
public class ImageLayerInfo extends JPanel{
private int imageLayerID;
private String imageLayerName;
private JPanel titlePanel;
private JLabel title;
private GUI gui;


private JPanel markingArea;
JPanel addMarkingLayerJPanel;
private final static Logger LOGGER = Logger.getLogger("MCCLogger");


	/**
	 * Class constructor
	 * @param imageLayer the ImageLayer which information will be shown
	 * @param gui the parent  GUI object for sending information
	 */
	public ImageLayerInfo(ImageLayer imageLayer, GUI gui){
		try {
			// set objects and information
			this.gui=gui;
			this.setImageLayer_ID(imageLayer.getLayerID());
			this.setImageLayerName(imageLayer.getImageFileName());

			//set panel
			this.setBackground(Color_schema.dark_40);
			this.setLayout(new BorderLayout(0,0));


			this.setMaximumSize(new Dimension(5000,75));
			this.setBorder(BorderFactory.createLineBorder(Color_schema.button_light_border, 1));

			// setup title for Image Layer
			this.titlePanel = new JPanel();
			this.titlePanel.setLayout(new BoxLayout(this.titlePanel, BoxLayout.LINE_AXIS));
			this.titlePanel.setMaximumSize(new Dimension(gui.getRightPanelWidth(),40));
		//	this.titlePanel.setMinimumSize(new Dimension(50,40));
			this.titlePanel.setPreferredSize(new Dimension(50,40));
			this.titlePanel.setBackground(Color_schema.dark_40);
			JPanel titleLabelJPanel = new JPanel();
			titleLabelJPanel.setLayout(new BoxLayout(titleLabelJPanel, BoxLayout.LINE_AXIS));
			titleLabelJPanel.setMaximumSize(new Dimension(gui.getRightPanelWidth()-120,40));
			titleLabelJPanel.setPreferredSize(new Dimension(gui.getRightPanelWidth()-120,40));
		//	LOGGER.fine("titlePanelmaxWidth: "+titleLabelJPanel.getMaximumSize().width);
			titleLabelJPanel.setBackground(Color_schema.dark_40);

			JButton selectedImageLayerJButton = new JButton();
			if(imageLayer.isSelected())
				selectedImageLayerJButton.setIcon(getImageIcon("/images/eye_open.png"));
			else
				selectedImageLayerJButton.setIcon(getImageIcon("/images/eye_closed.png"));



			selectedImageLayerJButton.setAlignmentX(Component.LEFT_ALIGNMENT);
			selectedImageLayerJButton.setPreferredSize(new Dimension(40,35));
			selectedImageLayerJButton.setMaximumSize(new Dimension(40,35));
			selectedImageLayerJButton.setMinimumSize(new Dimension(40,35));
			selectedImageLayerJButton.setMargin(new Insets(0, 0, 0, 0));
			selectedImageLayerJButton.setBorder(BorderFactory.createLineBorder(Color_schema.dark_40, 1));
			selectedImageLayerJButton.setContentAreaFilled(false);
			selectedImageLayerJButton.setFocusable(false);
			addActionsToJButtons(selectedImageLayerJButton, ID.SELECTED_IMAGELAYER_JBUTTON);
			MouseListenerCreator.addMouseListenerToButtonsWithDark40Border(selectedImageLayerJButton);
		//	createMouseListenerToButtons(selectedImageLayerJButton, ID.SELECTED_IMAGELAYER_JBUTTON);

		/*
			JScrollPane titleScroll = new JScrollPane(titleLabelJPanel);
			titleScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			titleScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			titleScroll.setMaximumSize(new Dimension((int)(gui.getScreenSize().getWidth()/5),60));
			titleScroll.setPreferredSize(new Dimension(250,60));
			titleScroll.setBorder(BorderFactory.createEmptyBorder());
	*/

			// add gridIcon for imagelayer
			JButton gridButton=new JButton();
			gridButton.setPreferredSize(new Dimension(30,30));
			gridButton.setBorder(BorderFactory.createLineBorder(Color_schema.dark_40, 2));

			gridButton.setContentAreaFilled(false);
			gridButton.setFocusable(false);
			addActionsToJButtons(gridButton, ID.IMAGELAYER_SET_GRID);
			MouseListenerCreator.addMouseListenerToButtonsWithDark40Border(gridButton);
			if(imageLayer.hasGridOn()){
				gridButton.setIcon(getImageIcon("/images/g_selected.png"));
			}
			else{
				gridButton.setIcon(getImageIcon("/images/g_unselected.png"));
			}
			this.title=new JLabel(splitTextIfTooLong(this.getImageLayerName()));
			Font rightFont = setRightTitleFont(title.getText(),Fonts.p20, false);
			this.title.setFont(rightFont);
			this.title.setMaximumSize(new Dimension(gui.getRightPanelWidth()-120,40));

			this.title.setForeground(Color_schema.white_230);
			if(imageLayer.isSelected()){
				rightFont = setRightTitleFont(title.getText(), Fonts.b20, true);
				this.title.setForeground(Color_schema.orange_bright);
				this.title.setFont(rightFont);
			}

			if(this.imageLayerName.length()> 50)
				this.title.setToolTipText(this.imageLayerName);
			addMouseListenerToTitle(this.title, ID.IMAGE_INFO);
		//	this.title.setAlignmentX(Component.CENTER_ALIGNMENT);

			titleLabelJPanel.add(this.title);

			this.titlePanel.add(selectedImageLayerJButton);
		//	this.titlePanel.add(Box.createRigidArea(new Dimension(5,0)));
			this.titlePanel.add(gridButton);
			this.titlePanel.add(Box.createRigidArea(new Dimension(5,0)));
			this.titlePanel.add(titleLabelJPanel);
		//	this.titlePanel.add(title);
			this.titlePanel.add(Box.createHorizontalGlue()); // when panel gets bigger the horizontal extra space comes between title and deleting button

			// setup button for deleting this Image Layer
			JPanel closeImagePanel = new JPanel();
		//	closeImagePanel.setLayout(new BoxLayout(closeImagePanel, BoxLayout.LINE_AXIS));
			closeImagePanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			closeImagePanel.setMaximumSize(new Dimension(25,40));
			closeImagePanel.setPreferredSize(new Dimension(25,40));
			closeImagePanel.setMinimumSize(new Dimension(25,40));
			closeImagePanel.setBackground(Color_schema.dark_40);
			JButton closeJButton = new JButton(getImageIcon("/images/close_big.png"));
			closeJButton.setAlignmentY(Component.CENTER_ALIGNMENT);
			closeJButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
			closeJButton.setPreferredSize(new Dimension(20,20));
			closeJButton.setMaximumSize(new Dimension(20,20));
			closeJButton.setMargin(new Insets(0, 0,0, 0));
			closeJButton.setBorder(BorderFactory.createLineBorder(Color_schema.button_grey_border, 1));
			closeJButton.setContentAreaFilled(false);
			closeJButton.setFocusable(false);
			closeJButton.setToolTipText("Delete Image Layer: " +this.title.getText());

			// set up listener for closeButton
			addMouseListenerToCloseButtons(closeJButton);
			addActionsToJButtons(closeJButton, ID.DELETE_IMAGELAYER);

			closeImagePanel.add(closeJButton, BorderLayout.CENTER);
			titlePanel.add(closeImagePanel);

			// markingArea will contain one or more individual MarkingPanels

			this.markingArea = new JPanel();
			this.markingArea.setLayout(new BoxLayout(this.markingArea, BoxLayout.PAGE_AXIS));
			this.markingArea.setBackground(Color_schema.dark_30);
			this.markingArea.setMaximumSize(new Dimension(5000,40));

			// add all MarkingLayers to markingsArea
			addMarkingLayers(imageLayer.getMarkingLayers());



			this.add(this.titlePanel, BorderLayout.PAGE_START);
			this.add(this.markingArea, BorderLayout.CENTER);
			initAddMarkingButton(); // add JButton for creating new Marking to bottom of ImageLayerInfo

		} catch (Exception e) {
			LOGGER.severe("Error in " +e.getClass().toString() + " :" +e.getMessage());
		}
	}

	private void addMouseListenerToTitle(JLabel label, int type){
		if(type == ID.IMAGE_INFO)
		label.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mousePressed(MouseEvent e) {
				int iLayerID=((ImageLayerInfo)((JPanel)((JPanel)((JLabel)e.getSource()).getParent()).getParent()).getParent()).getImageLayerID();
				//LOGGER.fine("Panel: " +((ImageLayerInfo)((JPanel)((JPanel)((JLabel)e.getSource()).getParent()).getParent()).getParent()).getImageLayerID());
				gui.setSelectedImageLayerAndImage(iLayerID);

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});
		else
			if(type== ID.MARKING_INFO)
				label.addMouseListener(new MouseListener() {

					@Override
					public void mouseReleased(MouseEvent e) {
						// TODO Auto-generated method stub
					}

					@Override
					public void mousePressed(MouseEvent e) {
						String iLayerID= e.getSource().getClass().toString();
						//LOGGER.fine("marking title:: " +((ImageLayerInfo)((JPanel)((JPanel)((JLabel)e.getSource()).getParent()).getParent()).getParent()).getImageLayerID());
						//gui.changeSelectedImageLayerAndImage(iLayerID);

					}

					@Override
					public void mouseExited(MouseEvent e) {
						// TODO Auto-generated method stub
					}

					@Override
					public void mouseEntered(MouseEvent e) {
						// TODO Auto-generated method stub
					}
					@Override
					public void mouseClicked(MouseEvent e) {
						// TODO Auto-generated method stub

					}
				});
	}

	private int getWidthOfText(Font font, JLabel lab){

		try {
			return lab.getFontMetrics(font).stringWidth(lab.getText());
		} catch (Exception e) {
			LOGGER.severe("Error in  getting JLabel width: " +e.getClass().toString() + " :" +e.getMessage());
			return -1;
		}
	}

	private String splitTextIfTooLong(String text){
		try {
			if(text != null){
				if(text.length()>50){
				 // text is too long for title
					text = text.substring(0, 10) + "..." + text.substring(text.length()-10,text.length());
				}
				return text;

			}
			else{

				return "";
			}
		} catch (Exception e) {
			LOGGER.severe("Error splitting titles: " +e.getClass().toString() + " :" +e.getMessage());
			return text;
		}
	}

	/**
	 * Changes font size for title text for getting it to fit
	 * @param text string which length affects to font size
	 * @return font which size has been set
	 */
	private Font setRightTitleFont(String text, Font font, boolean bold){


			if(text != null && text.length()>20){
				if(bold){
					font=Fonts.b18;
				}
				else{
					font=Fonts.p18;
				}
				if(text.length()>30){
					if(bold){
						font=Fonts.b16;
					}
					else{
						font=Fonts.p16;
					}
					if(text.length()>40){
						if(bold){
							font=Fonts.b14;
						}
						else{
							font=Fonts.p14;
						}
					}
				}
			}
			return font;


	}

	/**
	 * Adds an action to JButton
	 * @param button The button where the action is added
	 * @param typeOfAction int indentifier the type of action
	 */
	private void addActionsToJButtons(JButton button, int typeOfAction){

		if(typeOfAction == ID.DELETE_IMAGELAYER){
			button.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						// remove Imagelayer by giving unique layerID and layername
						int layerID =((ImageLayerInfo)((JPanel)((JPanel)((JButton)e.getSource()).getParent()).getParent()).getParent()).getImageLayerID();
					//	LOGGER.fine(" layerid:"+layerID);
						String layerName=((ImageLayerInfo)((JPanel)((JPanel)((JButton)e.getSource()).getParent()).getParent()).getParent()).getImageLayerName();
					//	LOGGER.fine(" layername:"+layerName);
						if(layerID >0)
							gui.removeImageLayer(layerID, layerName);
					} catch (Exception e1) {
						LOGGER.severe("Error in deleting ImageLayer: " +e1.getClass().toString() + " :" +e1.getMessage());

					}
				}
			});




		}
		else if(typeOfAction == ID.DELETE_MARKINGLAYER){
			button.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						// remove Imagelayer by giving unique layerID and layername
						String markingLayerName=   ((SingleMarkingPanel)((JPanel)((JButton)e.getSource()).getParent()).getParent()).getMarking_layer_name();
						int mLayerID =   ((SingleMarkingPanel)((JPanel)((JButton)e.getSource()).getParent()).getParent()).getMarking_layer_id();
						int iLayerID= ((ImageLayerInfo)((JPanel)((SingleMarkingPanel)((JPanel)((JButton)e.getSource())
								.getParent()).getParent()).getParent()).getParent()).getImageLayerID();

							if(iLayerID >0)
							gui.removeMarkingLayer(iLayerID, mLayerID, markingLayerName);
					} catch (Exception e1) {
						LOGGER.severe("Error in deleting MarkingLayer" +e1.getClass().toString() + " :" +e1.getMessage());

					}
				}
			});


		}
		else // creating MarkingLayer
			if(typeOfAction == ID.CREATE_MARKINGLAYER){
				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
							try {
								int iLayerID =  ((ImageLayerInfo)((JPanel)((JButton)arg0.getSource()).getParent().getParent())).getImageLayerID();
								if(iLayerID > 0){
									gui.createNewMarkingLayer(iLayerID);
								//	LOGGER.fine("create marking 1");
								}
							} catch (Exception e) {
								LOGGER.severe("Error in creating new MarkingLayer" +e.getClass().toString() + " :" +e.getMessage());
							}
					}
				});
			}
			else // creating MarkingLayer
				if(typeOfAction == ID.EDIT_MARKING_PROPERTIES){
					button.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
								try {

									int mLayerID= ((SingleMarkingPanel)((JButton)e.getSource()).getParent()).getMarking_layer_id();

									int iLayerID= ((ImageLayerInfo)((JPanel)((SingleMarkingPanel)((JButton)e.getSource()).getParent()).getParent()).getParent()).getImageLayerID();
								//	LOGGER.fine(" "+iLayerID);

										if(iLayerID >0 && mLayerID>0){
											MarkingLayer editingMarking = gui.getMarkingLayer(iLayerID, mLayerID);
											if(editingMarking != null){

												
												//MarkingProperties dialog = new MarkingProperties(frame, gui, ((JButton)e.getSource()).getLocationOnScreen(), editingMarking);
												MarkingProperties dialog = new MarkingProperties(new JFrame(), gui, ((JButton)e.getSource()).getLocationOnScreen(), editingMarking);
											
													dialog.showDialog();
													dialog =null;
											}
										}

								} catch (Exception ex) {
									LOGGER.severe("Error in editing marking properties" +e.getClass().toString() + " : " +ex.getMessage());
								}
						}
					});
				}
				else // setting visibility of MarkingLayer
					if(typeOfAction == ID.SET_MARKING_LAYER_VISIBILITY){
						button.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
									try {
									//	LOGGER.fine("pressed visibility button");
										SingleMarkingPanel sPanel =(SingleMarkingPanel)((JButton)e.getSource()).getParent();
										boolean isVisible= sPanel.isVisibleLayer();


										if(isVisible){
											sPanel.setVisibleLayer(false);
										((JButton)e.getSource()).setIcon(getImageIcon("/images/eye_closed_small.png"));
										}else{
											sPanel.setVisibleLayer(true);
											((JButton)e.getSource()).setIcon(getImageIcon("/images/eye_open_small.png"));
										}
										int mLayerID= sPanel.getMarking_layer_id();
										//int mLayerID= ((SingleMarkingPanel)((JButton)e.getSource()).getParent()).getMarking_layer_id();

									//	int iLayerID= ((ImageLayerInfo)((JPanel)((SingleMarkingPanel)((JButton)e.getSource()).getParent()).getParent()).getParent()).getImageLayerID();
									//	LOGGER.fine(" "+iLayerID);

										gui.setMarkingLayerVisibility(mLayerID, !isVisible);

									} catch (Exception ex) {
										LOGGER.severe("Error in editing marking properties" +e.getClass().toString() + " : " +ex.getMessage());
									}
							}
						});
					}
					else // setting title of MarkingLayer
						if(typeOfAction == ID.EDIT_MARKING_TITLE){
							button.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
										try {
											int lastcaret = Math.max(((SingleMarkingPanel)((JButton)e.getSource()).getParent()).markingTitleJTextField.getText().length(), 0);
											((SingleMarkingPanel)((JButton)e.getSource()).getParent()).markingTitleJTextField.setCaretPosition(lastcaret);
											((SingleMarkingPanel)((JButton)e.getSource()).getParent()).markingTitleJTextField.setEnabled(true);
											((SingleMarkingPanel)((JButton)e.getSource()).getParent()).markingTitleJTextField.requestFocus();
											Font fontUsed= ((SingleMarkingPanel)((JButton)e.getSource()).getParent()).markingTitleJTextField.getFont();
											((SingleMarkingPanel)((JButton)e.getSource()).getParent()).markingTitleJTextField.setFont(new Font(fontUsed.getName(),Font.ITALIC,fontUsed.getSize()));
										} catch (Exception ex) {
											LOGGER.severe("Error in editing marking properties" +e.getClass().toString() + " : " +ex.getMessage());
										}
								}
							});
						}
						else // set imageLayer selected
							if(typeOfAction == ID.SELECTED_IMAGELAYER_JBUTTON){
								button.addActionListener(new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent e) {
											try {
												int iLayerID = ((ImageLayerInfo)((JPanel)((JButton)e.getSource()).getParent()).getParent()).getImageLayerID();
												//LOGGER.fine(" layerid:"+layerID);
												gui.setSelectedImageLayerAndImage(iLayerID);

												} catch (Exception ex) {
												LOGGER.severe("Error in editing marking properties" +e.getClass().toString() + " : " +ex.getMessage());
											}
									}
								});
							}
							else // setting title of MarkingLayer
								if(typeOfAction == ID.IMAGELAYER_SET_GRID){
									button.addActionListener(new ActionListener() {
										@Override
										public void actionPerformed(ActionEvent e) {
												try {
													int iLayerID = ((ImageLayerInfo)((JPanel)((JButton)e.getSource()).getParent()).getParent()).getImageLayerID();
													//LOGGER.fine(" layerid:"+layerID);
													gui.showGridPropertiesPanelForMarkingLayersOfImageLayer(((JButton)e.getSource()).getLocationOnScreen(), iLayerID);

													} catch (Exception ex) {
													LOGGER.severe("Error in editing marking properties" +e.getClass().toString() + " : " +ex.getMessage());
												}
										}
									});
								}
								else // set grid for single MarkingLayer
									if(typeOfAction == ID.MARKINGLAYER_SET_GRID){
										button.addActionListener(new ActionListener() {
											@Override
											public void actionPerformed(ActionEvent e) {
													try {

														int mLayerID= ((SingleMarkingPanel)((JButton)e.getSource()).getParent()).getMarking_layer_id();

													//	int iLayerID= ((ImageLayerInfo)((JPanel)((SingleMarkingPanel)((JButton)e.getSource()).getParent()).getParent()).getParent()).getImageLayerID();
													//	LOGGER.fine(" "+iLayerID);

														if(mLayerID>0){
															gui.showGridPropertiesPanelForSingleMarkingLayer(((JButton)e.getSource()).getLocationOnScreen(), mLayerID);

														}

													} catch (Exception ex) {
														LOGGER.severe("Error in editing marking properties" +e.getClass().toString() + " : " +ex.getMessage());
													}
											}
										});
									}
	}

	/** Adds MarkingLayers to markingArea JPanel
	 * @param markingLayerList
	 */
	private void addMarkingLayers(ArrayList<MarkingLayer> markingLayerList){

		if(markingLayerList != null && markingLayerList.size()>0){

			Iterator<MarkingLayer> iterator = markingLayerList.iterator();
			while (iterator.hasNext()) {
				MarkingLayer ma = (MarkingLayer) iterator.next();
				if (ma.getLayerID() >0 && ma.getLayerName() != null && ma.getLayerName().length()>0) { // layerId not yet given to MarkingLayer
				//	LOGGER.fine("create marking 3 "+ma.getLayerID() + " " + ma.getLayerName());
					// increase the maximum height of area where SingleMarkingPanels are added
					this.markingArea.setMaximumSize(new Dimension((int)this.markingArea.getMaximumSize().getWidth(),(int)this.markingArea.getMaximumSize().getHeight()+25));
					// increase the maximum height of this SingleMarkingPanel
					this.setMaximumSize(new Dimension((int)this.getMaximumSize().getWidth(),(int)this.getMaximumSize().getHeight()+30));
					SingleMarkingPanel p = new SingleMarkingPanel(ma);
					this.markingArea.add(p);
					this.markingArea.add(Box.createRigidArea(new Dimension(0,5)));
				}

			}
		}

	}



	/**
	 * Creates a JButton for adding new Marking Layer
	 * @throws Exception
	 */
	private void initAddMarkingButton() throws Exception{
		addMarkingLayerJPanel = new JPanel();
		addMarkingLayerJPanel.setLayout(new BoxLayout(addMarkingLayerJPanel, BoxLayout.PAGE_AXIS));
		addMarkingLayerJPanel.setMaximumSize(new Dimension(5000,35));
		addMarkingLayerJPanel.setPreferredSize(new Dimension(140, 35));
		addMarkingLayerJPanel.setBackground(Color_schema.dark_40);
		JButton addMarkingLayerJButton = new JButton("ADD NEW MARKING");
		addMarkingLayerJButton.setMaximumSize(new Dimension(220, 30));
	//	addMarkingLayerJButton.setFont(new Font("Consolas", Font.PLAIN,15));
		addMarkingLayerJButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		addMarkingLayerJButton.setToolTipText("Create new Marking Layer for this Image Layer: " +getImageLayerName() );
		addMarkingLayerJButton.setBackground(Color_schema.dark_30);
	//	addMarkingLayerJButton.setForeground(Color_schema.color_Button_light_border);
		addMarkingLayerJButton.setBorder(BorderFactory.createLineBorder(Color_schema.button_grey_border, 1));
		addMarkingLayerJButton.setFocusable(false);

		addActionsToJButtons(addMarkingLayerJButton, ID.CREATE_MARKINGLAYER);
		addMarkingLayerJButton.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent arg0) {
				((JButton)arg0.getSource()).setForeground(Color_schema.white_230);
			}
			@Override
			public void mousePressed(MouseEvent arg0) {
				((JButton)arg0.getSource()).setForeground(Color_schema.orange_dark);
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
				((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.button_grey_border, 2));
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
		addMarkingLayerJPanel.add(Box.createRigidArea(new Dimension(0,2)));
		addMarkingLayerJPanel.add(addMarkingLayerJButton);
		addMarkingLayerJPanel.add(Box.createRigidArea(new Dimension(0,2)));

		this.add(addMarkingLayerJPanel,BorderLayout.PAGE_END);
	}

private ImageIcon getImageIcon(String path) {

		try {
			URL url = this.getClass().getResource(path);
			ImageIcon img = new ImageIcon(url);
			return img;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.severe("Error in adding SingleMarkingPanel " +e.getClass().toString() + " :" +e.getMessage());
			return null;
		}
}

private void createMouseListenerToButtons(JButton button, int typeOfButton){
	if(typeOfButton == ID.VISIBLEMARKINGJBUTTON){
	button.addMouseListener(new MouseListener() {

		@Override
		public void mouseReleased(MouseEvent arg0) {
			((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.dark_70, 1));

		}
		@Override
		public void mousePressed(MouseEvent arg0) {
			((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.orange_dark, 2));

		}
		@Override
		public void mouseExited(MouseEvent arg0) {
			((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.dark_70, 1));

		}
		@Override
		public void mouseEntered(MouseEvent arg0) {
			((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.button_light_border, 1));

		}
		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}
	});
	}
	else if(typeOfButton==ID.SHAPEJBUTTON){
		button.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.dark_70, 1));
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.orange_dark, 1));
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.dark_70, 1));

			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.button_light_border, 1));

			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
	}else if(typeOfButton==ID.SELECTED_IMAGELAYER_JBUTTON){
		button.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.dark_40, 1));
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.orange_dark, 1));
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.dark_40, 1));

			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				((JButton)arg0.getSource()).setBorder(BorderFactory.createLineBorder(Color_schema.button_light_border, 1));

			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

}

	private void addMouseListenerToCloseButtons(JButton button) throws Exception{
		button.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent arg0) {
				((JButton)arg0.getSource()).setIcon(getImageIcon("/images/close_small.png"));

			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				((JButton)arg0.getSource()).setIcon(getImageIcon("/images/close_small_selected.png"));

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

	public int getImageLayerID() {
		return imageLayerID;
	}

	public void setImageLayer_ID(int layer_ID) {
		this.imageLayerID = layer_ID;
	}
	public String getImageLayerName() {
		return imageLayerName;
	}
	public void setImageLayerName(String layer_name) {
		this.imageLayerName = layer_name;
	}





	/**
	 * Creates a JPanel containing information of single Marking panel
	 * @author Antti Kurronen
	 *
	 */
	private class SingleMarkingPanel extends JPanel {
		private int marking_layer_id;
		private boolean isVisibleLayer = true;
		private String marking_layer_name;
		private JTextField markingTitleJTextField;
		private JButton closeJButton;
		private JLabel countJLabel;
		private MarkingLayer markingLayer;

		/**
		 * @param name name for markingPanel is given only if markingPanel is opened from file
		 * @param id identifier for layer that enables accessing to right markings between informationCenter - GUI
		 */
		private SingleMarkingPanel(MarkingLayer markingLayer){
			try {
				this.markingLayer=markingLayer;
				setVisibleLayer(markingLayer.isVisible());
				this.setMarking_layer_name(markingLayer.getLayerName());
				this.marking_layer_id= markingLayer.getLayerID();
				this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
				this.setBorder(BorderFactory.createEmptyBorder());
				this.setBackground(Color_schema.dark_70);
				this.setMaximumSize(new Dimension(5000,25));
				this.setMinimumSize(new Dimension(20,25));
				JButton visibleMarkingJButton = new JButton();
				if(markingLayer.isVisible())
					visibleMarkingJButton.setIcon(getImageIcon("/images/eye_open_small.png"));
				else
					visibleMarkingJButton.setIcon(getImageIcon("/images/eye_closed_small.png"));

				visibleMarkingJButton.setAlignmentX(Component.LEFT_ALIGNMENT);
				visibleMarkingJButton.setPreferredSize(new Dimension(37,20));
				visibleMarkingJButton.setMargin(new Insets(0, 0, 0, 0));
				visibleMarkingJButton.setBorder(BorderFactory.createLineBorder(Color_schema.dark_70, 1));
				visibleMarkingJButton.setContentAreaFilled(false);
				visibleMarkingJButton.setFocusable(false);
				visibleMarkingJButton.setToolTipText("Change visibility of MarkingLayer");

				addActionsToJButtons(visibleMarkingJButton, ID.SET_MARKING_LAYER_VISIBILITY);
				createMouseListenerToButtons(visibleMarkingJButton, ID.VISIBLEMARKINGJBUTTON);

				ShapeIcon icon = new ShapeIcon(markingLayer.getShapeID(), 22, 22, markingLayer.getColor(), this.getBackground());
				JButton shapeJButton = new JButton(icon);
			//	JButton shapeJButton = new JButton(getImageIcon("/images/eye_closed_small.png"));


				shapeJButton.setAlignmentX(Component.LEFT_ALIGNMENT);
				shapeJButton.setPreferredSize(new Dimension(22,27));
				shapeJButton.setMargin(new Insets(0, 0, 0, 0));
				shapeJButton.setBorder(BorderFactory.createLineBorder(Color_schema.dark_70, 1));
				shapeJButton.setContentAreaFilled(false);
				shapeJButton.setFocusable(false);
				shapeJButton.setToolTipText("Edit shape and color of markings ");
				addActionsToJButtons(shapeJButton, ID.EDIT_MARKING_PROPERTIES);
				createMouseListenerToButtons(shapeJButton, ID.SHAPEJBUTTON);

				// add gridIcon for MarkingLayer
				JButton gridButton=new JButton();
				gridButton.setPreferredSize(new Dimension(25,25));
				gridButton.setBorder(BorderFactory.createLineBorder(Color_schema.dark_70, 1));

				gridButton.setContentAreaFilled(false);
				gridButton.setFocusable(false);
				gridButton.setToolTipText("Edit grid of MarkingLayer");
				addActionsToJButtons(gridButton, ID.MARKINGLAYER_SET_GRID);
				MouseListenerCreator.addMouseListenerToButtonsWithMarkingWith70Border(gridButton);
				if(this.markingLayer.isGridON()){
					gridButton.setIcon(getImageIcon("/images/g_selected.png"));
				}
				else{
					gridButton.setIcon(getImageIcon("/images/g_unselected_lighter.png"));
				}

				JButton editMarkingTitleJButton = new JButton();

				editMarkingTitleJButton.setIcon(getImageIcon("/images/editPen_small.png"));

				editMarkingTitleJButton.setAlignmentX(Component.LEFT_ALIGNMENT);
				editMarkingTitleJButton.setPreferredSize(new Dimension(25,25));
				editMarkingTitleJButton.setMargin(new Insets(0, 0, 0, 0));
				editMarkingTitleJButton.setBorder(BorderFactory.createLineBorder(Color_schema.dark_70, 1));
				editMarkingTitleJButton.setContentAreaFilled(false);
				editMarkingTitleJButton.setToolTipText("Edit title of MarkingLayer");
				addActionsToJButtons(editMarkingTitleJButton, ID.EDIT_MARKING_TITLE);
				createMouseListenerToButtons(editMarkingTitleJButton, ID.VISIBLEMARKINGJBUTTON); // can use same listeners as to visibleMarkingButton


				countJLabel = new JLabel(""+markingLayer.getCounts());
				countJLabel.setFont(Fonts.b17);
				countJLabel.setForeground(Color_schema.markingInfoPanel_fg);
				countJLabel.setAlignmentY(Component.CENTER_ALIGNMENT);

				markingTitleJTextField = new JTextField();
				markingTitleJTextField.setText(this.getMarking_layer_name());
		//		markingTitleJTextField.setAlignmentX(Component.RIGHT_ALIGNMENT);
				markingTitleJTextField.setCaretPosition(0);
				markingTitleJTextField.setEnabled(false);
				markingTitleJTextField.setFont(Fonts.p17);
				markingTitleJTextField.setForeground(Color_schema.white_230);
				markingTitleJTextField.setMaximumSize(new Dimension(gui.getRightPanelWidth()-170-getCountingTextWidth(),25));
				markingTitleJTextField.setPreferredSize(new Dimension(gui.getRightPanelWidth()-170-getCountingTextWidth(),25));

				if(markingLayer.isSelected()){
					markingTitleJTextField.setFont(Fonts.b18);
					markingTitleJTextField.setDisabledTextColor(Color_schema.orange_bright);
					markingTitleJTextField.setForeground(Color_schema.orange_bright);
				}
				markingTitleJTextField.setBackground(null);
				markingTitleJTextField.setBorder(null);
				markingTitleJTextField.setCaretColor(Color_schema.button_light_border);
				if(this.marking_layer_name.length()>50)
					markingTitleJTextField.setToolTipText(this.marking_layer_name);
				markingTitleJTextField.addCaretListener(new CaretListener() {

			        @Override
			        public void caretUpdate(CaretEvent e) {
			        	setMarking_layer_name(markingTitleJTextField.getText());

			        	closeJButton.setToolTipText("Delete Marking Layer: " +getMarking_layer_name());

			        }
			    });

				markingTitleJTextField.addKeyListener(new KeyListener() {

					@Override
					public void keyTyped(KeyEvent arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void keyReleased(KeyEvent arg0) {
						int imageID = ((ImageLayerInfo)((JPanel)((JPanel)((JTextField)arg0.getSource())
								.getParent()).getParent()).getParent()).getImageLayerID();

						int markingID = ((SingleMarkingPanel)((JTextField)arg0.getSource()).getParent()).getMarking_layer_id();
						String markingName = ((JTextField)arg0.getSource()).getText();
						gui.setMarkingLayerName(imageID, markingID, markingName);
						//LOGGER.fine("the text is: " +((JTextField)arg0.getSource()).getText() + "id"+markingID);
						if(arg0.getKeyCode()== KeyEvent.VK_ENTER){
							title.requestFocus();


						}


					}

					@Override
					public void keyPressed(KeyEvent arg0) {



					}
				});

				markingTitleJTextField.addFocusListener(new FocusListener() {

					@Override
					public void focusLost(FocusEvent e) {
						markingTitleJTextField.setEnabled(false);

						System.out.println("text par"+((JTextField)e.getSource()).getParent().getClass().toString());
						if(((SingleMarkingPanel)((JTextField)e.getSource()).getParent()).isMarkingLayerSelected())
							markingTitleJTextField.setFont(Fonts.b18);
						else
							markingTitleJTextField.setFont(Fonts.p17);

					//	markingTitleJTextField.setFont(new Font(fontUsed.getName(),Font.BOLD,fontUsed.getSize()));
						markingTitleJTextField.setCaretPosition(0);
					}

					@Override
					public void focusGained(FocusEvent e) {
						// TODO Auto-generated method stub

					}
				});



				markingTitleJTextField.addMouseListener(new MouseListener() {

					@Override
					public void mouseReleased(MouseEvent e) {
						// TODO Auto-generated method stub

					}

					@Override
					public void mousePressed(MouseEvent e) {
						if(!markingTitleJTextField.isEnabled()){
							gui.setSelectedMarkingLayer(marking_layer_id);
							//LOGGER.fine("selected MarkingLayer: "+marking_layer_id);
						}

					}

					@Override
					public void mouseExited(MouseEvent e) {
						// TODO Auto-generated method stub

					}

					@Override
					public void mouseEntered(MouseEvent e) {
						// TODO Auto-generated method stub

					}

					@Override
					public void mouseClicked(MouseEvent e) {
						// TODO Auto-generated method stub

					}
				});




				JPanel countingJPanel =new JPanel();
				countingJPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 1, 1));
				countingJPanel.setBackground(Color_schema.dark_70);
				countingJPanel.add(countJLabel);
				countingJPanel.setMaximumSize(new Dimension(5000,20));
				countingJPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
				countingJPanel.add(Box.createRigidArea(new Dimension(5,0)));

				//Add closing box to right of markinginfo
			/*	JPanel closePanel = new JPanel();
				closePanel.setLayout(new BorderLayout(0,0));
				closePanel.setMaximumSize(new Dimension(5000,18));
				closePanel.setBackground(Color_schema.dark_70);
			*/
				closeJButton = new JButton(getImageIcon("/images/close_small.png"));
				closeJButton.setAlignmentY(Component.CENTER_ALIGNMENT);
				closeJButton.setPreferredSize(new Dimension(18,18));
				closeJButton.setMargin(new Insets(0, 0, 0, 0));
				closeJButton.setBorder(BorderFactory.createEmptyBorder());
				closeJButton.setContentAreaFilled(false);
				closeJButton.setFocusable(false);
				closeJButton.setBorder(BorderFactory.createLineBorder(Color_schema.button_grey_border, 1));
				closeJButton.setToolTipText("Delete Marking Layer: " +getMarking_layer_name());
			//	addMouseListenerToCloseButtons(closeJButton);
				MouseListenerCreator.addMouseListenerToSmallCloseButtons(closeJButton);
				addActionsToJButtons(closeJButton, ID.DELETE_MARKINGLAYER);

			//	closePanel.add(closeJButton, BorderLayout.CENTER);
			//	countingJPanel.add(closePanel);
				countingJPanel.add(closeJButton);
				countingJPanel.add(Box.createRigidArea(new Dimension(3,0)));



				this.add(visibleMarkingJButton); //

				this.add(shapeJButton);
			//	this.add(Box.createRigidArea(new Dimension(5,0)));
				this.add(gridButton);
				this.add(editMarkingTitleJButton);
				this.add(Box.createRigidArea(new Dimension(5,0)));
				this.add(markingTitleJTextField);

				this.add(Box.createHorizontalGlue());
				this.add(countingJPanel);

			//	this.validate();


			} catch (Exception e) {
				// TODO Auto-generated catch block
				LOGGER.severe("Error in adding SingleMarkingPanel " +e.getClass().toString() + " :" +e.getMessage());
			}
		}



		private int getCountingTextWidth(){
			return getWidthOfText(this.countJLabel.getFont(), this.countJLabel);
		}




		public int getMarking_layer_id() {
			return marking_layer_id;
		}

		public boolean isMarkingLayerSelected(){
			if(this.markingLayer!= null){
				return this.markingLayer.isSelected();
			}
			return false;
		}

		/**
		 * Updates the markinglayerId value and JLabel showing it
		 * @param marking_layer_id int value of counted markings
		 */
		public void updateMarking_layer_id(int marking_layer_id) {
			this.marking_layer_id = marking_layer_id;
			this.countJLabel.setText(""+this.marking_layer_id);
		}

		/**
		 * Increases the markinglayerId value with one and updates JLabel showing it.
		 */
		public void increaseMarking_layer_id_with_one() {
			this.marking_layer_id++;
			this.countJLabel.setText(""+this.marking_layer_id);
		}



		public String getMarking_layer_name() {
			return marking_layer_name;
		}


		/**
		 * sets this Marking Layer name
		 * @param marking_layer_name name for Marking Layer
		 */
		public void setMarking_layer_name(String marking_layer_name) {
			this.marking_layer_name = marking_layer_name;
		}

		public boolean isVisibleLayer() {
			return isVisibleLayer;
		}

		public void setVisibleLayer(boolean isVisibleLayer) {
			this.isVisibleLayer = isVisibleLayer;
		}

	}

}
