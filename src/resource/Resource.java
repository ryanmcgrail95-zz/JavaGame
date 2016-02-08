package resource;

import ds.lst.CleanList;
import fl.FileExt;

public abstract class Resource {
	public final static byte
		R_TEXTURE = 0,
		R_SOUND = 1,
		R_MODEL = 2,
		R_CHARACTER = 3;
	private String name, fileName;
	private byte type;
	private long byteNum;
	private boolean isLoaded = false, isTemporary = false;
	private int numReferences = 0;
	
	private static CleanList<Resource> resourceList = new CleanList<Resource>("Resource");
	
	public Resource(String fileName, byte type, boolean isTemporary) {
		this.name = removeType(FileExt.getFile(fileName).getName());
		this.fileName = fileName;
		this.type = type;
		this.isTemporary = isTemporary;
		
		if(isTemporary)
			load();
	}
	
	public void load() {
		if(!isLoaded) {
			load(fileName);
			isLoaded = true;
			
			resourceList.add(this);
		}
	}
	
	public boolean isLoaded() {
		return isLoaded;
	}
	
	public void addReference() {
		numReferences++;
	}
	public void removeReference() {
		numReferences--;
		
		if(isTemporary)
			if(numReferences <= 0)
				unload();
	}
	
	public static String removeType(String fileName) {
		int pos = fileName.indexOf('.');
		if(pos != -1)
			return fileName.substring(0,pos);
		else
			return fileName;
	}
	
	public abstract void load(String fileName);
	public void unload() {
		resourceList.remove(this);
	}
	
	public int getReferences() {
		return numReferences;
	}

	public String getName() 	{return name;}
	public String getFileName() {return fileName;}

	public boolean getTemporary() {
		return isTemporary;
	}
	
	public static CleanList<Resource> getList() {
		return resourceList;
	}
}
