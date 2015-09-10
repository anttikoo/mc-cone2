package information;

public class PathCount {
	private int count;
	private String folderPath;
	public PathCount(String folderPath){
		this.setCount(1);
		this.setFolderPath(folderPath);
	}
	public String getFolderPath() {
		return folderPath;
	}
	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}
	public int getCount() {
		return count;
	}
	
	private void setCount(int count) {
		this.count = count;
	}
	
	public void addOne(){
		this.count++;
	}


}
