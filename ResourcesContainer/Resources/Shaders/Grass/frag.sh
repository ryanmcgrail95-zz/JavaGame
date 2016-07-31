uniform vec2 iResolution;
uniform float iGlobalTime;
uniform float iUnboundTime;
uniform sampler2D tex0;
uniform sampler2D tex1;

float interp(float a, float b, float angle) {
	float f = cos(angle);
	return a*f + b*(1-f);
}

vec4 grass(sampler2D source, vec2 size, vec2 uv) {

	float s = 1./16., rad = 4./256.;

	float x;
	float y;
	float w;
	float h;

	w = 1/size.x;
	h = 1/size.y;
	
	float
		px = gl_FragCoord.x,
		py = gl_FragCoord.y;
	
	float
		PI = 3.14159,
		time = iUnboundTime*100/180*3.14159,
		angle = fract((px + py + time/5)/PI) * PI,
		ang = cos(angle)*sin(angle),
		xrad = 0., //rad*cos(interp(5.*time, 6.*time, ang)),
		yrad = 0., //rad*sin(interp(4.5*time, 3.3*time, ang)),
		xtime = 0., //time*3.,
		ytime = 0.; //time*2.;

	x = uv.x + xrad*cos(xtime);
	y = uv.y + yrad*sin(ytime);

	float x1, x2, y1, y2;
	
	if(ang > 0) {
		x1 = 0;
		x2 = x1+s;
		y1 = 3*s;
		y2 = y1+s;
	}
	else {
		x1 = 2*s;
		x2 = x1+s;
		y1 = 2*s;
		y2 = y1+s;
	}

	while(x > x2)
		x -= s;
	while(x < x1)
		x += s;
	while(y > y2)
		y -= s;
	while(y < y1)
		y += s;

	return texture2D(source, vec2(x, y));
}

float rand(vec2 co){
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

vec4 ripple(sampler2D source, vec2 size, vec2 uv) {

	float s = 1./16., rad = 2./256.;

	float x;
	float y;
	float w;
	float h;

	w = 1/size.x;
	h = 1/size.y;
	
	float
		px = gl_FragCoord.x,
		py = gl_FragCoord.y;
	
	float
		PI = 3.14159,
		time = iUnboundTime*100/180*3.14159,
		angle = fract((px + py + time/5)/PI) * PI,
		ang = cos(angle)*sin(angle),
		xrad = rad*cos(interp(5.*time, 6.*time, ang)),
		yrad = rad*sin(interp(4.5*time, 3.3*time, ang)),
		xtime = time*3.,
		ytime = time*2.;
	
	if(rand(vec2(px,py) + time) < .1)
		return vec4(1.);

	x = uv.x + xrad; //*cos(xtime);
	y = uv.y + yrad; //*sin(ytime);

	float x1, x2, y1, y2;
	
	if(ang > 0) {
		x1 = 0;
		x2 = x1+s;
		y1 = 3*s;
		y2 = y1+s;
	}
	else {
		x1 = 2*s;
		x2 = x1+s;
		y1 = 2*s;
		y2 = y1+s;
	}

	while(x > x2)
		x -= s;
	while(x < x1)
		x += s;
	while(y > y2)
		y -= s;
	while(y < y1)
		y += s;

	return texture2D(source, vec2(x, y));
}

void main() {
	ivec2 size = ivec2(16,16);
	
	gl_FragColor = ripple(tex0, size.xy, gl_TexCoord[0].st);
}