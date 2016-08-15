package window.text;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;

import cont.GameController;
import ds.ChompException;
import ds.StringExt;
import fl.FileExt;
import functions.Math2D;
import functions.MathExt;
import gfx.G2D;
import gfx.GL;
import gfx.Gameboy;
import gfx.MultiTexture;
import gfx.RGBA;
import io.Keyboard;
import io.Mouse;
import resource.font.MergedFont;
import script.Script;
import window.EditorWindow;
import window.GUIDrawable;
import window.GUIEditor;
import window.GUIFrame;
import window.Window;

public class GUITextEditor extends GUIDrawable implements GUIEditor {
	private Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

	private LinkedList<StringExt> lines = new LinkedList<StringExt>();
	private int lineNumber, index;

	private int fontWidth = 8, fontHeight = 8;

	private char charDown;
	private int stepsDown;

	private RGBA backgroundColor = RGBA.WHITE, textColor = RGBA.BLACK;
	private RGBA lineColor = RGBA.WHITE, lineBGColor = Gameboy.GRAY_L;
	
	private String currentError = "", currentErrorExpected = "";
	private int currentErrorIndex = 0, currentErrorLineNumber = 0;

	private String fileName = "";

	private boolean showLineNumbers = true;

	private int selectionIndex1, selectionLine1, selectionIndex2, selectionLine2;

	public GUITextEditor(int width, int height) {
		super(0, 0, width, height);

		clear();
		addLine();
		
		open("Resources/Text/default.txt");
	}

	@Override
	public byte draw(float frameX, float frameY) {
		char c = Keyboard.getCharDown();

		if (c != Keyboard.C_NULL) {
			if (Keyboard.isControlActive()) {
				if (charDown != c) {
					stepsDown = 0;
				} else {
					stepsDown++;

					if (stepsDown == 1) {
						checkControlCharacters();
					}

					if (stepsDown > 30) {
						checkControlCharacters();
					}
				}

			} else {
				if (charDown != c) {
					stepsDown = 0;
					appendChar(c);
				} else {
					stepsDown++;

					if (stepsDown > 30) {
						appendChar(c);
					}
				}
			}
		}

		charDown = c;
		
		GUIFrame parent = getParent();
		int vY = parent.getViewY(),
			h = (int) parent.h(),
			iH = parent.getInternalHeight();
		
		int i1, i2;
		i1 = (int) Math.floor(MathExt.contain(0, 1f*(vY)/fontHeight, lines.size()));
		i2 = (int) Math.ceil(MathExt.contain(0, 1f*(vY+h)/fontHeight, lines.size()));

		GL.clear(backgroundColor);
		
		// Draw line numbers.
		GL.setColor(lineBGColor);
		G2D.fillRectangle(0, 0, fontWidth, h());
		int aY = 0;
		if (showLineNumbers) {
			for (int i = i1; i < i2; i++) {
				String num = String.valueOf(i + 1);

				aY = fontHeight*i;
				
				GL.setColor(lineColor);
				G2D.drawString(frameX, frameY + aY, 1f / num.length(), 1, num);

				GL.setColor(lineBGColor);
				G2D.drawLine(0, frameY + aY, w(), frameY + aY);				
			}

			frameX += fontWidth;
		}

		// Draw text.
		GL.setColor(textColor);
		aY = 0;
		for (int i = i1; i < i2; i++) {
			StringExt line = lines.get(i);
			aY = fontHeight*i;
			G2D.drawString(frameX, frameY + aY, line.get());
		}

		// Click to change position.
		if (checkMouse() && Mouse.checkRectangle(this.getScreenX(), this.getScreenY(), w(), h())) {
			Mouse.setTextCursor();

			if (Mouse.getLeftClick()) {
				float[] mouse = this.getParent().getRelativeMouseCoords();

				int newLineNumber = (int) ((mouse[1] - frameY) / fontHeight),
						newIndex = (int) Math.round((mouse[0] - frameX) / fontWidth);

				moveCursor(newIndex, newLineNumber);
			}
		}

		// Draw cursor.
		if (Math2D.calcLenX(GL.getTime() * 7) > 0) {
			MultiTexture tex = ((MergedFont) G2D.getFont()).getMultiTexture();

			GL.forceColor(RGBA.BLACK);
			tex.draw(frameX + G2D.getStringWidth(lines.get(lineNumber).substring(0, index)),
					frameY + lineNumber * fontHeight, 0);
			GL.unforceColor();
		}

		GL.setColor(RGBA.RED);
		G2D.drawString(0,h-fontHeight, currentError);
		if (Math2D.calcLenX(GL.getTime() * 7) > 0) {
			G2D.drawString(frameX + fontWidth*currentErrorIndex, frameY + fontHeight*currentErrorLineNumber, currentErrorExpected);
		}
		
		GL.resetColor();
		
		return 0;
	}

