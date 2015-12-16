uniform vec2 iResolution;
uniform float iGlobalTime;
uniform sampler2D tex0;
uniform sampler2D tex1;

vec3 mixColors(vec3 col1, vec3 col2, float amt) {
    float f = 1. - amt;
    return vec3(col1.r*f + col2.r*amt,
               col1.g*f + col2.g*amt,
               col1.b*f + col2.b*amt);
}

void main() {
	vec2 uv = gl_TexCoord[0].st, uvR;

	float dir, co, si;
	dir = iGlobalTime/180.*3.14159*50.*2.;
	co = cos(dir);
	si = sin(dir);
	
	mat3 maT = mat3(1., 0., 0., 
					0., 1., 0.,
					1., 0., 1.);
	mat3 maR = mat3(co, si, 0., 
					-si, co, 0.,
					0., 0., 1.);
	uvR = (.5 + maR*maT*(vec3(uv,1.)-.5)).xy;
	
 	vec4 texCol = texture2D(tex0,uv);
    	//float value = .21*texCol.r + .72*texCol.g + .07*texCol.b;

	vec3 rainbowCol = texture2D(tex1,uvR).rgb;

	vec3 outCol = texCol.rgb + rainbowCol*.6;
	gl_FragColor = vec4(outCol, texCol.a);
}