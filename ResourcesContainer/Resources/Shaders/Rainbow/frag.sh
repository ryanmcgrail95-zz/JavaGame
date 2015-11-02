uniform vec2 iResolution;
uniform float iGlobalTime;
uniform sampler2D tex;

void main() {
	vec2 coords = normalize(gl_TexCoord[0].xy);
	vec2 uv = coords;//;gl_FragCoord.xy / iResolution;

 	vec4 texCol = texture2D(tex,gl_TexCoord[0].st);
    	float value = .21*texCol.r + .72*texCol.g + .07*texCol.b;

	vec3 rainbowCol = vec3(uv,.5+.5*sin(iGlobalTime*50./180.*3.14159));
	
	vec3 finalColor;
	//finalColor = value*rainbowCol.rgb + (1-value)*texCol.rgb;
	finalColor = .5*rainbowCol + .5*texCol.rgb;

	gl_FragColor = vec4(rainbowCol, 1.); //vec4(finalColor, texCol.a);
}