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
import org.imgscalr.Scalr;
import managers.TaskManager;

/**
 * The Class SingleImage contains the image and its dimension to be shown in panel.
 */
public class SingleImage extends JComponent implements MouseListener{
	private BufferedImage originalImage;
	private BufferedImage image_to_shown;
	private Dimension imageDimension;
	private int margin_x=0;
	private int margin_y=0;
	private TaskManager taskManager;

	/**
	 * Instantiates a new SingleImage
	 *
	 * @param image the image
	 * @param taskManager the task manager
	 */
	public SingleImage(BufferedImage image,TaskManager taskManager){
		this.setOpaque(true);
		this.originalImage=image;
		this.taskManager=taskManager;
		this.setBackground(new Color(0,0,0,255));
		this.setLayout(null);
		this.imageDimension=null;
	}

	/**
	 * Returns the horizontal margin.
	 *
	 * @return the margin_x
	 */
	public int getMargin_x() {
		return margin_x;
	}

	/**
	 * Returns the vertical margin.
	 *
	 * @return the margin_y
	 */
	public int getMargin_y() {
		return margin_y;
	}

	/**
	 * Returns the scaled image.
	 *
	 * @param destinationDimension the destination dimension
	 * @return the scaled image
	 */
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

	/**
	 * Returns the scaled image dimension.
	 *
	 * @return the scaled image dimension
	 */
	public Dimension getScaledImageDimension(){
		return this.imageDimension;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		//System.out.println("pressed:" +e.getSource().toString());

	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		//System.out.println("pressed:" +e.getSource().toString());

	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		// do nothing


	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		// do nothing

	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d= (Graphics2D)g;
		final Composite c = g2d.getComposite();
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
		g2d.dispose();

	}

	/**
	 * Sets the image dimension.
	 *
	 * @param panelDimension the panel dimension
	 * @return the dimension
	 */
	public Dimension setImageDimension(Dimension panelDimension){
		if(this.originalImage != null){
			// get new image scaled to Dimension of ImagePanel
			BufferedImage scaledImage= getScaledImage(panelDimension);

			if(scaledImage != null){
				this.image_to_shown=scaledImage;
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

	/**
	 * Sets the horizontal margin.
	 *
	 * @param margin the new horizontal margin.
	 */
	public void setMargin_x(int margin) {
		this.margin_x = margin;
	}

	/**
	 * Sets the vertical margin.
	 *
	 * @param margin the new vertical margin.
	 */
	public void setMargin_y(int margin) {
		this.margin_y = margin;
	}

}
