// "Seascape" by Alexander Alekseev aka TDM - 2014
// License Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.

uniform vec2 iResolution;
uniform float iGlobalTime;
uniform vec3 iCamPos;
uniform vec3 rCamPos;
uniform vec3 iCamDir;
varying vec3 vVertex;
uniform float seaLevel;
uniform sampler2D noiseTex;
uniform vec3 lights[1];
varying vec4 vWVertex;

const int NUM_STEPS = 8;
const float PI	 	= 3.1415;
const float EPSILON	= 1e-3;
float EPSILON_NRM	= 0.1 / iResolution.x;

// sea
const int ITER_GEOMETRY = 3;
const int ITER_FRAGMENT = 5;
const float SEA_HEIGHT = 0.6;
const float SEA_CHOPPY = 4.0;
const float SEA_SPEED = 0.8;
const float SEA_FREQ = 0.16;
const vec3 SEA_BASE = vec3(0.1,0.19,0.22);
const vec3 SEA_WATER_COLOR = vec3(0.8,0.9,0.6);
float SEA_TIME = iGlobalTime * SEA_SPEED;
mat2 octave_m = mat2(1.6,1.2,-1.2,1.6);

// math
mat3 fromEuler(vec3 ang) {
	vec2 a1 = vec2(sin(ang.x),cos(ang.x));
    vec2 a2 = vec2(sin(ang.y),cos(ang.y));
    vec2 a3 = vec2(sin(ang.z),cos(ang.z));
    mat3 m;
    m[0] = vec3(a1.y*a3.y+a1.x*a2.x*a3.x,a1.y*a2.x*a3.x+a3.y*a1.x,-a2.y*a3.x);
	m[1] = vec3(-a2.y*a1.x,a1.y*a2.y,a2.x);
	m[2] = vec3(a3.y*a1.x*a2.x+a1.y*a3.x,a1.x*a3.x-a1.y*a3.y*a2.x,a2.y*a3.y);
	return m;
}
float hash( vec2 p ) {
	float h = dot(p,vec2(127.1,311.7));	
    return fract(sin(h)*43758.5453123);
}
float noise( in vec2 p ) {
    vec2 i = floor( p );
    vec2 f = fract( p );	
	vec2 u = f*f*(3.0-2.0*f);
    return -1.0+2.0*mix( mix( hash( i + vec2(0.0,0.0) ), 
                     hash( i + vec2(1.0,0.0) ), u.x),
                mix( hash( i + vec2(0.0,1.0) ), 
                     hash( i + vec2(1.0,1.0) ), u.x), u.y);
}

// lighting
float diffuse(vec3 n,vec3 l,float p) {
    return pow(dot(n,l) * 0.4 + 0.6,p);
}
float specular(vec3 n,vec3 l,vec3 e,float s) {    
    float nrm = (s + 8.0) / (3.1415 * 8.0);
    return pow(max(dot(reflect(e,n),l),0.0),s) * nrm;
}

