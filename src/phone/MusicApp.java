package phone;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.jogamp.opengl.util.texture.Texture;

import cont.TextureController;
import datatypes.vec2;
import io.Mouse;
import fl.FileExt;
import functions.Math2D;
import functions.MathExt;
import gfx.FBO;
import gfx.GLText;
import gfx.GOGL;
import gfx.RGBA;
import resource.sound.Playlist;
import resource.sound.Sound;
import resource.sound.SoundBuffer;
import resource.sound.SoundSource;

public final class MusicApp extends PhoneApp {
	
	private float speed = 1, toSpeed = 1, prevSpeed;
	private Playlist playlist;
	private FBO waveform;
	private SoundBuffer prevParent;
	private SoundSource source;
	private boolean isLooping = false, isScrubbing = false, hasSnapped;
	private int padding = 12;
	float 	textHeight = 10,
			textScale = textHeight/8;
	float 	infoPadding = 4,
			infoBGX = SmartPhone.WIDTH/2+padding,		// Infobox Background Location
			infoBGY = padding,
			infoBGW = SmartPhone.WIDTH/2-2*padding,
			infoBGH = 12+textHeight+2*infoPadding;
	float 	albS = 32,
			albX = infoBGX+12,
			albY = infoBGY+infoBGH/2 - albS/2;
	float 	infoX = albX+albS+padding,			// Info Location
			infoY = infoBGY + infoPadding;
	int 	barX = SmartPhone.WIDTH/2+padding,
			barW = SmartPhone.WIDTH/2-2*padding,
			barH = 16,			
			barY = SmartPhone.HEIGHT/2 - barH - padding,
			buttonY = barY - 24;
	float 	wfX = SmartPhone.WIDTH/2 + padding, //infoBGX+infoBGW+padding,				// Waveform Location
			wfW = SmartPhone.WIDTH - wfX - padding,
			wfH = 24,
			wfY = infoBGY+infoBGH+wfH/2;
	
	private ListDrawer holdDrawer;
	private int holdIndex = -1;

	
	private ListDrawer plDrawer, mlDrawer;
	
