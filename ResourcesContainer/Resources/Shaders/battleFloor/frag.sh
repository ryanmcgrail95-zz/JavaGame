uniform vec2 iResolution;
uniform float iUnboundTime;
uniform float iRadius;
uniform vec4 uColor, camPos;
uniform sampler2D tex;



vec3 dots(vec2 tileCoord) {

	float value = 1. * sin(tileCoord.x*3.14159*10. + 16.*iUnboundTime)*cos(tileCoord.y*3.14159*16. + 10.*iUnboundTime);

	return vec3(value);
}


vec2 rand2(vec2 p) {
	vec2 q = vec2(dot(p,vec2(127.1,311.7)), 
		dot(p,vec2(269.5,183.3)));
	return fract(sin(q)*43758.5453);
}

float rand(vec2 p) {
	return fract(sin(dot(p,iUnboundTime*vec2(419.2,371.9))) * 833458.57832);
}

float iqnoise(in vec2 pos, float irregular, float smoothness)
{
	vec2 cell = floor(pos);
	vec2 cellOffset = fract(pos);

	float sharpness = 1.0 + 63.0 * pow(1.0-smoothness, 4.0);
	
	float value = 0.0;
	float accum = 0.0;
	// Sample the surrounding cells, from -2 to +2
	// This is necessary for the smoothing as well as the irregular grid.
	for(int x=-2; x<=2; x++ )
	for(int y=-2; y<=2; y++ )
	{
		vec2 samplePos = vec2(float(y), float(x));

  		// Center of the cell is not at the center of the block for irregular noise.
  		// Note that all the coordinates are in "block"-space, 0 is the current block, 1 is one block further, etc
		vec2 center = rand2(cell + samplePos) * irregular;
		float centerDistance = length(samplePos - cellOffset + center);

		// High sharpness = Only extreme values = Hard borders = 64
		// Low sharpness = No extreme values = Soft borders = 1
		float sample = pow(1.0 - smoothstep(0.0, 1.414, centerDistance), sharpness);

		// A different "color" (shade of gray) for each cell
		float color = rand(cell + samplePos);
		value += color * sample;
		accum += sample;
	}

	return value/accum;
}

vec3 noise(vec2 uv) {
    vec2 p = 0.5 - 0.5*sin( vec2(1.01,1.71) );
		
	p = p*p*(3.0-2.0*p);
	p = p*p*(3.0-2.0*p);
	p = p*p*(3.0-2.0*p);
	
	float f = iqnoise( 128.0*uv, p.x, p.y );
	
	return vec3(f);
}

float gridDis(float x, float grid) {
	return round(x/grid)*grid - x;
}


float bgGrid(vec2 tileCoord, vec2 pos) {

	/*
		float gridS = 1.5*128., lineW = 3.;
		float value;
	
		//float innie = (.5 + .5*cos(tileCoord.y*3.14159*3 + 2*sin(tileCoord.x*3.14159)*iUnboundTime));
	
	
		value = .7*(mod(pos.x,gridS) < lineW ? 1. : (mod(pos.y,gridS) < lineW ? 1. : 0.));
	
		return vec3(value);		
	*/

	float gridS = .64, lineW = .0025;
	float value;

	//float innie = (.5 + .5*cos(tileCoord.y*3.14159*3 + 2*sin(tileCoord.x*3.14159)*iUnboundTime));

	value = (gridS - min(abs(gridDis(tileCoord.x,gridS)*1.5),abs(.8*gridDis(tileCoord.y,gridS))))/gridS;

	value = pow(value, 20.);	
	value = (value > .5) ? 1. : 0.;

	vec2 uv = gl_TexCoord[0].xy;
	value *= (uv.y < .5) ? 1. : (1-uv.y)*2.;
	
	
	
	// Darker sublines
	float t = 2*iUnboundTime, r = .05;
	
	float sGr = gridS/4., subG = (sGr-min(abs(gridDis(tileCoord.x,sGr)*1.5),abs(.8*gridDis(tileCoord.y,sGr))))/sGr;
		subG = pow(subG, 20.);	
		subG = (subG > .5) ? 1. : 0.;
		subG *= (uv.y < .5) ? 1. : (1-uv.y)*2.;
	
	value += (value < .1) ? .2*subG : 0.;
	
	//value = 1000*pow(value, 10.);

	return value;
}

void main() {    
	vec2 uv = gl_TexCoord[0].xy;
	
	uv.y = pow(uv.y, 3.);
	
	uv.x *= 5.00;
	uv.y *= 6.00;
	
	vec2 pos;
	pos.x = -300 + 600*uv.x;
	pos.y = -200 + 500*uv.y;
	
	float v = .5 + .5*sin(1.5*iUnboundTime);
	vec3 
		colBG = .2 * pow(gl_TexCoord[0].y,4.) * vec3(0,.8,1)*v,
		colG = pow(1.-gl_TexCoord[0].y,2. - 1.5*v) * vec3(.1,1,.6)*bgGrid(uv, pos);
	
		
	gl_FragColor = vec4(colG + colBG,1.);
}