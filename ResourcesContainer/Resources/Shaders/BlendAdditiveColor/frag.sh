// Blend Additive Color
// Fragment Shader

uniform sampler2D tex0;
uniform vec4 fadeRGBA;

void main() {
    vec2 uv = gl_TexCoord[0].st;
	vec4 pixRGBA = texture2D(tex0, uv);
	
	float
		outR = pixRGBA.r+fadeRGBA.r*fadeRGBA.a,
		outG = pixRGBA.g+fadeRGBA.g*fadeRGBA.a,
		outB = pixRGBA.b+fadeRGBA.b*fadeRGBA.a;
	
	gl_FragColor = vec4(outR, outG, outB, pixRGBA.a);
}