	public MusicApp(SmartPhone owner) {
		super(owner);
		playlist = new Playlist("Ace Attorney");//,"damonGant","godot","courtBegins");
		playlist.play();
		source = playlist.getSource();
		
		plDrawer = new PlaylistDrawer(SmartPhone.WIDTH/2, SmartPhone.HEIGHT/2);
		mlDrawer = new MusicListDrawer(0,0);

		
		waveform = new FBO(GOGL.gl,barW,barH);
		
		buttonList.add(new PlayButton(SmartPhone.WIDTH*3/4,buttonY,16));
		buttonList.add(new FastForwardButton(SmartPhone.WIDTH*3/4+32,buttonY,16));
		buttonList.add(new RewindButton(SmartPhone.WIDTH*3/4-32,buttonY,16));
		buttonList.add(new NextButton(SmartPhone.WIDTH*3/4+64,buttonY,16));
		buttonList.add(new PreviousButton(SmartPhone.WIDTH*3/4-64,buttonY,16));
		buttonList.add(new LoopButton(SmartPhone.WIDTH*3/4+3*32,buttonY,16));
	}
	
	
	public void render() {
		waveform.attach(GOGL.gl);
		GOGL.clear(new RGBA(0,0,0,0));
		
		GOGL.setColor(RGBA.BLACK);
		GOGL.drawWaveform(0,barH/2, barW,barH,   1,playlist.getSource().getParentBuffer().getByteLen(),   50,10, playlist.getSource().getParentBuffer());
		GOGL.resetColor();
		
		waveform.detach(GOGL.gl);
	}
	public void draw() {
		GLText.setFont("8bit");
		
		hasSnapped = false;
		
		speed += (toSpeed - speed)/5;
		if(Math.abs(speed-toSpeed) < .1)
			speed = toSpeed;
		if(speed == 0)
			source.pause();
		else
			source.play();
		source.setSpeed(speed);
		
		
		
		if(source.getParentBuffer() != prevParent) {
			render();
			prevParent = source.getParentBuffer();
		}
		
		parent.beginRendering();
		GOGL.clear(RGBA.WHITE);
		
		//drawBG();
		GOGL.fillVGradientRectangle(SmartPhone.WIDTH/2,0,SmartPhone.WIDTH/2,SmartPhone.HEIGHT/2, RGBA.CYAN, RGBA.WHITE, 10);

		float waveH = 64;				
		float shX,shY,shAmt;

		float curFrac = source.isReversed() ? 1 - source.getOffsetFraction() : source.getOffsetFraction(), possFrac = -1;
		if(hoverRectangle(0,0,SmartPhone.WIDTH,SmartPhone.HEIGHT)) {
			if(hoverRectangle(barX,barY,barW,barH)) {
				possFrac = (getMouseCoords().x() - barX)/barW;
				if(Mouse.getLeftClick()) {
					isScrubbing = true;
					prevSpeed = toSpeed;
					speed = toSpeed = 0;
				}
				if(Mouse.getLeftMouse() && isScrubbing) {
					source.setOffsetFraction(possFrac);
				}
			}
		}
		if(isScrubbing)
			if(!Mouse.getLeftMouse()) {
				isScrubbing = false;
				speed = toSpeed = prevSpeed;
			}
		
		if(speed == toSpeed) {
			shX = 0;
			shY = 0;
		}
		else {
			shAmt = 2;
			shX = MathExt.rnd(-shAmt,shAmt);
			shY = MathExt.rnd(-shAmt,shAmt);
		}
		
		float barDX = barX+shX, barDY = barY+shY;
		GOGL.drawFillBar(barDX,barDY,barW,barH, new RGBA(.3f,.3f,.3f,.3f), possFrac, RGBA.CYAN, curFrac);
		GOGL.drawFBO(barDX,barDY, waveform);
		
		
		
		GOGL.setColor(RGBA.BLACK);		
		checkButtons();
		
			
		playlist.setLoop(isLooping);
		playlist.setVolume(parent.volumeFrac);

		
		// DRAW INFO OVERLAY
			// Infobox Background
			GOGL.fillVGradientRectangle(infoBGX,infoBGY, infoBGW,infoBGH, RGBA.GRAY_DARK, RGBA.BLACK, 5);
			
			float albFY = Math2D.calcLenY(5,GOGL.getTime());
			
			
			// DRAW WAVEFORM AT CURRENT OFFSET
			int s = 10000, start, end;
			start = source.getByteOffset()-s;
			end = source.getByteOffset()+s;
			GOGL.setColor(RGBA.BLACK);
			GOGL.drawWaveform(wfX, wfY, wfW, wfH,   start, end,  10,0, source.getParentBuffer(), true);
			
			
			// Album Shadow
			/*GOGL.setColor(RGBA.BLACK);
			GOGL.setAlpha(.5f);
			GOGL.fillRectangle(albX+10,albY+albFY+5, albS,albS);*/
			
			// Album
			GOGL.setColor(RGBA.WHITE);
			Texture tex = source.getParentBuffer().getAlbumImage();		
			if(tex != null)
				GOGL.drawTexture(albX,albY+albFY, albS,albS, tex);
	
			// Info
			GLText.drawString(infoX, infoY, textScale,textScale, source.getParentBuffer().getName());
			GLText.drawString(infoX, infoY+textHeight, textScale,textScale, source.getParentBuffer().getAlbumName());



		GOGL.resetColor();
		mlDrawer.draw();
		plDrawer.draw();
		
		if(!Mouse.getLeftMouse()) {
			if(holdIndex != -1 && !hasSnapped) {
				holdDrawer.list.remove(holdIndex);
				if(holdIndex == playlist.getIndex())
					playlist.play(holdIndex);
				holdIndex = -1;
			}
		}
		else if(holdIndex != -1 && !hasSnapped) {
			vec2 mousePos = getMouseCoords();
			float mX,mY;
			mX = mousePos.x();
			mY = mousePos.y();
			holdDrawer.draw(mX,mY, holdIndex, false);
			GOGL.setColor(A_BLACK);
			GOGL.fillRectangle(mX,mY,SmartPhone.WIDTH/2,20);
		}
		GOGL.resetColor();
		
		parent.endRendering();		
		
		GLText.setFont("OoT");
	}
	
	
	private class PlayButton extends CircleButton {
		
		public PlayButton(float x, float y, float r) {
			super(x, y, r);
		}
		
		public boolean click() {
			boolean state = super.click();
			if(state)
				toSpeed = (toSpeed == 0) ? 1 : 0;
			return state;
		}