	private void checkControlCharacters() {
		if (stepsDown == 0) {
			if (charDown == 's') {
				save();
			} else if (charDown == 'l') {
				open();
			}
		}

		if (charDown == 'v') {
			paste();
		}
	}

	private void moveCursor(int newIndex, int newLineNumber) {
		GUIFrame parent = getParent();
		float[] mouse = this.getParent().getRelativeMouseCoords();

		int lineCt = lines.size();
		if (newLineNumber >= lineCt) {

			while (newLineNumber >= lineCt) {
				addLine();
				lineCt++;
			}
		} else if (lines.getLast().length() == 0) {
			if (lineNumber == lineCt - 1 && newLineNumber < lineNumber && lineNumber > 0) {
				while (lines.size() > newLineNumber + 1 && lines.getLast().length() == 0)
					lines.removeLast();
			}

			lineCt--;
		}
		
		lineNumber = (int) MathExt.contain(0, newLineNumber, lineCt);
		index = (int) MathExt.contain(0, newIndex, lines.get(lineNumber).length());
		
		// Move screen with cursor.
		int iH = parent.getInternalHeight(), 
			h = (int) parent.h(),
			vY = parent.getViewY(),
			miIH = (int) Math.max(h(), (lines.size()-1) * fontHeight),
			maIH = (lineNumber+1) * fontHeight;
		if(maIH > iH) {
			parent.setInternalHeight(maIH);
			parent.setViewY(maIH - h);
		}
		else if(miIH <= iH) {
			parent.setInternalHeight(miIH);
			if(vY >= iH - h)
				parent.setViewY(iH - h);
		}
	}

	public void appendString(String s) {
		StringExt line = lines.get(lineNumber);
		int len = line.length();

		StringExt str = new StringExt(s);
		String[] lineArray = str.split('\n');

		for (int i = 0, l = lineArray.length; i < l; i++) {
			if (i == 0) {

			}
		}

		line.insert(index, s);
		index += s.length();
	}

	public void appendChar(char c) {
		StringExt line = lines.get(lineNumber);
		int len = line.length();

		if (c != '\n') {

			// Move position with arrow keys.
			if (c == Keyboard.C_ARROW_LEFT) {
				if (index == 0 && lineNumber > 0)
					moveCursor(lines.get(lineNumber - 1).length(), lineNumber - 1);
				else
					moveCursor(index - 1, lineNumber);
			} else if (c == Keyboard.C_ARROW_RIGHT) {
				if (index == len && lineNumber < lines.size())
					moveCursor(0, lineNumber + 1);
				else
					moveCursor(index + 1, lineNumber);
			} else if (c == Keyboard.C_ARROW_UP) {
				moveCursor(index, lineNumber - 1);
			} else if (c == Keyboard.C_ARROW_DOWN) {
				moveCursor(index, lineNumber + 1);
			} else if (c == Keyboard.C_BACKSPACE) {

				// Move line to end of previous.
				if (index == 0 && lineNumber > 0) {
					StringExt prev = lines.get(lineNumber - 1);

					index = prev.length();

					prev.set(prev.toString() + line.toString());
					lines.remove(lineNumber--);
				}

				// Backspace previous character.
				else if (index > 0) {
					line.backspaceAt(index);
					index--;
				}
			}

			// Delete character.
			else if (c == Keyboard.C_DELETE) {

				// Move line to end of previous.
				if (index == len && lineNumber < lines.size() - 1) {
					/*
					 * StringExt next = lines.get(lineNumber + 1);
					 * 
					 * line.set(line.toString() + next.toString());
					 * lines.remove(lineNumber + 1);
					 */

					mergeLines(lineNumber, 1);
				}

				// Delete next character.
				else if (index < len) {
					line.deleteAt(index);
				}
			}

			else {

				if (Pattern.matches(Keyboard.REGEX_NON_CONTROL, "" + c)) {
					line.insert(index, "" + c);
					index++;
				}
			}
		}

		// Newline
		else {
			splitLine(lineNumber, index);
			lineNumber++;
			index = 0;
		}
	}

	@Override
	public boolean paste() {
		appendString(getClipBoard());
		return true;
	}

