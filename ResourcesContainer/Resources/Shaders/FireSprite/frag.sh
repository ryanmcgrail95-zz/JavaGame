uniform float iGlobalTime;
uniform sampler2D tex0;
uniform sampler2D tex1;

float convAlpha(float alpha, float f) {
	return alpha == 0. ? 0. : alpha*f + 1.-f;
}
float fixAlpha(float alpha) {
	return 1. - (1. - alpha)*(1. - alpha);
}
float fixAlpha(float alpha, float f) {
	return convAlpha(fixAlpha(alpha), f);
}

float powAlpha(float alpha, float p) {
	return 1. - pow(1. - alpha, p);
}

void main() {
	float x = gl_TexCoord[0].x;
	float y = gl_TexCoord[0].y;
	
	float aY = (y*64 - iGlobalTime/360*2*50*64)/64;
	
	vec3 colorInnerFire = vec3(1., .3,.1),
		colorOuterFire = vec3(1.,1.,.2);
	vec3 colorAni = texture2D(tex0, vec2(x,aY)).rgb;
	vec3 colorMask = texture2D(tex1, vec2(x,y)).rgb;
	
		float value = colorAni.r;
		
		float maskAlpha, aniAlpha, alpha;
		maskAlpha = fixAlpha(colorMask.r,.3);
		aniAlpha = fixAlpha(colorAni.r,.7);
		
		float contrastMaskAlpha, contrastAniAlpha;
		contrastMaskAlpha = powAlpha(colorMask.r,7.5); //8
		contrastAniAlpha = powAlpha(colorAni.r,1.5);
		
		
		float strongAlpha = max(contrastMaskAlpha, contrastAniAlpha); //max(fixAlpha(colorAni.r), fixAlpha(colorMask.r));
			strongAlpha = fixAlpha(strongAlpha);

		// Combine These!!
		float alphaA = aniAlpha == 0. ? 0. : max(1.-fixAlpha(colorAni.r), powAlpha(colorMask.r,2)); //fixAlpha(colorMask.r)
			alphaA = 1. - alphaA;
		float alphaB = strongAlpha;
			alphaB = maskAlpha == 0. ? 0. : alphaB;	// Cut out Outside of Fire
			alphaB = alphaB < .9 ? 0. : alphaB;	// Cut out Invisible Parts
		//alphaA = maskAlpha == 0. ? 0. : maskAlpha*strongAlpha;
		
	//gl_FragColor = vec4(colorFire*colorMask,colorMask.r) + vec4(value*colorFire,colorMask.r);
	//gl_FragColor = vec4(value*colorInnerFire + (1.-value)*colorOuterFire,alpha);
	//gl_FragColor = vec4(colorOuterFire,alpha);
	
	vec3 color;
	color = alphaA*colorInnerFire + (1.-alphaA)*colorOuterFire;
	
	color -= .5*vec3(1.-contrastAniAlpha);
	
	//gl_FragColor = vec4(vec3(alphaA), 1.);
	gl_FragColor = vec4(color, alphaB);
}