// sky
vec3 getSkyColor(vec3 e) {
    e.y = max(e.y,0.0);
    vec3 ret;
    ret.x = pow(1.0-e.y,2.0);
    ret.y = 1.0-e.y;
    ret.z = 0.6+(1.0-e.y)*0.4;
    return ret;
}


	vec3 sunColor = vec3(1.8, 1.1, 0.6);
	vec3 skyColor = vec3(0.4, 0.6, 0.85);
	vec3 sunLightColor = vec3(1.5, 1.25, 0.9);
	vec3 skyLightColor = vec3(0.15, 0.2, 0.3);
	vec3 indLightColor = vec3(0.4, 0.3, 0.2);
	vec3 cloudsColor = vec3(1.0, 1.0, 1.0);
	vec3 horizonColor = vec3(0.7, 0.75, 0.8);
	vec3 fogColorB = vec3(0.7, 0.8, 0.9);
	vec3 fogColorR = vec3(0.8, 0.7, 0.6);

	vec3 sunDirection = normalize(vec3(0.6, 0.8, 0.5));

	vec2 uv;

	float sqr(float x) {
		return x*x;
	}

	float calcDis(vec3 pt1, vec3 pt2) {
		return sqrt(sqr(pt1.x-pt2.x) + sqr(pt1.y-pt2.y) + sqr(pt1.y-pt2.y));
	}

	float contain(float val) {
		return max(0.,min(1.,val));
	}


	float cloudsHeight = 800.0;
	float cloudsDensity = 0.3;
	float cloudsCover = 0.2;

	float noiseT(in vec2 p) {
		return texture2D(noiseTex, p / 256.0, -100.0).x * 2.0 - 1.0;
	}

	float fBm(in vec2 p) {
	    float sum = 0.0;
	    float amp = 1.0;
	    for(int i = 0; i < 4; i++) {
		sum += amp * noiseT(p);
		amp *= 0.5;
		p *= 2.0;
	    }
	    return sum;
	}

	float fBmC(in vec2 p) {
	    float sum = 0.0;
	    float amp = 1.0;
	    for(int i = 0; i < 5; i++) {
		sum += amp * noiseT(p);
		amp *= 0.5;
		p *= 2.0;
	    }
	    return sum;
	}



	float raymarchShadow(in vec3 ro, in vec3 rd, float tmin, float tmax) {
	    float sh = 1.0;
	    float t = tmin;
	    for(int i = 0; i < 50; i++) {
		vec3 p = ro + rd * t;
		float d = p.y - fBm(p.xz);
		sh = min(sh, 16.0 * d / t);
		t += 0.5 * d;
		if (d < (0.001 * t) || t > tmax)
		    break;
	    }
	    return sh;
	}
	vec4 getFogColor() {
		vec2 p = gl_FragCoord.xy / iResolution.xy * 2.0 - 1.0;

		vec3 ro = iCamPos - vec3(0.,seaLevel,0.);


		vec3 ang = iCamDir;   
		vec3 dir = normalize(vec3(p,-2.5));
		vec3  rd = normalize(dir) * fromEuler(ang);

		// the powerful sun dot
		float sunDot = clamp(0, 0.0, 1.0);

		// terrain marching
		vec3 color;

		// sky and sun
		float sky = clamp(0.6 * (1.0 - 0.8 * rd.y), 0.0, 1.0);
		float diffuse = clamp(0.4 * sunDot, 0.0, 1.0);
		color = sky * skyColor + diffuse * skyLightColor;

		// horizon
		color = mix(color, horizonColor, pow(1.0 - rd.y, 4.0));

		// gamma correction
		vec3 gamma = vec3(1.0 / 2.2);
		return vec4(pow(color, gamma), 1.0);
	}

	vec4 mixColors(vec4 col1, vec4 col2) {
		float a1 = col1.a, a2 = col2.a;
		float aTot = a1+a2;
		float w1 = a1/aTot, w2 = a2/aTot;

		vec4 mixed = vec4(col1.rgb*w1 + col2.rgb*w2, 1.);

		return mixed;
	}

// sea
float sea_octave(vec2 uv, float choppy) {
    uv += noise(uv);        
    vec2 wv = 1.0-abs(sin(uv));
    vec2 swv = abs(cos(uv));    
    wv = mix(wv,swv,wv);
    return pow(1.0-pow(wv.x * wv.y,0.65),choppy);
}

float map(vec3 p) {
    float freq = SEA_FREQ;
    float amp = SEA_HEIGHT;
    float choppy = SEA_CHOPPY;
    vec2 uv = p.xz; uv.x *= 0.75;
    
    float d, h = 0.0;    
    for(int i = 0; i < ITER_GEOMETRY; i++) {        
    	d = sea_octave((uv+SEA_TIME)*freq,choppy);
    	d += sea_octave((uv-SEA_TIME)*freq,choppy);
        h += d * amp;        
    	uv *= octave_m; freq *= 1.9; amp *= 0.22;
        choppy = mix(choppy,1.0,0.2);
    }
    return p.y - h;
}

