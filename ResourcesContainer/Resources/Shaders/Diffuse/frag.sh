uniform vec2 iResolution;
uniform float iGlobalTime;
uniform float iRadius;
uniform sampler2D tex;
uniform vec4 uColor;

vec4 diffuse(sampler2D source, vec2 size, vec2 uv, float radius, float dir) {

	float x;
	float y;
	float w;
	float h;

	w = 1/size.x;
	h = 1/size.y;

	x = max(0, min(1, uv.x + radius*cos(dir)*w));
	y = max(0, min(1, uv.y + radius*sin(dir)*h));

	return texture2D(source, vec2(x, y));
}

void main() {
	ivec2 size = textureSize2D(tex,0);
	float rad;
	float dir;
	//float t = iGlobalTime * .1 + ((.25 + .05 * sin(iGlobalTime * .1))/(length(gl_TexCoord[0].xy) + .07)) * 2.2;
	float t = iGlobalTime*length(size.xy)*length(iResolution.xy)*(length(gl_TexCoord[0].xy) + gl_TexCoord[0].x + gl_TexCoord[0].y);
	rad = mod(iRadius*t, iRadius);
	dir = iGlobalTime*t;

	gl_FragColor = uColor*diffuse(tex, size.xy, gl_TexCoord[0].st, rad, dir);
}