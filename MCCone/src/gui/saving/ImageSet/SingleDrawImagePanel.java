package gui.saving.ImageSet;

import gui.Color_schema;
import information.ImageLayer;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import managers.TaskManager;

public class SingleDrawImagePanel extends JPanel{
	private SingleImage singleImage;
	private int[] gridPosition=null; // row, column
	private String title;


	private Font font;
	private JPanel titlePanel;
	private int titlePanelHeight=30;
	private BufferedImage image;
	private TaskManager taskManager;
//	private JLabel titleLabel;
	private JTextField titleField;
	private Dimension panelDimension;


	public SingleDrawImagePanel(BufferedImage bi, String title,TaskManager taskManager, Font font ){
		this.setOpaque(true);
		this.taskManager=taskManager;
		this.singleImage=new SingleImage(bi, this.taskManager);
		this.title=title;
		this.setImage(bi);
		this.font=font;


		initPanel();
		this.revalidate();
		this.repaint();

	}

	private void initPanel(){

		this.setLayout(new BorderLayout());
	//	this.setLayout(null);
	//	this.setBorder(BorderFactory.createLineBorder(Color_schema.color_white_230, 2));
		this.setBackground(Color_schema.white_230);
		titlePanel = new JPanel();
		this.titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.PAGE_AXIS));
		this.titlePanel.setBackground(Color_schema.white_230);
	//	titleLabel = new JLabel(this.title);
	//	titleLabel.setFont(font);
	//	titleLabel.setForeground(Color_schema.color_white_230);
	//	this.titlePanel.add(titleLabel);

		this.titleField=new JTextField(this.title);
		//this.titleField.setText(this.title);
		this.titleField.setFont(font);
		this.titleField.setHorizontalAlignment(JLabel.LEFT);


		this.titleField.setBackground(Color_schema.white_230);
		this.titleField.setForeground(Color_schema.dark_20);
		this.titleField.setMinimumSize(new Dimension(300,font.getSize()+6));
		this.titleField.setBorder(null);
		this.titlePanel.add(this.titleField);

/*
		JPanel imagePanel=new JPanel();
		imagePanel.setLayout(new BorderLayout());
		imagePanel.setBackground(Color_schema.color_dark_40_bg);
		if(image != null){
			JLabel backLabel=new JLabel();
			ImageIcon icon= new ImageIcon(image);
			backLabel.setIcon(icon);
		//	backLabel.setLayout(new BorderLayout());
			imagePanel.add(backLabel, BorderLayout.CENTER);
		}
		*/
	/*	JPanel singleImagePanel = new JPanel();
		singleImagePanel.setLayout(new BoxLayout(singleImagePanel, BoxLayout.X_AXIS));
		singleImagePanel.setBackground(Color_schema.color_orange_bright);
		singleImagePanel.add(this.singleImage);
*/
		this.add(titlePanel, BorderLayout.PAGE_START);
		this.add(this.singleImage, BorderLayout.CENTER);
	//	this.add(titlePanel);
	//	this.add(this.singleImage);

	}

	public Font getFont() {
		return font;
	}

	public void updateFont(Font f) {
		if(this.titlePanel.getPreferredSize().width >0)
		while(this.titlePanel.getPreferredSize().width < getTitleWidth(f)){
			f=new Font(f.getFontName(),f.getStyle(),f.getSize()-1);
		}
		this.font = f;
		this.titleField.setFont(this.font);
		this.titleField.repaint();

	}

	public BufferedImage getScaledImage(Dimension destination){
		return this.singleImage.getScaledImage(destination);
	}

	public String getTitle() {
		return this.titleField.getText();
	}

	public void setGridPosition(int r, int c){
		this.gridPosition=new int[]{r,c};
	}

	public void setGridPosition(int[] rc){
		this.gridPosition=rc;
	}

	public int[] getGridPosition(){
		return this.gridPosition;
	}

	public void setGridPositionNull(){
		this.gridPosition=null;
	}

	private int getTitleWidth(Font f){
		return this.titleField.getFontMetrics(f).stringWidth(this.title);
	}

	public Dimension getScaledImageDimension(){
		return this.singleImage.getScaledImageDimension();
	}



	public Dimension setDimension(Dimension dim, int titlePanelHeight) throws Exception{
		//int titlePanelHeight=this.font.getSize()+6;
		Dimension imageDimension=this.singleImage.setImageDimension(new Dimension(dim.width,dim.height-titlePanelHeight));

		panelDimension = new Dimension(imageDimension.width, imageDimension.height+titlePanelHeight);

		this.setMaximumSize(panelDimension);
		this.setPreferredSize(panelDimension);
		this.setMinimumSize(panelDimension);

		this.titlePanel.setMaximumSize(new Dimension(panelDimension.width,titlePanelHeight));
		this.titlePanel.setMinimumSize(new Dimension(panelDimension.width,titlePanelHeight));
		this.titlePanel.setPreferredSize(new Dimension(panelDimension.width,titlePanelHeight));
		this.titleField.setMaximumSize(new Dimension(panelDimension.width,titlePanelHeight));
		this.titleField.setMinimumSize(new Dimension(panelDimension.width,titlePanelHeight));
		this.titleField.setPreferredSize(new Dimension(panelDimension.width,titlePanelHeight));
	//	updateFont(this.font); // if the title don't fit to field -> decrease font size
		return panelDimension;

	}

	public BufferedImage getImage() {
		return image;
	}

	public int getTitlePanelHeight(){
		return this.titlePanel.getPreferredSize().height;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public Dimension getPanelSize(){
		return this.panelDimension;
	}











}
