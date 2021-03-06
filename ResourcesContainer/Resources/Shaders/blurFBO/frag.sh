uniform sampler2D tex0;
uniform sampler2D tex1;
uniform ivec2 iResolution;
uniform float focalPoint;
uniform float focalScale;

float normpdf(in float x, in float sigma){
	return 0.39894*exp(-0.5*x*x/(sigma*sigma))/sigma;
}


void main() {
    vec2 uv = gl_TexCoord[0].st;
	//ivec2 tSi = textureSize(tex0);


	vec4 cw = texture2D(tex0, uv);
	vec3 c = cw.rgb;
	
	//declare stuff
	const int mSize = 11;
	const int kSize = (mSize-1)/2;
	float kernel[mSize];
	vec3 final_colour = vec3(0.0);
	
	//create the 1-D kernel
	float sigma = 7.0;
	float Z = 0.0;
	for (int j = 0; j <= kSize; ++j) {
		kernel[kSize+j] = kernel[kSize-j] = normpdf(float(j), sigma);
	}
	
	//get the normalization factor (as the gaussian has been clamped)
	for (int j = 0; j < mSize; ++j) {
		Z += kernel[j];
	}
	
	float dep = texture2D(tex1, uv).r;
	float blurScale = abs(focalPoint-dep) * focalScale;
		
	//read out the texels
	vec2 uvo;
	vec3 o;
	float depo, tot = 0;
	for (int i=-kSize; i <= kSize; ++i) {
		for (int j=-kSize; j <= kSize; ++j) {
			uvo = (uv*iResolution + blurScale*vec2(float(i),float(j)))/iResolution;
			o = texture2D(tex0, uvo).rgb;
			
			// TODO: Reduce blur spread between different depthed regions.
			depo = 1. - pow(abs(texture2D(tex1, uvo).r - dep), 10);
			tot += depo;
			
			final_colour += kernel[kSize+j]*kernel[kSize+i]*depo*o;
		}
	}
		
	gl_FragColor = vec4(final_colour/(Z*Z), cw.a);
}