float map_detailed(vec3 p) {
    float freq = SEA_FREQ;
    float amp = SEA_HEIGHT;
    float choppy = SEA_CHOPPY;
    vec2 uv = p.xz; uv.x *= 0.75;
    
    float d, h = 0.0;    
    for(int i = 0; i < ITER_FRAGMENT; i++) {        
    	d = sea_octave((uv+SEA_TIME)*freq,choppy);
    	d += sea_octave((uv-SEA_TIME)*freq,choppy);
        h += d * amp;        
    	uv *= octave_m; freq *= 1.9; amp *= 0.22;
        choppy = mix(choppy,1.0,0.2);
    }
    return p.y - h;
}

vec3 getSeaColor(vec3 p, vec3 n, vec3 l, vec3 eye, vec3 dist) {  
    float fresnel = 1.0 - max(dot(n,-eye),0.0);
    fresnel = pow(fresnel,3.0) * 0.65;
        
    vec3 reflected = getSkyColor(reflect(eye,n));    
    vec3 refracted = SEA_BASE + diffuse(n,l,80.0) * SEA_WATER_COLOR * 0.12; 
    
    vec3 color = mix(refracted,reflected,fresnel);
    
    float atten = max(1.0 - dot(dist,dist) * 0.001, 0.0);
    color += SEA_WATER_COLOR * (p.y - SEA_HEIGHT) * 0.18 * atten;
    
    color += vec3(specular(n,l,eye,60.0));
    
    return color;
}

// tracing
vec3 getNormal(vec3 p, float eps) {
    vec3 n;
    n.y = map_detailed(p);    
    n.x = map_detailed(vec3(p.x+eps,p.y,p.z)) - n.y;
    n.z = map_detailed(vec3(p.x,p.y,p.z+eps)) - n.y;
    n.y = eps;
    return normalize(n);
}

float heightMapTracing(vec3 ori, vec3 dir, out vec3 p) {  
    float tm = 0.0;
    float tx = 1000.0;    
    float hx = map(ori + dir * tx);
    if(hx > 0.0) return tx;   
    float hm = map(ori + dir * tm);    
    float tmid = 0.0;
    for(int i = 0; i < NUM_STEPS; i++) {
        tmid = mix(tm,tx, hm/(hm-hx));                   
        p = ori + dir * tmid;                   
    	float hmid = map(p);
		if(hmid < 0.0) {
        	tx = tmid;
            hx = hmid;
        } else {
            tm = tmid;
            hm = hmid;
        }
    }
    return tmid;
}

// main
void main() {
	vec2 uv = gl_FragCoord.xy / iResolution.xy;
    uv = uv * 2.0 - 1.0;
    uv.x *= iResolution.x / iResolution.y;    
    float time = 0.;//iGlobalTime * 0.3;// + iMouse.x*0.01;
        
    // ray
	vec3 ori = iCamPos - vec3(0.,vVertex.y,0.);
	vec3 ang = iCamDir;
    //vec3 ori = vec3(0.0,3.5,time*5.0);
    vec3 dir = normalize(vec3(uv.xy,-2.5)); //dir.z += length(uv) * 0.15;
    dir = normalize(dir)*fromEuler(ang);
    
    // tracing
    vec3 p;
    heightMapTracing(ori,dir,p);
    vec3 dist = p - ori;
    vec3 n = getNormal(p, dot(dist,dist) * EPSILON_NRM);
    vec3 light = normalize(vec3(0.0,1.0,0.8)); 
             
    // color
    vec3 color = mix(
        getSkyColor(dir),
        getSeaColor(p,n,light,dir,dist),
    	pow(smoothstep(0.0,-0.05,dir.y),0.3));
        
    // post
	gl_FragColor = vec4(pow(color,vec3(0.75)), .8);
}
