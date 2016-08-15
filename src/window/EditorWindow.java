package window;

import gfx.G2D;
import gfx.GL;
import gfx.RGBA;
import gfx.Sprite;
import io.Mouse;

public class EditorWindow extends Window {
	private GUIEditor editorObj;
	
	public EditorWindow(String title, int x, int y, int width, int height, boolean canDrag, GUIEditor editorObj) {
		super(title, x,y, width,height, canDrag);
		this.editorObj = editorObj;
	}
	
	@Override
	public void drawBorder() {
		//TODO: Prevent icons from moving when window dragged.
		super.drawBorder();
		
		float bX,bY,bS;
		bS = 12;
		bX = x()+SIDE_BORDER;
		bY = y()+TOP_BORDER/2-bS/2;
				
		// New File
		if(drawButton(bX,bY,bS,bS, buttons,6))
			editorObj.newFile();
		
		bX += bS;
		if(drawButton(bX,bY,bS,bS, buttons,1))
			editorObj.save();
		
		bX += bS;
		if(drawButton(bX,bY,bS,bS, buttons,2))
			editorObj.open();

		bX += bS*1.5;
		if(drawButton(bX,bY,bS,bS, buttons,3))
			editorObj.copy();
		
		bX += bS*1;
		if(drawButton(bX,bY,bS,bS, buttons,4))
			editorObj.paste();
		
		bX += bS*1.5;
		if(drawButton(bX,bY,bS,bS, buttons,9))
			editorObj.run();
	}
}
