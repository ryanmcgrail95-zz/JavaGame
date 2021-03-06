package gfx;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import com.jogamp.opengl.GL2;
import fl.FileExt;

public class Shader {
	private int vertexShader, fragmentShader, mProgram;
	
	public Shader(GL2 gl, String name) {
		
		vertexShader = loadShader(gl, gl.GL_VERTEX_SHADER, loadShaderText("Resources/Shaders/" + name + "/vertex.sh"));
		fragmentShader = loadShader(gl, gl.GL_FRAGMENT_SHADER, loadShaderText("Resources/Shaders/" + name + "/frag.sh"));

        mProgram = gl.glCreateProgram();

		gl.glAttachShader(mProgram, vertexShader);
        gl.glAttachShader(mProgram, fragmentShader);
        
        
        gl.glLinkProgram(mProgram);
        
        System.out.println(gl.glGetError() == gl.GL_NO_ERROR);

        
        gl.glValidateProgram(mProgram);
        
        
        
        IntBuffer intBuffer = IntBuffer.allocate(1);
        gl.glGetProgramiv(mProgram, gl.GL_LINK_STATUS,intBuffer);
        if (intBuffer.get(0)!=1){
           gl.glGetProgramiv(mProgram, gl.GL_INFO_LOG_LENGTH,intBuffer);
           int size = intBuffer.get(0);
           System.err.println("Program link error: ");
           if (size>0){
              ByteBuffer byteBuffer = ByteBuffer.allocate(size);
              gl.glGetProgramInfoLog(fragmentShader, size, intBuffer, byteBuffer);
              for (byte b:byteBuffer.array()){
                 System.err.print((char)b);
              }
           } else {
              System.out.println("Unknown");
           }
           System.exit(1);
        }
	}
	
	
	private static String loadShaderText(String fileName) {
		InputStreamReader is = new InputStreamReader(FileExt.get(fileName));
		BufferedReader br = new BufferedReader(is);
		String shSource = "", line = "";
		
		try {
			while ((line = br.readLine()) != null) {
			    shSource += line + "\n";
			}
		} catch (IOException e) {
			return "";
		}
		
		System.out.println(fileName);
		//System.out.println(shSource);
		
		try {
			is.close();
			//br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return shSource;
	}
	
	public static int loadShader(GL2 gl, int type, String shaderCode) {
		// Create a shader of the correct type
        int shader = gl.glCreateShader(type);

        // Compile the shader from source code
        gl.glShaderSource(shader, 1, new String[] { shaderCode }, (int[]) null, 0);
        gl.glCompileShader(shader);
        
        return shader;
    }


	public int getVertexShader() {
		return vertexShader;
	}
	
	public int getFragmentShader() {
		return fragmentShader;
	}
	
	public int getProgram() {
		return mProgram;
	}	
}
