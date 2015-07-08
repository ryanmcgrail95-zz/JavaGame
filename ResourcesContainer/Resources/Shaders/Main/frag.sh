uniform vec4 iColor;
uniform int textureBound;
uniform sampler2D tex;

void main() {

	/*if(textureBound == 1)
		gl_FragColor = texture2D(tex,gl_TexCoord[0].xy);
	else*/
		gl_FragColor = vec4(1.,1.,1.,1.); //iColor;
}