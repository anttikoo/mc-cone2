package information;

import java.util.ArrayList;

public class LayersOfPath {
	private String xmlpath;
	private ArrayList<ImageLayer> imageLayerList;
	private int fileState;
	
	public int getFileState() {
		return fileState;
	}

	public LayersOfPath(String path, int state){
		this.setXmlpath(path);
		this.imageLayerList=new ArrayList<ImageLayer>();
		this.fileState=state;
		
	}

	public ArrayList<ImageLayer> getImageLayerList() {
		return imageLayerList;
	}

	public void setImageLayerList(ArrayList<ImageLayer> imageLayerList) {
		this.imageLayerList = imageLayerList;
	}
	
	public void addImageLayer(ImageLayer imageLayer) {
		this.imageLayerList.add(imageLayer);
	}

	public String getXmlpath() {
		return xmlpath;
	}

	public void setXmlpath(String xmlpath) {
		this.xmlpath = xmlpath;
	}
}
