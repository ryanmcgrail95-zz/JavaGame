uniform vec2 iResolution;
uniform float iGlobalTime;
uniform float iRadius;
uniform sampler2D tex;


vec4 outline(sampler2D source, vec2 size, vec2 uv) {

	vec4 color;
	color = texture2D(source, uv);

	float x; float y; float w; float h;
	float A;
	float n;
	
	w = 1/size.x;
	h = 1/size.y;

	A = color.a;

	n = 3;

	for(float r = -n/2; r <= n/2; r++)
		for(float c = -n/2; c <= n/2; c++) {
			x = max(0, min(1, (uv.x + c*w)));
			y = max(0, min(1, (uv.y + r*h)));

			A = max(A, texture2D(source, vec2(x,y)).a);
		}

	return vec4(color.rgb,A);
}

void main() {
	ivec2 size = textureSize2D(tex,0);

	gl_FragColor = outline(tex, size.xy, gl_TexCoord[0].st);
}