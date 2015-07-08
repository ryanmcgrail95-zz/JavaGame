uniform vec2 iResolution;
uniform float iGlobalTime;
uniform sampler2D tex;


vec4 ripple(sampler2D source, vec2 size, vec2 uv, float radius, float time) {

	//vec2 sUV = 1 - 2*abs((gl_FragCoord.xy / iResolution) - .5);

	float x;
	float y;
	float w;
	float h;

	w = 1/size.x;
	h = 1/size.y;

	x = uv.x + radius*cos(time + uv.y*3.14*2)*w;
	y = uv.y + radius/w*h*sin(time + uv.x*3.14*2)*h;

	while(x > 1) {
		x -= 1;
	}
	while(x < 0) {
		x += 1;
	}

	y = max(0, min(1, y));

	return texture2D(source, vec2(x, y));
}

void main() {
	ivec2 size = textureSize2D(tex,0);
	
	gl_FragColor = ripple(tex, size.xy, gl_TexCoord[0].st, length(size.xy)/10, iGlobalTime*50/180*3.14159);
}