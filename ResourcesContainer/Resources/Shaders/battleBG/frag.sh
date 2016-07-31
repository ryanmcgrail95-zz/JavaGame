/*
	MasterGamePro
	battleBG
*/

uniform vec2 iResolution;
uniform float iUnboundTime;
uniform float iRadius;
uniform vec4 uColor;
uniform sampler2D tex;


vec3 diamondShine(float i){
    float shine = abs(mod(i,2.0)-1.0);
    shine *= 7.0;
    return vec2(32.0+shine*24.0,4.0+shine*20.0).xxy/255.0;
}


vec3 bgTexture(vec2 tileCoord) {
	
	tileCoord.x += iUnboundTime/30.;
	
    tileCoord.y += iUnboundTime*0.1;
    tileCoord.y += pow(sin(tileCoord.y+iUnboundTime*0.75),2.0);
    
    vec2 withinTile = mod(tileCoord,1.0);
    vec2 tileStep = floor(tileCoord);
    
    bool inDiamond = (abs(withinTile.x-0.5) + abs(withinTile.y-0.5)) < 0.5;
    
    vec3 color;
    
    if (inDiamond) {
        return diamondShine( tileStep.x*1.1 + tileStep.y*1.6 + iUnboundTime*0.5 );
    } else {
        return vec3(80.0/255.0,96.0/255.0,72.0/255.0);
    }
	
}


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


float bgGrid(vec2 tileCoord) {

	float gridS = .64, lineW = .015;
	float value;

	tileCoord += iUnboundTime*.5;

	//float innie = (.5 + .5*cos(tileCoord.y*3.14159*3 + 2*sin(tileCoord.x*3.14159)*iUnboundTime));

	float v = .5 + .5*sin(1.5*iUnboundTime);


	value = .7*(v*1. + (1.-v)*noise(tileCoord).x) * v * (mod(tileCoord.x,gridS) < lineW ? 1. : (mod(tileCoord.y,gridS) < lineW ? 1. : 0.));
	
	vec2 withinTile = mod(tileCoord,1.0);
    vec2 tileStep = floor(tileCoord);
    
    bool inDiamond = (abs(withinTile.x-0.5) + abs(withinTile.y-0.5)) < 0.5;
    

	return value;
}


void main() {    
	vec2 uv = gl_TexCoord[0].xy;
		
	uv.x *= 6.40;
	uv.y *= 4.00;
	
	float n1 = 0; //noise(1-uv).x
	float v = .1*n1 + bgGrid(uv);

	gl_FragColor = vec4(vec3(0,.8,1)*v,1.); //vec4(vec3(value), 1.);

    //gl_FragColor = texture2D(tex,uv);
}