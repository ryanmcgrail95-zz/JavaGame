package object.primitive;

import gfx.GL;

public abstract class Printable {

	protected void start(String action)	{GL.start(action);}
	protected void end(String action) 	{GL.end(action);}
	
	protected void print(Object str)	{GL.print(str);}
	protected void println(Object str)	{GL.println(str);}
	protected void println() 			{GL.println();}
}
