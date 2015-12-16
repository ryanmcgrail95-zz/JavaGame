package resource;

public abstract class Resource {
	public final static byte
		R_TEXTURE = 0,
		R_SOUND = 1,
		R_MODEL = 2,
		R_CHARACTER = 3;
	private String name, fileName;
	private byte type;
	private long byteNum;
	
	public Resource(String fileName, byte type) {
		this.name = removeType(fileName);
		this.fileName = fileName;
		this.type = type;
	}
	
	public void load() {
		load(fileName);
	}
	
	public String removeType(String fileName) {
		int pos = fileName.indexOf('.');
		if(pos != -1)
			return fileName.substring(0,pos);
		else
			return fileName;
	}
	
	public abstract void load(String fileName);
	public abstract void unload();

	public String getName() 	{return name;}
	public String getFileName() {return fileName;}
}
