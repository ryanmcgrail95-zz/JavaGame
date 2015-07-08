uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform mat4 projectionMatrix;
attribute vec4 vPosition;                  // (x,y,z)
attribute vec4 vColour;                    // (r,g,b,a)

varying vec4 vWVertex;

varying vec2 v_vTexcoord;
varying vec3 vNormal;
uniform vec3 camNormal;
varying vec4 vColor;
varying vec3 wvNormal;


void main() {
	mat4 p = projectionMatrix;// gl_ProjectionMatrix;
	//mat4 mvMatrix = viewMatrix * modelMatrix;
	gl_Position = p * gl_ModelViewMatrix * gl_Vertex;
	//gl_Position = gl_ProjectionMatrix * mvMatrix * gl_Vertex;
    	vColor = vec4(1.,1.,1.,1.);
	gl_TexCoord[0] = gl_MultiTexCoord0;    
}