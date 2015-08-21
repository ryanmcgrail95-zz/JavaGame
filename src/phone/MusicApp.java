package phone;

import java.util.List;

import com.jogamp.opengl.util.texture.Texture;

import cont.TextureController;
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
	private boolean isLooping = false, isScrubbing = false;
	private int padding = 12;
	
	float 	textHeight = 10,
			textScale = textHeight/8;
	float 	infoBGX = padding,							// Infobox Background Location
			infoBGY = padding,
			infoBGW = SmartPhone.WIDTH/2-infoBGX,
			infoBGH = 12+2*textHeight+12;
	float 	infoX = infoBGX + 12+64+padding,			// Info Location
			infoY = infoBGY + 12;
	float 	wfX = infoBGX+infoBGW+padding,				// Waveform Location
			wfY = infoBGY+infoBGH/2,
			wfW = SmartPhone.WIDTH - wfX - padding,
			wfH = infoBGH;
	float 	albS = 48,
			albX = padding+12,
			albY = infoBGY+infoBGH/2 - 64/2;
	int 	barX = padding,
			barY = SmartPhone.HEIGHT/2 - padding,
			barW = SmartPhone.WIDTH-2*padding,
			barH = 16,
			buttonY = barY - 24;
	
	private PlaylistDrawer plDrawer;
	
	public MusicApp(SmartPhone owner) {
		super(owner);
		playlist = new Playlist("Ace Attorney");//,"damonGant","godot","courtBegins");
		playlist.play();
		source = playlist.getSource();
		
		plDrawer = new PlaylistDrawer(SmartPhone.WIDTH/2, SmartPhone.HEIGHT/2,playlist.getList());
		
		waveform = new FBO(GOGL.gl,barW,barH);
		
		buttonList.add(new PlayButton(SmartPhone.WIDTH/2,buttonY,16));
		buttonList.add(new FastForwardButton(SmartPhone.WIDTH/2+64,buttonY,16));
		buttonList.add(new RewindButton(SmartPhone.WIDTH/2-64,buttonY,16));
		buttonList.add(new NextButton(SmartPhone.WIDTH/2+128,buttonY,16));
		buttonList.add(new PreviousButton(SmartPhone.WIDTH/2-128,buttonY,16));
		buttonList.add(new LoopButton(SmartPhone.WIDTH/2+3*64,buttonY,16));
	}
	
	
	public void render() {
		waveform.attach(GOGL.gl);
		GOGL.clearScreen(new RGBA(0,0,0,0));
		
		GOGL.setColor(RGBA.BLACK);
		GOGL.drawWaveform(0,barH/2, barW,barH,   1,playlist.getSource().getParentBuffer().getByteLen(),   50,10, playlist.getSource().getParentBuffer());
		GOGL.resetColor();
		
		waveform.detach(GOGL.gl);
	}
	public void draw() {
		GLText.setFont("8bit");
		
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
		GOGL.clearScreen(RGBA.WHITE);
		
		//drawBG();
		GOGL.fillVGradientRectangle(0,0,SmartPhone.WIDTH,SmartPhone.HEIGHT, RGBA.CYAN, RGBA.WHITE, 10);

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
			
			// Album Shadow
			GOGL.setColor(RGBA.BLACK);
			GOGL.setAlpha(.5f);
			GOGL.fillRectangle(albX+10,albY+albFY+5, albS,albS);
			
			// Album
			GOGL.setColor(RGBA.WHITE);
			Texture tex = source.getParentBuffer().getAlbumImage();		
			if(tex != null)
				GOGL.drawTexture(albX,albY+albFY, albS,albS, tex);
	
			// Info
			GLText.drawString(infoX, infoY, textScale,textScale, source.getParentBuffer().getName());
			GLText.drawString(infoX, infoY+textHeight, textScale,textScale, source.getParentBuffer().getAlbumName());

		
		// DRAW WAVEFORM AT CURRENT OFFSET
		int s = 10000, start, end;
		start = source.getByteOffset()-s;
		end = source.getByteOffset()+s;
		GOGL.setColor(RGBA.BLACK);
		GOGL.drawWaveform(wfX, wfY, wfW, wfH,   start, end,  10,0, source.getParentBuffer(), true);


		GOGL.resetColor();
		plDrawer.draw();
		
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
		private float x, y;
		protected List<T> list;
		
		public ListDrawer(float x, float y, List<T> list) {
			this.x = x;
			this.y = y;
			this.list = list;
		}
		
		public void draw() {
			float dX = x, dY = y;
			for(int i = 0; i < list.size(); i++)
				dY += draw(dX,dY,i);
		}
		protected abstract float draw(float x, float y, int index);
	}
	private class PlaylistDrawer extends ListDrawer<String> {
		
		public PlaylistDrawer(float x, float y, List<String> list) {
			super(x, y, list);
		}
		
		public float draw(float x, float y, int index) {
			SoundBuffer bf = Sound.get(list.get(index));
			float dP = 2, dS = 20;

			bf = playlist.getSoundBuffer(index);
				
			GOGL.drawTexture(x+dP,y,  dS,dS, bf.getAlbumImage());
			GLText.drawString(x+dP+dS+dP,y, bf.getName());
	
			return dS;
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
