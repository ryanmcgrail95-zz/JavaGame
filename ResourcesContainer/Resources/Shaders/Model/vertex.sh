attribute vec4 vPosition;
attribute vec4 iColor;

varying vec4 vColor;
varying vec3 vNormal;

void main() {
	gl_Position = ftransform();
	gl_TexCoord[0] = gl_MultiTexCoord0;
	
	vColor = iColor;
	vNormal = gl_Normal/length(gl_Normal);
}