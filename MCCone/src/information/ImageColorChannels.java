package information;

public class ImageColorChannels {
	private int[][] alpha;
	private int[][] red;
	private int[][] green;
	private int[][] blue;
	private int width;
	private int height;
	private boolean useAlpha=false;
	
	public ImageColorChannels(int h, int w){
		this.setAlpha(new int[h][w]);
		this.setRed(new int[h][w]);
		this.setGreen(new int[h][w]);
		this.setBlue(new int[h][w]);	
		setWidth(w);
		setHeight(h);
	}
	
	public void addAlpha(int r, int c, int value) throws Exception {
		
			this.alpha[r][c]=value;
		
		
	}
	public void addRed(int r, int c, int value ) throws Exception{
		this.red[r][c]=value;
	}
	public void addGreen(int r, int c, int value){
		this.green[r][c]=value;
	}
	public void addBlue(int r, int c, int value){
		this.blue[r][c]=value;
	}


	public int[][] getAlpha() {
		return alpha;
	}
	
	public int getAlpha(int r, int c) {
		return alpha[r][c];
	}


	public void setAlpha(int[][] alpha) {
		this.alpha = alpha;
	}


	public int[][] getRed() {
		return red;
	}
	
	public int getRed(int r, int c){
		return red[r][c];
	}


	public void setRed(int[][] red) {
		this.red = red;
	}


	public int[][] getGreen() {
		return green;
	}
	
	public int getGreen(int r, int c) {
		return green[r][c];
	}


	public void setGreen(int[][] green) {
		this.green = green;
	}


	public int[][] getBlue() {
		return blue;
	}
	
	public int getBlue(int r, int c) {
		return blue[r][c];
	}


	public void setBlue(int[][] blue) {
		this.blue = blue;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public boolean useAlpha() {
		return useAlpha;
	}

	public void setUseAlpha(boolean useAlpha) {
		this.useAlpha = useAlpha;
	}

	
}
