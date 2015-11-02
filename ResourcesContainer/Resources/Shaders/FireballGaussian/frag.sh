uniform vec2 iResolution;
uniform float iGlobalTime;
uniform float iRadius;
uniform vec4 uColor;
uniform sampler2D tex;


void main() {    
    //gl_FragColor = texture2D(tex, gl_TexCoord[0].xy);
    //uColor.rgb, 
    
    vec4 A = texture2D(tex, gl_TexCoord[0].st);
	gl_FragColor = vec4(uColor.rgb, 3*A.r);
}