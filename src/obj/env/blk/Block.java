package obj.env.blk;

import com.jogamp.opengl.util.texture.Texture;
import cont.TextureController;
import object.primitive.Environmental;

public abstract class Block extends Environmental {
	protected final static Texture shadowTex = TextureController.getTexture("texBlockShadow");
	
	public Block(float x, float y, float z) {
		super(x,y,z,false,false);
	}
}
