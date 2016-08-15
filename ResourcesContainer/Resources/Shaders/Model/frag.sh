uniform vec2 iResolution;
uniform float iGlobalTime, hasColor;
uniform sampler2D tex0, tex1;

varying vec4 vColor;
varying vec3 vNormal;

vec4 mix(vec4 col1, vec4 col2) {
	return vec4(col1.r * col2.r, col1.g * col2.g, col1.b * col2.b, col1.a * col2.a);
}

float dot(vec3 a, vec3 b) {
	return a.x*b.x + a.y*b.y + a.z*b.z;
}

vec3 normalize(vec3 v) {
	return v/length(v);
}

void main() {
	vec3 lightNorm = normalize(vec3(0,-1,-1));
	
	vec4 col = texture2D(tex0, 1. - vec2(abs(1 - mod(gl_TexCoord[0].s,2.0)), abs(1 - mod(gl_TexCoord[0].t,2.0)) ));
	vec4 mixed = (1.-hasColor) * col + hasColor * mix(col, vColor);
	
	gl_FragColor = vec4((dot(lightNorm,vNormal)*.5 + .5) * mixed.rgb, mixed.a);
}