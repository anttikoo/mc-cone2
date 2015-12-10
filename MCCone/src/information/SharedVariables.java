package information;

import java.awt.AlphaComposite;

/**
 * The Class SharedVariables. Contains static variables.
 */
public class SharedVariables {
	public static final int IMAGESET_EXPORT_MAX_RESOLUTION = 5000;
	public static final int DISTANCE_TO_REMOVE = 20;
	public static final int DISTANCE_TO_ADD = 10;
	public static final int GLOBAL_MIN_CELL_DIAMETER = 6;
	public static final int GLOBAL_MIN_COORDINATE_NUMBER_IN_CELL = 4;
	public static final int MAX_GAP = 8;
	public static int operationSystem=ID.OS_WINDOWS;
	public static int transparencyModeIN = AlphaComposite.SRC_IN;
	public static int transparencyModeOVER = AlphaComposite.SRC_OVER;
	public static int transparencyModeOut= AlphaComposite.SRC_OUT;
	public static int transparencyModeATOP= AlphaComposite.SRC_ATOP;
	public static int usedDimmingMode=transparencyModeIN;

	/**
	 * Sets the transparencyIN.
	 *
	 * @param trm the new transparency in
	 */
	public static void setTransParencyIn(int trm){
		transparencyModeIN=trm;
	}

	/**
	 * Sets the transparency over.
	 *
	 * @param trm the new transparency over
	 */
	public static void setTransParencyOver(int trm){
		transparencyModeOVER=trm;
	}
	
	/**
	 * Sets the used dimming mode to srcOver.
	 */
	public static void setUsedDimmingModeToSrcOver(){
		usedDimmingMode=transparencyModeOVER;
	}
	
	/**
	 * Sets the used dimming mode to srcIn.
	 */
	public static void setUsedDimmingModeToSrcIn(){
		usedDimmingMode=transparencyModeIN;
	}
	
	/**
	 * Sets the used dimming mode to srcOut.
	 */
	public static void setUsedDimmingModeToSrcOut(){
		usedDimmingMode=transparencyModeOut;
	}
	
	/**
	 * Sets the used dimming mode to srcAtop.
	 */
	public static void setUsedDimmingModeToSrcAtop(){
		usedDimmingMode=transparencyModeATOP;
	}
	
	/**
	 * Sets the ID of operation system.
	 *
	 * @param osID the new ID of operation system
	 */
	public static void setOS(int osID){
		operationSystem=osID;
	}
}
