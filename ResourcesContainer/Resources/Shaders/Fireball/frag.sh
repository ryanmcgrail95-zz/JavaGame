uniform float iGlobalTime;
uniform sampler2D tex0;
uniform sampler2D tex1;
uniform vec4 outsideColor;
uniform vec4 insideColor;

void main() {
	float x = gl_TexCoord[0].x;
	float y = gl_TexCoord[0].y;
	
	vec4 colorTex = texture2D(tex0, vec2(x,y));
	
	float value = colorTex.r, alpha = colorTex.a;
		
	float mi = .6;
	float ma = min(1., mi+.4);
		
	vec4 outColor;
	outColor = vec4(outsideColor.rgb, 0.);
	
	if(value > mi) {
		if(value < ma) {
			outColor = vec4(outsideColor.rgb, 1.);
		}
		else {
			outColor = vec4(insideColor.rgb, 1.);
		}
	}
	
	gl_FragColor = outColor;
}