uniform mat4 uMVPMatrix;
attribute vec4 vPosition;
varying vec4 vColor;
varying vec3 vVertex;
uniform float seaLevel;
varying vec4 vWVertex;

void main() {
	gl_PointSize = 8.0;
	gl_Position = ftransform();

	vColor = vPosition;
	vVertex = vec3(0.,seaLevel,0.); //vPosition.xyz;
	vWVertex = gl_Vertex;//uMMatrix * gl_ModelViewMatrix * gl_Position;
}
