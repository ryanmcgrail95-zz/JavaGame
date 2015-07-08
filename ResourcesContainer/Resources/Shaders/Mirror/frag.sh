uniform vec2 iResolution;
uniform float iGlobalTime;
uniform sampler2D tex;


vec4 mirror(sampler2D source, vec2 size, vec2 uv, float xM) {

	float x;
	float y;
	float w;
	float h;

	w = 1/size.x;
	h = 1/size.y;

	x = xM*w - abs(xM*w - uv.x);
	//y = max(0, min(1, uv.y + )*h));

	return texture2D(source, vec2(x, uv.y));
}

void main() {
	ivec2 size = textureSize2D(tex,0);
	
	gl_FragColor = mirror(tex, size.xy, gl_TexCoord[0].st, 32);
}