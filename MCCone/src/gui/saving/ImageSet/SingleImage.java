package gui.saving.ImageSet;

import gui.Color_schema;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.imgscalr.Scalr;

import managers.TaskManager;

public class SingleImage extends JComponent implements MouseListener{
	private BufferedImage originalImage;
	private BufferedImage image_to_shown;
	private Dimension imageDimension;
	private int margin_x=0;
	private int margin_y=0;
	private TaskManager taskManager;


	public SingleImage(BufferedImage image,TaskManager taskManager){
		this.setOpaque(true);
		this.originalImage=image;
		this.taskManager=taskManager;


	//	this.setBackground(Color_schema.color_dark_40_bg);
		this.setBackground(new Color(0,0,0,255));
		this.setLayout(null);
		this.imageDimension=null;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);


		Graphics2D g2d= (Graphics2D)g;
		final Composite c = g2d.getComposite();
	//	g2d.setComposite(AlphaComposite.Clear);
	     g2d.setPaint(new Color(0,0,0,255));
         g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 1.0f));
		if(this.image_to_shown != null){
			//g.drawImage(this.image_to_shown,0,0,this.getWidth(),this.getHeight(),null);
			g2d.drawImage(this.image_to_shown, this.margin_x,this.margin_y,null);

		}
		else{
			g2d.setPaint(Color_schema.dark_50);
			g2d.fillRect(0, 0, this.imageDimension.width, this.imageDimension.height);
		}
	//	g2d.setComposite(c);

		g2d.dispose();

	}

	public Dimension setImageDimension(Dimension panelDimension){
		if(this.originalImage != null){
			// get new image scaled to Dimension of ImagePanel
			BufferedImage scaledImage= getScaledImage(panelDimension);

			if(scaledImage != null){
				this.image_to_shown=scaledImage;
				//this.margin_x=(int)((panelDimension.width-scaledImage.getWidth())/2);
				//this.margin_y=(int)((panelDimension.height-scaledImage.getHeight())/2);
				this.imageDimension=new Dimension(this.image_to_shown.getWidth(), this.image_to_shown.getHeight());
			}
		}
		else{
			this.image_to_shown=null;
			this.margin_x=0;
			this.margin_y=0;
			this.imageDimension=panelDimension;
		}
		return this.imageDimension;

	}

	public BufferedImage getScaledImage(Dimension destinationDimension){
		if(this.originalImage != null){
			Scalr.Mode scalingMode= taskManager.getScalingMode(this.originalImage.getWidth(), this.originalImage.getHeight(), destinationDimension);

			// set up the quality/speed constant
			Scalr.Method processingQuality = Scalr.Method.ULTRA_QUALITY;

			// get new image scaled to Dimension of ImagePanel
			return Scalr.resize(this.originalImage, processingQuality, scalingMode, destinationDimension.width, destinationDimension.height, null);
		}
		return null;

	}

	public int getMargin_x() {
		return margin_x;
	}

	public Dimension getScaledImageDimension(){
		return this.imageDimension;
	}

	public int getMargin_y() {
		return margin_y;
	}

	public void setMargin_y(int margin) {
		this.margin_y = margin;
	}

	public void setMargin_x(int margin) {
		this.margin_x = margin;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		System.out.println("pressed:");
	//	if(e.getSource() instanceof SingleDrawImagePanel){
		//	this.imageSetCreator.setMovingPosition(this.gridPosition);
		//	System.out.println("pressed:" +((SingleDrawImagePanel)e.getSource()).getGridPosition()[0] + " "+((SingleDrawImagePanel)e.getSource()).getGridPosition()[1]);
		/*	this.movingPanel=(SingleDrawImagePanel)e.getSource();

			this.movingPosition=((SingleDrawImagePanel)e.getSource()).getGridPosition();
			System.out.println("pressed r c :" +this.movingPosition[0] + " "+movingPosition[1]);
*/
	//	}


	}

	@Override
	public void mouseReleased(MouseEvent e) {

	//	if(e.getSource() instanceof SingleDrawImagePanel){

		//	SingleDrawImagePanel sdp =(SingleDrawImagePanel)e.getSource();
			System.out.println("released r c :");
	//		if(this.imageSetCreator.getMovingPosition() != null && (this.imageSetCreator.getMovingPosition()[0] !=this.getGridPosition()[0] || this.imageSetCreator.getMovingPosition()[1] != this.getGridPosition()[1])){
				// swith positions
			//	int[] secondPosition = sdp.getGridPosition();
			//	sdp.setGridPosition(this.movingPosition);
			//	this.movingPanel.setGridPosition(tempPosition);
		//		this.imageSetCreator.swithcSingleDrawPanels(this.getGridPosition());
				System.out.println("released:"+ e.getSource().toString());
	//		}

	//	}

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		//System.out.println("pressed:" +e.getSource().toString());

	}

	@Override
	public void mouseExited(MouseEvent e) {
		//System.out.println("pressed:" +e.getSource().toString());

	}

}
