package btl;

import cont.TextureController;
import object.primitive.Drawable;
import time.Delta;
import functions.Math2D;
import functions.MathExt;
import gfx.GOGL;
import gfx.MultiTexture;
import gfx.RGBA;

public class Fireball extends Drawable {
	private static final MultiTexture tex = new MultiTexture("Resources/Images/explosion.png",8,1);
	private static final MultiTexture texBlur = new MultiTexture("Resources/Images/explosionBlur.png",8,1);	
	private float x, y, z, sc, scB, index, pauseTimer = 0, fadeTimer = 0, pauseTime = 10, fadePoint = 20;
	private boolean isFading = false;
	private float size = 64;
	
	private boolean element;
	private final static boolean
		EL_FIRE = true,
		EL_ICYFIRE = false;
	
	public Fireball(float x, float z, boolean isHot) {
		super(false, false);
		this.x = x;
		this.y = -8;
		this.z = z;
		this.sc = 1;
		index = 0;
		
		element = isHot;
	}	
	
	public float calcIndex() {
		int i = (int) Math.floor(index);
		
		float ind = .23f;
		return ind;
		
		/*if(i == 2)
			ind = .1f;
		else if(i >= 6)
			ind = .1f;
						
		return ind;*/
	}
	
	public void update() {
		int i = (int) Math.floor(index);
		float scF = Math.min(1f*i/8, 1);

		sc = (1-scF)*.5f + 2.5f*scF;
		
		float midInd = 3;
		if(i == 0 || i > midInd)
			scB = sc;
		else if(i == 1)
			scB = 2*sc;
		else if(i == 2)
			scB = 1.4f*sc;
		
		float prevInd = index;
		//if(pauseTimer == 0 && fadeTimer == 0)
			index += Delta.convert(calcIndex());
		/*else if(pauseTimer > 0)
			pauseTimer--;
		else if(fadeTimer > 0)
			fadeTimer--;
		
		float bigExp = 2, fadeExp = 8;
		if(prevInd < bigExp && bigExp <= index)
			pauseTimer = pauseTime;
		if(prevInd < fadeExp && fadeExp <= index) {
			isFading = true;
			fadeTimer = fadePoint*2;
		}*/
		
		if(index > 8)
			destroy();
	}
	
	public void draw() {
		GOGL.transformClear();
			GOGL.transformTranslation(x,y,z);
			GOGL.transformPaper();

			float dSize = sc*size, dBSize = scB*size;
			
			float f = 1f; //1.4f
			if(index < 6)
				GOGL.setColor(element ? RGBA.RED : RGBA.BLUE);
			else {
				float frac = Math.min((index - 6)/2, 1);
				GOGL.setColor(RGBA.interpolate(element ? RGBA.RED : RGBA.BLUE, RGBA.BLACK, frac));
			}

			GOGL.enableShader("FireballGaussian");
				float[] bounds = tex.getBounds((int) index);
				GOGL.bind(texBlur.getTexture());
					GOGL.enableInterpolation();
					GOGL.fillRectangle(-dBSize/2,-dBSize/2,dBSize,dBSize, bounds);
				GOGL.unbind();
			GOGL.disableShaders();
			
			GOGL.transformTranslation(0,0, 1);
			if(index < 6) {
				GOGL.enableShader("Fireball");
				
				if(element)
					GOGL.passShaderVec4("outsideColor", new float[] {1,1,0,1});
				else
					GOGL.passShaderVec4("outsideColor", new float[] {.5f,.5f,1,1});

				GOGL.passShaderVec4("insideColor", new float[] {1,1,1,1});
					
				GOGL.bind(tex.getTexture());
					GOGL.enableInterpolation();
					GOGL.fillRectangle(-dSize/2,-dSize/2,dSize,dSize, bounds);
				GOGL.unbind();
				GOGL.disableShaders();
			}
			
			GOGL.resetColor();
			GOGL.disableInterpolation();
		GOGL.transformClear();
	}

	public float calcDepth() {
		return 20;
	}
	@Override
	public void add() {
		// TODO Auto-generated method stub
		
	}
}
