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
		sprite = new FBO(GL.gl,w,h);
	}
	
	public static void ini() {
		instance = new FireSprite();
	}
	
	public static FBO getFBO() {
		return instance.sprite;
	}
	
	
	public void render() {
		GL2 gl = GL.gl;
				
		sprite.attach(gl);
		
			GL.clear(RGBA.TRANSPARENT);
		
			GL.setColor(RGBA.WHITE);

			GL.enableShader("FireSprite");
			GL.bind(fireAni, 0);
			G2D.setTextureRepeat(true);
			GL.bind(fireMask, 1);
			
			G2D.fillRectangle(0,0,w,h);
			
			GL.unbind(1);
			GL.unbind(0);
			GL.disableShaders();
		sprite.detach(gl);
	}
	
	public void add() {}

	@Override
	public void draw() {
		// TODO Auto-generated method stub
		
	}
}
