package gfx;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;

import cont.TextureController;
import object.primitive.Drawable;

public class FireSprite extends Drawable {
	private FBO sprite;
	private static FireSprite instance;
	private static Texture fireAni, fireMask;
	static {
		fireAni = TextureController.getTexture("fireAni");
		fireMask = TextureController.getTexture("fireMask");
	}
	private static int w = 32, h = 64;
	
	private FireSprite() {
		super(false,true);
		sprite = new FBO(GOGL.gl,w,h);
	}
	
	public static void ini() {
		instance = new FireSprite();
	}
	
	public static FBO getFBO() {
		return instance.sprite;
	}
	
	
	public void render() {
		GL2 gl = GOGL.gl;
				
		sprite.attach(gl);
		
			GOGL.clear(new RGBA(0,0,0,0));
		
			GOGL.setColor(RGBA.WHITE);

			GOGL.enableShader("FireSprite");
			GOGL.bind(fireAni, 0);
			GOGL.enableTextureRepeat();
			GOGL.bind(fireMask, 1);
			
			GOGL.fillRectangle(0,0,w,h);
			
			GOGL.unbind(1);
			GOGL.unbind(0);
			GOGL.disableShaders();
		sprite.detach(gl);
	}
	
	public void add() {}

	@Override
	public void draw() {
		// TODO Auto-generated method stub
		
	}
}
