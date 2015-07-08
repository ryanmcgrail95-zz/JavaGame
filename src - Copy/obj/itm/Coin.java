package obj.itm;

import cont.ItemController;
import cont.TextureController;
import obj.chr.Player;
import obj.prim.Physical;
import sts.Stat;
import func.Math2D;
import gfx.TextureExt;

public class Coin extends Item {
	private static TextureExt coinTex = TextureController.getTextureExt("texCoin");
	private float imageIndex = 0;
	
	public Coin(float x, float y, float z) {
		super("Coin", x,y,z);
		
		size = 4;
		
		float s = 32;
		//shape.addSprite(0,0,s*.3f, s,s, coinTex.getFrame(0));
		//shape.setScale(.5f);
	}
	
	
	public void update(float deltaT) {		
		super.update(deltaT);
		
	    /*float pDir;
	    pDir = point_direction(x,y,parPlayer.x,parPlayer.y);
	    x += lengthdir_x(.2,pDir);
	    y += lengthdir_y(.2,pDir);*/
		
		imageIndex += .5f*deltaT;
		//shape.setTexture(coinTex.getFrame(imageIndex));
	}
	
	public void use() {
	}
	
	public boolean collide(Physical p) {
		Player pl;
		
		if(p.getClass() == Player.class) {
			pl = (Player) p;
			
			if(collideSize(p))
				if(p.getZ() < z+16 && p.getZ()+10 > z-2) {
				    //pl.getStat().addCoin();
				    destroy();
				    
				    //sound_play(sndCoin);
				    return true;
				}
		}
		
		return false;
	}
}
