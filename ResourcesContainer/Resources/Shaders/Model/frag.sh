uniform vec2 iResolution;
uniform vec4 uColor;
uniform float iGlobalTime;
uniform sampler2D tex0, tex1;

void main() {
	gl_FragColor = gl_Color; //texture2D(tex0, 1. - vec2(abs(1 - mod(gl_TexCoord[0].s,2.0)), abs(1 - mod(gl_TexCoord[0].t,2.0)) ));		
}