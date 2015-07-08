uniform vec2 iResolution;
uniform float iGlobalTime;
uniform sampler2D tex;

void main() {
	vec2 coords = normalize(gl_TexCoord[0].xy);
	vec2 uv = coords;//;gl_FragCoord.xy / iResolution;


 	vec4 color = texture2D(tex,gl_TexCoord[0].st);
    	float value = .21*color.r + .72*color.g + .07*color.b;

	vec3 col = vec3(uv,0.5+0.5*sin(iGlobalTime));

	gl_FragColor = vec4(value*col.rgb + (1-value)*color.rgb, color.a);
}