	public String getClipBoard() {
		try {
			return (String) clipboard.getData(DataFlavor.stringFlavor);
		} catch (HeadlessException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} catch (UnsupportedFlavorException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		return "";
	}

	// GUI METHODS
	public StringExt insertLine(int lineNumber, String text) {
		StringExt s = new StringExt(text);
		lines.add(lineNumber, s);
		return s;
	}

	public StringExt addLine(String text) {
		return insertLine(lines.size(), text);
	}

	public StringExt addLine() {
		return addLine("");
	}

	public StringExt mergeLines(int startLineNumber, int ct) {
		StringExt line = lines.get(startLineNumber), oLine;
		StringBuilder text = new StringBuilder(line.get());

		for (int i = 0; i < ct; i++) {
			oLine = lines.get(startLineNumber + 1);
			text.append(oLine.get());
			lines.remove(startLineNumber + 1);
		}

		line.set(text.toString());
		return line;
	}

	public void splitLine(int lineNumber, int index) {
		/*
		 * if (index == len) { index = 0; lines.add(++lineNumber, new
		 * StringExt("")); } else if (index == 0) { lines.add(lineNumber++, new
		 * StringExt("")); } else { StringExt next = new
		 * StringExt(line.substring(index, len)); line.set(line.substring(0,
		 * index));
		 * 
		 * lines.add(++lineNumber, next); index = 0; }
		 */

		StringExt line = lines.get(lineNumber), next = new StringExt(line.substring(index, line.length()));
		line.set(line.substring(0, index));

		lines.add(lineNumber + 1, next);
	}

	/*
	 * String myString =
	 * "This text will be copied into clipboard when running this code!";
	 * StringSelection stringSelection = new StringSelection(myString);
	 * Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
	 * clpbrd.setContents(stringSelection, null);(non-Javadoc)
	 * 
	 * @see window.GUISaveable#open()
	 */
	
	private void setFile(File file) {		
		fileName = file.getAbsolutePath();
		
		GUIFrame f = getParent();
		if(f instanceof Window) {
			((Window) f).setTitle('"' + file.getName() + '"');
		}
	}

	@Override
	public boolean open() {
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

		int returnVal = fc.showOpenDialog(GameController.getInstance());

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			
			if(!file.exists())
				try {
					file.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			return open(file);
		} else
			return false;
	}
	
	public boolean open(String fileName) {
		return open(FileExt.getFile(fileName));
	}
	
	public boolean open(File file) {
		lines.clear();

		setFile(file);

		StringExt text = new StringExt(FileExt.readFile2String(file));
		String[] lineArray = text.split('\n');

		if(lineArray.length > 0)
			for (String line : lineArray)
				addLine(line);
		else
			addLine();

		index = 0;
		lineNumber = 0;
		return true;
	}

	@Override
	public boolean save() {

		boolean isFileNameValid = true;
		File file = null;

		if (fileName.equals(""))
			isFileNameValid = false;
		else {
			file = new File(fileName);

			if (!file.exists() || file.isDirectory())
				isFileNameValid = false;
		}

		// If invalid file name, reacquire.
		if (!isFileNameValid) {
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

			int returnVal = fc.showSaveDialog(GameController.getInstance());

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = fc.getSelectedFile();
				fileName = file.getAbsolutePath();
			} else
				return false;
		}

		// Write to file!
		try {
			FileWriter fw = new FileWriter(file);
			BufferedWriter writer = new BufferedWriter(fw);

			int ln = 0;
			for (StringExt line : lines) {
				if (ln++ > 0)
					writer.write('\n');
				writer.write(line.get());
			}

			writer.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	public void clear() {
		GUIFrame f = getParent();
		if(f instanceof Window) {
			((Window) f).setTitle("Text Editor");
		}
		
		fileName = "";
		clearSelection();
		index = 0;
		
		lineNumber = 0;
		lines.clear();
	}
	
	public void clearSelection() {
		
	}

	@Override
	public void destroy() {
		clear();
	}

	@Override
	public boolean checkMouse() {
		return getParent().checkMouse();
	}

	public static void createWindow(int x, int y) {
		int width = 320, height = 240;

		GUITextEditor g = new GUITextEditor(width, height);
		Window w = new EditorWindow("Text Editor", x, y, width, height, true, g);
		w.add(g);
	}

	private void minify() {
		/*String line, output = "";
		StringExt lineExt = new StringExt();

		// Erase Whitespace, Newlines, Comments, Etc.
		while(!code.isEmpty()) {
			lineExt.set(line = code.munchLine());
						
			if(lineExt.contains("//"))
				output += lineExt.munchTo("//");
			else
				output += lineExt;			
		}
				 
		code.set(output);
		
		while(!code.munchFromTo("/*","* /").equals(""));*/
	}

	@Override
	public boolean copy() {
		StringSelection selection = new StringSelection("test");
		clipboard.setContents(selection, selection);
		return true;
	}

	@Override
	public boolean cut() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean newFile() {
		clear();
		addLine();
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder text = new StringBuilder("");
		
		int k = 0;
		for(StringExt line : lines) {
			if(k++ > 0)
				text.append('\n');
			
			text.append(line);
		}
		
		return text.toString();
	}

	@Override
	public boolean run() {
		try {
			Script.exec(GL.memory, toString());
			currentError = "";
			currentErrorExpected = "";
		} catch (ChompException e) {
			currentError = e.getError();
			currentErrorExpected = e.getExpected();
			
			currentErrorLineNumber = 0;
			
			int i = e.getIndex(), l;
			for(StringExt line : lines) {
				l = line.length();
				
				if(i > l) {
					currentErrorLineNumber++;
					i -= l+1;
				}
				else
					break;
			}
			
			currentErrorIndex = i;
		}
		
		
		return true;
	}
}
