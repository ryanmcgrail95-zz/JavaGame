uniform mat4 uMVPMatrix;
attribute vec4 vPosition;
varying vec4 vColor;
void main() {
	gl_PointSize = 8.0;
	gl_Position = ftransform();

	gl_TexCoord[0] = gl_MultiTexCoord0;

	vColor = vPosition;
}