		public void draw() {
			float dX = getX(), dY = getY() + Math2D.calcLenY(1,wobbleDir);
			if(speed == 0)									//Draw Play Button
				GOGL.fillPolygon(dX-4,dY, 16, 3);
			else {
				GOGL.fillRectangle(dX-12,dY-12, 8,24);
				GOGL.fillRectangle(dX+4,dY-12, 8,24);
			}
		}
	}
	
	private class FastForwardButton extends CircleButton {
		
		public FastForwardButton(float x, float y, float r) {
			super(x, y, r);
		}
		public boolean click() {
			boolean state = super.click();
			if(state)
				toSpeed = (toSpeed == 0 || toSpeed == -1) ? 1 : toSpeed+1;
			return state;
		}

		public void draw() {
			float dX = getX(), dY = getY(), shAmt;
			if(speed > 1) {
				shAmt = speed;
				dX += MathExt.rnd(-shAmt,shAmt);
				dY += MathExt.rnd(-shAmt,shAmt);
				
				if(toSpeed > 1)
					GLText.drawString(dX+8,dY-16, ""+(int) (toSpeed));
			}
			dY += Math2D.calcLenY(1,wobbleDir);
			GOGL.fillPolygon(dX-5,dY, 8, 3);			//Draw Fast Forward Button
			GOGL.fillPolygon(dX+5,dY, 8, 3);
		}
	}
	
	private class RewindButton extends CircleButton {
		public RewindButton(float x, float y, float r) {
			super(x, y, r);
		}
		

		public boolean click() {
			boolean state = super.click();
			if(state)
				toSpeed = (toSpeed == 0 || toSpeed == 1) ? -1 : toSpeed-1;
			return state;
		}

		public void draw() {
			float dX = getX(), dY = getY(), shAmt;
			if(speed < 0) {
				shAmt = speed;
				dX += MathExt.rnd(-shAmt,shAmt);
				dY += MathExt.rnd(-shAmt,shAmt);

				if(toSpeed < -1)
					GLText.drawString(dX+8,dY-16, ""+(int) ((toSpeed)/-1));
			}
			dY += Math2D.calcLenY(1,wobbleDir);
			GOGL.fillPolygon(dX-5,dY, 8, 3, 180);			//Draw Fast Forward Button
			GOGL.fillPolygon(dX+5,dY, 8, 3, 180);
		}
	}
	
	private class NextButton extends CircleButton {
		public NextButton(float x, float y, float r) {
			super(x, y, r);
		}
		
		public boolean click() {
			boolean state = super.click();
			if(state)
				playlist.playNextSong();
			return state;
		}
		public void draw() {
			float dX = getX(), dY = getY();
			
			GLText.drawStringCentered(dX,dY-20, .5f,.5f, playlist.getNextName());
			
			dY += Math2D.calcLenY(1,wobbleDir);
			GOGL.fillPolygon(dX-5,dY, 8, 3);
			GOGL.fillRectangle(dX+5,dY-8, 4, 16);
		}
	}
	
	private class PreviousButton extends CircleButton {
		public PreviousButton(float x, float y, float r) {
			super(x, y, r);
		}
		
		public boolean click() {
			boolean state = super.click();
			if(state)
				playlist.playPreviousSong();
			return state;
		}
		public void draw() {
			float dX = getX(), dY = getY();

			GLText.drawStringCentered(dX,dY-20, .5f,.5f, playlist.getPreviousName());

			dY += Math2D.calcLenY(1,wobbleDir);
			GOGL.fillPolygon(dX+5,dY, 8, 3, 180);
			GOGL.fillRectangle(dX-9,dY-8, 4, 16);
		}
	}
	
	private class LoopButton extends CircleButton {
		public LoopButton(float x, float y, float r) {
			super(x, y, r);
		}
		
		public boolean click() {
			boolean state = super.click();
			if(state)
				isLooping = !isLooping;
			return state;
		}
		public void draw() {
			float dX = getX(), dY = getY()+Math2D.calcLenY(1,wobbleDir), w = 10;
			
			if(!isLooping)
				GOGL.forceColor(RGBA.GRAY);
			GOGL.drawTexture(dX-w,dY-w,2*w,2*w, TextureController.getTexture("loop"));
			GOGL.unforceColor();
		}
	}
	
	protected abstract class ListDrawer<T> {
		protected float x, y, w, h;
		protected List<T> list;
		
		public ListDrawer(float x, float y, float w, float h, List<T> list) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			this.list = list;
		}
		
