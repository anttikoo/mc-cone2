package gui.saving.ImageSet;

import java.awt.image.BufferedImage;

public class BufferedImageWithName {
private BufferedImage image;
private String imageName;

public BufferedImageWithName(BufferedImage bi, String name){
	this.setImage(bi);
	this.setImageName(name);
}

public BufferedImage getImage() {
	return image;
}

public void setImage(BufferedImage image) {
	this.image = image;
}

public String getImageName() {
	return imageName;
}

public void setImageName(String imageName) {
	this.imageName = imageName;
}
}
