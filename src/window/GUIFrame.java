package window;

import gfx.GOGL;
import gfx.RGBA;

import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.util.awt.TextureRenderer;
import com.jogamp.opengl.util.texture.Texture;

public class GUIFrame extends GUIDrawable {
	private TextureRenderer renderTex;
	private List<GUIDrawable> drawList;
	private List<GUIObject> nondrawList;
	
	public GUIFrame(float x, float y, float w, float h) {
		super(x, y, w, h);
		
//		//renderTex = new TextureRenderer((int)w,(int)h,true,false);
		
		drawList = new ArrayList<GUIDrawable>();
		nondrawList = new ArrayList<GUIObject>();
	}
	
	public void destroy() {
		drawList.clear();
		nondrawList.clear();
		//super.destroy();
		//renderTex.dispose();
	}
	
	public void add(GUIObject obj) {
		obj.setParent(this);
		if(obj.getDrawable())
			drawList.add((GUIDrawable) obj);
		else
			nondrawList.add(obj);
	}
	
	public void render() {
		//renderTex.beginOrthoRendering(640,480);
		/*for(GUIDrawable g : drawList)
			g.draw();*/
		//renderTex.endOrthoRendering();
	}
	
	public void draw() {
		draw(x(),y());
	}
	public void draw(float x, float y) {
		for(GUIDrawable g : drawList)
			g.draw(x,y);
		//GOGL.drawTexture(x(),y(),renderTex.getTexture());
	}
}