		public void draw() {
			float dX = x, dY = y;
			for(int i = 0; i < list.size(); i++)
				dY += draw(dX,dY,i,true);
		}
		
		public boolean hover() {
			return hoverRectangle(x,y,w-1,h);
		}
		protected abstract float draw(float x, float y, int index, boolean inList);
	}

	protected final static RGBA A_WHITE = new RGBA(1,1,1,.2f),
			A_BLACK = new RGBA(0,0,0,.5f),
			A_YELLOW = new RGBA(1,1,0,.5f),
			A_CYAN = new RGBA(0,1,1,.5f),
			CUR_RGBA = new RGBA(1,0,0,.2f);
	private abstract class MusicDrawer extends ListDrawer<String> {
		private RGBA bg, fg;
		private int checkIndex = 0;
		
		public MusicDrawer(float x, float y, float w, float h, RGBA bg, RGBA fg, List<String> list) {
			super(x, y, w, h, list);
			this.bg = bg;
			this.fg = fg;
		}
		
		public void draw() {
			checkIndex = 0;
			GOGL.setColor(bg);
			GOGL.fillRectangle(x, y, w, h);
			super.draw();
		}
			
		public float draw(float dX, float dY, int index, boolean inList) {
			SoundBuffer bf = Sound.get(list.get(index));
			float dP = 2, dS = 20, aY = 0;

						
			GOGL.fillVGradientRectangle(dX,dY, w,dS, fg, bg, 2);

			GOGL.setColor(RGBA.WHITE);
			GOGL.drawTexture(dX+dP,dY,  dS,dS, bf.getAlbumImage());
			GLText.drawString(dX+dP+dS+dP,dY, bf.getName());
			
			checkIndex++;			
			
			return dS+aY;
		}
	}
		
	private class MusicListDrawer extends MusicDrawer {
		public MusicListDrawer(float x, float y) {
			super(x, y, SmartPhone.WIDTH/2, SmartPhone.HEIGHT, RGBA.GRAY, RGBA.GRAY_LIGHT, Sound.getMusicList());
		}
			
		public float draw(float dX, float dY, int index, boolean inList) {
			float dH = super.draw(dX,dY,index,inList);
			
			if(!inList || holdIndex != -1)
				return dH;
			if(hoverRectangle(dX,dY,w,dH-1)) {
				GOGL.setColor(A_WHITE);
				Mouse.setFingerCursor();
				GOGL.fillRectangle(dX,dY,w,dH);
				
				if(index != playlist.getIndex())
					if(Mouse.getLeftClick())
						playlist.add(list.get(index));
			}
			
			return dH;
		}		
	}

	private class PlaylistDrawer extends MusicDrawer {
		
		public PlaylistDrawer(float x, float y) {
			super(x, y, SmartPhone.WIDTH/2, SmartPhone.HEIGHT/2, RGBA.GRAY_DARK, RGBA.GRAY, playlist.getList());
		}
			
		public float draw(float dX, float dY, int index,boolean inList) {
			float dH = super.draw(dX,dY,index,inList);
			
			if(!inList || holdIndex != -1)
				return dH;
			if(playlist.getIndex() == index) {
				GOGL.setColor(A_CYAN);
				GOGL.fillRectangle(dX,dY,w,dH);
			}
			
			if(hoverRectangle(dX,dY,w,dH-1)) {
				GOGL.setColor(A_WHITE);
				Mouse.setFingerCursor();
				GOGL.fillRectangle(dX,dY,w,dH);
				
				/*if(index != playlist.getIndex())
					if(Mouse.getLeftReleased())
						playlist.play(index);*/
			}
			
			return dH;
		}
	}
	
	private void drawBG() {
		float segL = 5;
		
		float d = 3*GOGL.getTime(), dSpd = 15, f, amt;
		int wobbleTime = 5;
		
		for(float dY = (int) Math2D.calcLenY(wobbleTime,d)*segL; dY < SmartPhone.HEIGHT; dY += segL) {
			d += dSpd;
			f = .5f + Math2D.calcLenY(.5f,d);
			
			f = MathExt.snap(f, .1f);
			
			System.out.println(f);

			GOGL.setColor(RGBA.interpolate(RGBA.WHITE, RGBA.GREEN, f));
			GOGL.fillRectangle(0,dY,SmartPhone.WIDTH,segL);
		}
		
		GOGL.resetColor();
	}
	
	
	
}
