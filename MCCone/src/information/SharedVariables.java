package information;

import java.awt.AlphaComposite;

public class SharedVariables {
	public static final int IMAGESET_EXPORT_MAX_RESOLUTION = 5000;
	public static final int DISTANCE_TO_REMOVE = 20;
	public static final int DISTANCE_TO_ADD = 10;
	public static int operationSystem=ID.OS_WINDOWS;
	public static int transparencyModeIN = AlphaComposite.SRC_IN;
	public static int transparencyModeOVER = AlphaComposite.SRC_OVER;
	public static int transparencyModeOut= AlphaComposite.SRC_OUT;
	public static int transparencyModeATOP= AlphaComposite.SRC_ATOP;

	public static int usedDimmingMode=transparencyModeIN;


	public static void setTransParencyIn(int trm){
		transparencyModeIN=trm;
	}

	public static void setTransParencyOver(int trm){
		transparencyModeOVER=trm;
	}
	
	public static void setUsedDimmingModeToSrcOver(){
		usedDimmingMode=transparencyModeOVER;
	}
	
	public static void setUsedDimmingModeToSrcIn(){
		usedDimmingMode=transparencyModeIN;
	}
	
	public static void setUsedDimmingModeToSrcOut(){
		usedDimmingMode=transparencyModeOut;
	}
	
	public static void setUsedDimmingModeToSrcAtop(){
		usedDimmingMode=transparencyModeATOP;
	}
	
	public static void setOS(int osID){
		operationSystem=osID;
	}
}
