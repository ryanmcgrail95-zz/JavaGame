uniform vec2 iResolution;
uniform vec4 iPosition;
uniform float iGlobalTime;
uniform sampler2D tex0;
uniform sampler2D tex1;


float value(vec3 texCol) {
	return .21*texCol.r + .72*texCol.g + .07*texCol.b;
}

vec3 value3(vec3 texCol) {
	return vec3(value(texCol));
}

float conv(float val) {
	return .2*sin(val*.2);
}

vec3 conv3(vec3 v) {
	return vec3( conv(v.x), conv(v.y), conv(v.z) );
}

vec3 calcSheen() {
	vec2 uv2 = .5*conv(iPosition.x) + .5*conv(iPosition.z) + gl_TexCoord[0].st;
	vec3 uv3 = vec3(iPosition.xz, iPosition.y);
	
	float x, z;
	x = conv(uv2.x);
	z = conv(uv2.y);
	
	float f = .2*sin(20.*length( uv2 ));
	
	return vec3(f);
}

/*vec3 convColor(vec3 outCol) {
	return normalize(outCol.rgb + 2*conv3(iPosition.xyz) + vec3(gl_TexCoord[0].st, length(gl_TexCoord[0].st) ) );
}*/

vec3 mixColors(vec3 col1, vec3 col2, float amt) {
    float f = 1. - amt;
    return vec3(col1.r*f + col2.r*amt,
               col1.g*f + col2.g*amt,
               col1.b*f + col2.b*amt);
}

void main() {
	vec2 uv = gl_TexCoord[0].st;
		
 	vec4 texCol = texture2D(tex0,uv);
 	
 	
 	float r, g, b, v;
 	r = texCol.r;
 	g = texCol.g;
 	b = texCol.b;
 	v = value(texCol.rgb);

 	vec2 uvNorm = uv, uvPos = .05*length( texCol )*iPosition.xz; 
 	vec2 mUV;
 	
 	mUV = .5 - vec2(r, g) - vec2(b, v);
 		mUV += .8*uvNorm + .25*uvPos;
 		
 	// Adjust Normal to Wrap Around
 	vec2 mmUV = mUV-.5;
 	float dir, len, mL = .8*.5;
 	dir = atan(mmUV.y, mmUV.x);
 	len = length(mmUV);
 	while(len > mL)
 		len -= 2*mL;
 	mUV = .5 + len*vec2(cos(dir),sin(dir));
 		
 	vec4 metCol = texture2D(tex1,mUV);
 	
	vec3 outCol = mixColors(value3(texCol.rgb), metCol.rgb, .7);
	
	//outCol += value3(convColor(outCol));
 	
	gl_FragColor = vec4(outCol, texCol.a);
}