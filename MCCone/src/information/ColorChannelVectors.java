package information;

public class ColorChannelVectors {
	private int[] alpha_original;
	private int[] red_original;
	private int[] green_original;
	private int[] blue_original;
	private int[] fullColorInt_original;
	
	

	private double[] alpha_ma;
	private double[] red_ma;
	private double[] green_ma;
	private double[] blue_ma;
	private int size;
	private boolean hasAlpha=false;
	




	public ColorChannelVectors(int size){
		
		
	//	this.alpha_ma = new double[size];
	//	this.red_ma = new double[size];
		
	//	this.green_ma = new double[size];
	//	this.blue_ma = new double[size];
		this.size=size;
		
	}
	
	
	
	public void setMoveAveragedData(double[] a, double[] r, double[] g, double[] b){	
		this.alpha_ma=a;
		this.red_ma=r;
		this.green_ma=g;
		this.blue_ma=b;
	}
	
	public void setOriginalData(int[] a, int[] r, int[] g, int[] b) throws Exception{	
		this.alpha_original=a;
		this.red_original=r;
		this.green_original=g;
		this.blue_original=b;
		
		setOriginalDataAsSingleInt();
	}
	
	public void setSingleDataPoint(double a, double r, double g, double b, int i){	
		this.alpha_ma[i]=a;
		this.red_ma[i]=r;
		this.green_ma[i]=g;
		this.blue_ma[i]=b;
	}
	
	public void setSingleDataPoint(double r, double g, double b, int i){	
		
		this.red_ma[i]=r;
		this.green_ma[i]=g;
		this.blue_ma[i]=b;
	}

	public int getSize() {
		return size;
	}
	
	public double getAlphaAt(int x){
		return this.alpha_ma[x];
	}
	public double getRedAt(int x){
		return this.red_ma[x];
	}
	public double getGreenAt(int x){
		return this.green_ma[x];
	}
	public double getBlueAt(int x){
		return this.blue_ma[x];
	}

	public void setSize(int size) {
		this.size = size;
	}
	
	public int getFullColorInt_original(int index) {
		return fullColorInt_original[index];
	}



	public void setFullColorInt_original(int[] fullColorInt_original) {
		this.fullColorInt_original = fullColorInt_original;
	}
	
	public double getKvalueAt(int start_x, int end_x){
		
		if(start_x < end_x && start_x >=0 && end_x <this.size){
			
			double redK = (red_ma[end_x]-red_ma[start_x])/(end_x-start_x);
			if(redK<0)
				redK *=-1;
			double greenK = (green_ma[end_x]-green_ma[start_x])/(end_x-start_x);
			if(greenK<0)
				greenK *=-1;
			double blueK = (blue_ma[end_x]-blue_ma[start_x])/(end_x-start_x);
			if(blueK<0)
				blueK *=-1;
			return (redK+greenK + blueK);
			
		}
		else 
			return 0;
		
	}
	
	public boolean hasAlpha() {
		return hasAlpha;
	}



	public void setHasAlpha(boolean hasAlpha) {
		this.hasAlpha = hasAlpha;
	}
	
	private void setOriginalDataAsSingleInt() throws Exception{
		this.fullColorInt_original=new int[this.red_original.length];
		int argb=0;
		if(!hasAlpha){
	//		argb += -16777216; // 255 alpha
		}
		/*			
		for (int i = 0; i < fullColorInt_original.length; i++) {
			argb= ((255 & 0xFF) << 24) | //alpha
		            (((int) red_original[i] & 0xFF) << 16) | //red
		            (((int) green_original[i] & 0xFF) << 8)  | //green
		            (((int) blue_original[i] & 0xFF) << 0); //blue
			
			fullColorInt_original[i]=argb;
		}
		*/
			for (int i = 0; i < fullColorInt_original.length; i++) {
			//	 int argb =  (255 << 24) | (r << 16)| (g << 8) | b;
			// argb = ((red_original[i]&0x0ff)<<16)|((green_original[i]&0x0ff)<<8)|( blue_original[i]&0x0ff);
			 argb =  (255 << 24) | (red_original[i] << 16) | ( green_original[i] << 8) | blue_original[i];
			// int r=(argb >> 16) & 0xFF;
			// int g=(argb >> 8) & 0xFF;
			// int b =argb & 0xFF;
		//	 System.out.println("red "+ red_original[i] +" "+r);
		//	 System.out.println("green "+ green_original[i] +" "+g);
		//	 System.out.println("blue "+ blue_original[i] +" "+b);
			fullColorInt_original[i]=argb;
		}
		
			
		/*
		argb += (((int) alpha_original[i] & 0xff) << 24); // alpha
        argb += ((int) blue_original[i] & 0xff); // blue
        argb += (((int) green_original[i] & 0xff) << 8); // green
        argb += (((int) red_original[i] & 0xff) << 16); // red
       */
		
		
	}
	
	
	
}
