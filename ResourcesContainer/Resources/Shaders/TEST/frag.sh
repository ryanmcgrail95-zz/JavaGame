uniform vec2 iResolution;
uniform float iGlobalTime;

void main() {
	vec2 coords = normalize(gl_TexCoord[0].xy);

	vec2 uv = coords;//;gl_FragCoord.xy / iResolution;

	gl_FragColor = vec4(uv,0.5+0.5*sin(iGlobalTime),1.0);
}