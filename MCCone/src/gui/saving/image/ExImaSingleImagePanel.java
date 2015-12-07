package gui.saving.image;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JPanel;

import gui.saving.SaverDialog;
import gui.saving.SingleImagePanel;
import gui.saving.SingleMarkingPanel;
import information.ID;
import information.ImageLayer;
import information.MarkingLayer;

public class ExImaSingleImagePanel extends SingleImagePanel{

	public ExImaSingleImagePanel(ImageLayer imageLayer, SaverDialog saverDialog) {
		super(imageLayer, saverDialog);
		
	}

	public ArrayList<Integer> getAllSelectedMarkingLayerIDs(){
		ArrayList<Integer> selectedMarkingLayerIDs=new ArrayList<Integer>();
		Component[] sMarkingList= markingTableJPanel.getComponents();
		if(sMarkingList != null && sMarkingList.length>0){
			for(int i=0;i<sMarkingList.length;i++){
				SingleMarkingPanel smp= (SingleMarkingPanel)sMarkingList[i];
				if(smp.isSelected()){
					selectedMarkingLayerIDs.add(smp.getMarkingLayer().getLayerID());
				}


			}

		}
		return selectedMarkingLayerIDs;

	}

	public ArrayList<MarkingLayer> getAllSelectedMarkingLayers(){
		ArrayList<MarkingLayer> selectedMarkingLayers=new ArrayList<MarkingLayer>();
		Component[] sMarkingList= markingTableJPanel.getComponents();
		if(sMarkingList != null && sMarkingList.length>0){
			for(int i=0;i<sMarkingList.length;i++){
				ExImaSingleMarkingPanel smp= (ExImaSingleMarkingPanel)sMarkingList[i];
				if(smp.isSelected()){
					selectedMarkingLayers.add(smp.getMarkingLayer());
				}
			}

		}
		return selectedMarkingLayers;

	}

	public void updateGridDrawing(){

		Component[] sMarkingList= markingTableJPanel.getComponents();
		if(sMarkingList != null && sMarkingList.length>0){
			for(int i=0;i<sMarkingList.length;i++){
				ExImaSingleMarkingPanel esmp= (ExImaSingleMarkingPanel)sMarkingList[i];
				if(esmp.isSelected()){
					if(esmp.isDrawCheckBoxSelected()){
						MarkingLayer ml = this.imageLayer.getMarkingLayer(esmp.getMarkingLayerID());
						if(ml != null){
							ml.setDrawGridToImage(true);
						}
					}
				}
			}

		}

	}



	protected String initProperFilePathForSaving(ImageLayer iLayer){
		if(iLayer != null){
				if(iLayer.getExportImagePath() != null && iLayer.getExportImagePath().length()>0)
					return iLayer.getExportImagePath();

				else if(iLayer.getImageFilePath() != null && iLayer.getImageFilePath().length()>0){
					return  iLayer.getFolderOfImage(); // just give folder

				}
		}
			return System.getProperty("user.home");

	}
	



	protected ExImaSingleMarkingPanel createSingleMarkingPanel(MarkingLayer markingLayer) throws Exception{
		return new ExImaSingleMarkingPanel(markingLayer);
	}

}
