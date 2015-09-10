package information;

import java.awt.Point;
import java.awt.image.BufferedImage;

public class PositionedImage {
private BufferedImage image = null;
private Point position =null;

public PositionedImage(BufferedImage im, Point p){
	this.setImage(im);
	this.setPosition(p);
}
public PositionedImage(BufferedImage im){
	this.setImage(im);
	this.setPosition(new Point(0,0));
}

public BufferedImage getImage() {
	return image;
}

public void setImage(BufferedImage image) {
	this.image = image;
}

public Point getPosition() {
	return position;
}

public void setPosition(Point position) {
	this.position = position;
}
}
