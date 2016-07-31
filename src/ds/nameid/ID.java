package ds.nameid;

public class ID<T> {
	private NameIDMap<T> 	parent;
	private int				idNum;
	private String 			name;
	
	
	public ID(NameIDMap<T> parent, int idNum, String name) {
		this.parent = parent;
		this.idNum = idNum;
		this.name = name;
	}
	
	
	public void refactor() {
		idNum = parent.refactorValue;
	}
	
	public int getIDNum() 	{return idNum;}
	public String getName() {return name;}
	public T get() 			{return parent.get(this);}
	
	
	public void destroy() {
		idNum = -2;		
		
		parent = null;
	}
	
	public String toString() {
		return "ID(" + name + ", " + idNum + "): " + get(); 
	}
}
